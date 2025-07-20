package com.humuson.assignment.domain.order.service;

import com.humuson.assignment.domain.order.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

// 주문 관련 비즈니스 로직 담당
public interface OrderService {

    // 외부 시스템별 주문 저장
    void saveOrder(String systemName,
                   OrderDTO order);

    // 외부 시스템별 주문 반환(List<OrderDTO>)
    List<OrderDTO> getOrders(String systemName);

    // 외부 시스템별 주문 조회(주문 ID)
    Optional<OrderDTO> getOrderById(String systemName,
                                    String orderId);

}
