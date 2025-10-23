package com.example.orden_pedido.infrastructure.adapter.in.web.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ResourceValidationException extends RuntimeException {

    private final List<String> errors;

    // Constructor normal con mensaje y lista
    public ResourceValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    // Constructor con ConstraintViolation
    public <T> ResourceValidationException(String resourceName, Set<ConstraintViolation<T>> violations) {
        super(String.format("Not all constraints satisfied for %s", resourceName));
        this.errors = violations.stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.toList());
    }

    public List<String> getErrors() {
        return errors;
    }
}
