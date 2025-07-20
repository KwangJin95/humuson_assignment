package com.humuson.assignment.domain.order.external.service;

// 외부 시스템 비즈니스 로직 수행
public interface ExternalOrderService {
    
    // 외부 시스템별 주문 유효성 검증 및 저장
    void fetchAndSaveOrders(String systemName);

    // 외부 시스템별 주문 반환
    void sendOrdersToExternal(String systemName);

}
