package com.humuson.assignment.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    // 주문 ID
    @NotBlank
    private String orderId;

    // 고객명
    @NotBlank
    private String customerName;

    // 주문 날짜
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    // 주문 상태
    @NotNull
    private OrderStatus orderStatus;

}
