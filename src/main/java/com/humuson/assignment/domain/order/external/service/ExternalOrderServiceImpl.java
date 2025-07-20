package com.humuson.assignment.domain.order.external.service;

import com.humuson.assignment.config.ExternalSystemProperties;
import com.humuson.assignment.domain.order.dto.OrderDTO;
import com.humuson.assignment.domain.order.external.exception.OrderSyncException;
import com.humuson.assignment.domain.order.service.OrderService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class ExternalOrderServiceImpl implements ExternalOrderService {
    
    // 외부 시스템별 설정 정보
    private final ExternalSystemProperties externalSystemProperties;

    // 내부 시스템 주문 관련 비즈니스 로직 담당
    private final OrderService orderService;

    // 외부 시스템
    private final ExternalOrderClient externalOrderClient;

    // 주문 데이터(OrderDTO) 유효성 검증
    private final Validator validator;

    public ExternalOrderServiceImpl(ExternalOrderClient externalOrderClient,
                                    OrderService orderService,
                                    ExternalSystemProperties externalSystemProperties,
                                    Validator validator) {

        this.externalSystemProperties = externalSystemProperties;
        this.externalOrderClient = externalOrderClient;
        this.orderService = orderService;
        this.validator = validator;

    }

    // 외부 시스템별 주문 요청 및 저장
    @Override
    public void fetchAndSaveOrders(String systemName) {
        
        // 외부 시스템 설정 정보
        ExternalSystemProperties.SystemDetail systemDetail =
                externalSystemProperties.getSystems().get(systemName);

        // 외부 시스템 이름에 해당하는 정보 없음
        if (systemDetail == null) {
            throw new IllegalArgumentException("[시스템 설정 정보 없음] - system : " + systemName);
        }

        // 외부 시스템 주문 요청 API 엔드포인트
        String url = systemDetail.getFetchUrl();

        try {

            // 외부 시스템에 요청한 주문 목록
            List<OrderDTO> orders = externalOrderClient.fetchOrders(url);

            log.info("[외부][주문 요청 성공] - system : {} / count : {}", systemName,
                                                                               orders.size());

            // 주문 저장 성공 개수
            int successCount = 0;
            
            for (OrderDTO order : orders) {
                try {
                    // 주문 데이터(OrderDTO) 유효성 검증
                    validateOrder(order);
                    
                    // 메모리에 주문 저장
                    orderService.saveOrder(systemName, order);
                    
                    successCount++;

                } catch (Exception e) {
                    log.error("[내부][주문 저장 실패] - system : {} / orderId : {} / error : {}", systemName,
                                                                                                       order.getOrderId(),
                                                                                                       e.getMessage());
                }
            }
            
            log.info("[내부][주문 저장 완료] - success : {}/{}", successCount,
                                                                      orders.size());

        } catch (Exception e) {
            log.error("[외부][주문 요청 실패] - system : {} / url : {} / error : {}", systemName,
                                                                                           url,
                                                                                           e.getMessage());
            throw e;
        }
    }

    // 외부 시스템별 주문 반환
    @Override
    public void sendOrdersToExternal(String systemName) {
        
        // 외부 시스템 설정 정보
        ExternalSystemProperties.SystemDetail systemDetail =
                externalSystemProperties
                        .getSystems()
                        .get(systemName);

        // 외부 시스템 이름에 해당하는 정보 없음
        if (systemDetail == null) {
            throw new IllegalArgumentException("[시스템 설정 정보 없음] - system : " + systemName);
        }

        // 외부 시스템 주문 반환 API 엔드포인트
        String url = systemDetail.getSendUrl();
        
        try {
            // 외부 시스템에 반환할 주문 목록
            List<OrderDTO> ordersToSend = orderService.getOrders(systemName);

            // 조회된 주문이 없으면 예외
            if (ordersToSend.isEmpty()) {
                throw new IllegalArgumentException("[내부][주문 정보 없음] - system : " + systemName);
            }

            // 외부 시스템으로 주문 반환
            externalOrderClient.sendOrders(url,
                                           ordersToSend);

        } catch (Exception e) {
            log.error("[외부][주문 반환 실패] - system: {} / url : {} / error : {}", systemName,
                                                                                          url,
                                                                                          e.getMessage());
            throw e;
        }
    }

    // 주문 정보 유효성 검증
    private void validateOrder(OrderDTO order) {

        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(order);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("[외부][유효하지 않은 주문 정보]");
            for (ConstraintViolation<OrderDTO> violation : violations) {
                sb.append("[field : ").append(violation.getPropertyPath())
                  .append(" -> ").append(violation.getMessage())
                  .append("]");
            }
            throw new OrderSyncException(sb.toString());
        }
    }

}
