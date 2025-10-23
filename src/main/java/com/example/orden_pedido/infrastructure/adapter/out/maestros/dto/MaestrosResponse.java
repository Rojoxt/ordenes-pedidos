package com.example.orden_pedido.infrastructure.adapter.out.maestros.dto;

import java.util.List;
import java.util.Map;

public record MaestrosResponse(
        String message,
        List<Map<String, Object>> object
) {}
