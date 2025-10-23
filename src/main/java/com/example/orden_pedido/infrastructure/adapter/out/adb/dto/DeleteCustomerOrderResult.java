package com.example.orden_pedido.infrastructure.adapter.out.adb.dto;

public record DeleteCustomerOrderResult(
        String status,
        String message,
        int headerRows,
        int detailRows
) {}
