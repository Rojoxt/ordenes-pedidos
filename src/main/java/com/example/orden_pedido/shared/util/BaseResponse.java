package com.example.orden_pedido.shared.util;


public record BaseResponse<T> (
        String message,
        T data
){}
