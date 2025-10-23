package com.example.orden_pedido.infrastructure.adapter.in.web.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class FactoryOrderAlreadyExistsException extends RuntimeException {
    private final List<String> existingOrderIds;
    public FactoryOrderAlreadyExistsException(String message, List<String> existingOrderIds) {
        super(message);
        this.existingOrderIds = existingOrderIds;
    }
}
