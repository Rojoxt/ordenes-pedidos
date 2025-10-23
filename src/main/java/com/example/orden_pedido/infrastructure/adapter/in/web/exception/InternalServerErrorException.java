package com.example.orden_pedido.infrastructure.adapter.in.web.exception;

public class InternalServerErrorException extends RuntimeException {


    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(String message) {
        super(message);
    }
    public InternalServerErrorException(Throwable cause) {
        super("Ocurrió un error interno en el servidor", cause);
    }

}
