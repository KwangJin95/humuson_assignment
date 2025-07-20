package com.humuson.assignment.domain.order.repository;

import com.humuson.assignment.domain.order.dto.OrderDTO;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// 메모리 기반 주문 데이터 접근을 담당
@Repository("inMemoryOrderRepository")
public class InMemoryOrderRepository implements OrderRepository {

    // 외부 시스템별 주문 목록 Map
    private final Map<String, List<OrderDTO>> store = new ConcurrentHashMap<>();

    // 외부 시스템별 주문 저장
    public void save(String systemName,
                     OrderDTO order) {

        List<OrderDTO> orders = store.computeIfAbsent(systemName,
                                                      k -> new ArrayList<>());

        // 기존 주문 제거 후 새로 추가 (연동 특성 고려 업데이트 개념)
        orders.removeIf(existingOrder -> existingOrder.getOrderId().equals(order.getOrderId()));

        orders.add(order);
    }

    // 외부 시스템별 주문 반환(List<OrderDTO>)
    public List<OrderDTO> findBySystem(String systemName) {

        return store.getOrDefault(systemName,
                                  Collections.emptyList());

    }

    // 외부 시스템별 주문 조회(주문 ID)
    public Optional<OrderDTO> findById(String systemName,
                                       String orderId) {

        return store.getOrDefault(systemName,
                                  Collections.emptyList())
                .stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst();
    }

}
