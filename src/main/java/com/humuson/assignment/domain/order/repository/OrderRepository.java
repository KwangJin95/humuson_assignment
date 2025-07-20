package com.humuson.assignment.domain.order.repository;

import com.humuson.assignment.domain.order.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

// 주문 데이터 접근을 담당
public interface OrderRepository {

    // 외부 시스템별 주문 저장
    void save(String systemName,
              OrderDTO order);
    
    // 외부 시스템별 주문 반환(List<OrderDTO>)
    List<OrderDTO> findBySystem(String systemName);
    
    // 외부 시스템별 주문 조회(주문 ID)
    Optional<OrderDTO> findById(String systemName,
                                String orderId);

}
