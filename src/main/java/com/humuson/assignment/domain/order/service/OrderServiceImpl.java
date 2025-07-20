package com.humuson.assignment.domain.order.service;

import com.humuson.assignment.domain.order.dto.OrderDTO;
import com.humuson.assignment.domain.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// 주문 관련 비즈니스 로직 담당
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    // 메모리 기반 주문 데이터 접근 repository
    public OrderServiceImpl(@Qualifier("inMemoryOrderRepository")
                            OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 외부 시스템별 주문 저장
    @Override
    public void saveOrder(String systemName,
                          OrderDTO order) {

        orderRepository.save(systemName, order);

    }

    // 외부 시스템별 주문 반환(List<OrderDTO>)
    @Override
    public List<OrderDTO> getOrders(String systemName) {

        return orderRepository.findBySystem(systemName);

    }

    // 외부 시스템별 주문 조회(주문 ID)
    @Override
    public Optional<OrderDTO> getOrderById(String systemName,
                                           String orderId) {

        return orderRepository.findById(systemName,
                                        orderId);

    }
}
