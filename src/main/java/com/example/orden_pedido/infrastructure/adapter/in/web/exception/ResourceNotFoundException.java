package com.example.orden_pedido.infrastructure.adapter.in.web.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException() {
        super("Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        super(String.format("%s with id %d not found.", resourceName, resourceId));
    }
}
