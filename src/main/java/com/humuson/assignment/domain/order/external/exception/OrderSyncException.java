package com.humuson.assignment.domain.order.external.exception;

public class OrderSyncException extends RuntimeException {

    public OrderSyncException(String message) {
        super(message);
    }

    public OrderSyncException(String message, Throwable cause) {
        super(message, cause);
    }

}