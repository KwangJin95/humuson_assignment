package com.humuson.assignment.domain.order.external.service;

import com.humuson.assignment.domain.order.dto.OrderDTO;

import java.util.List;

// 외부 시스템 연결 및 통신
public interface ExternalOrderClient {
    
    // 외부 시스템 주문 요청(GET 방식)
    List<OrderDTO> fetchOrders(String url);

    // 외부 시스템 주문 반환(POST 방식)
    void sendOrders(String url,
                    List<OrderDTO> orders);

}
