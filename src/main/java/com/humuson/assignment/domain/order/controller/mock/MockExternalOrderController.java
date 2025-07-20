package com.humuson.assignment.domain.order.controller.mock;

import com.humuson.assignment.domain.order.dto.OrderDTO;
import com.humuson.assignment.domain.order.dto.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/mock-external/orders")
@Slf4j
public class MockExternalOrderController {

    private final List<OrderDTO> mockOrders = new ArrayList<>();

    public MockExternalOrderController() {
        // 초기 샘플 데이터 3건 등록
        mockOrders.add(OrderDTO.builder()
                .orderId("1001")
                .customerName("김민수")
                .orderDate(LocalDateTime.now().minusDays(1))
                .orderStatus(OrderStatus.PENDING)
                .build());

        mockOrders.add(OrderDTO.builder()
                .orderId("1002")
                .customerName("이영호")
                .orderDate(LocalDateTime.now().minusDays(2))
                .orderStatus(OrderStatus.SHIPPING)
                .build());

        mockOrders.add(OrderDTO.builder()
                .orderId("1003")
                .customerName("장경철")
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.COMPLETED)
                .build());

        // ---------------------------------------------
        // 실패 케이스
        mockOrders.add(OrderDTO.builder()
                .orderId("9999")
                .customerName("") // @NotBlank 위반
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .build());
    }

    @GetMapping
    public List<OrderDTO> fetchOrders() {
        log.info("-------------------------------------------------------------------------------------------------");
        log.info("[Mock API][주문 요청] - count : {}", mockOrders.size());
        return mockOrders;
    }

    @PostMapping
    public void receiveOrders(@RequestBody List<OrderDTO> orders) {
        log.info("-------------------------------------------------------------------------------------------------");
        log.info("[Mock API][주문 반환] - count : {}", orders.size());
        for (OrderDTO orderDTO : orders) {
            log.info(orderDTO.toString());
        }
        mockOrders.clear();
        mockOrders.addAll(orders);
    }
}
