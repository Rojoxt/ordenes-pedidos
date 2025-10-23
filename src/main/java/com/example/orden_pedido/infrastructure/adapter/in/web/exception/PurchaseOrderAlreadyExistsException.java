package com.example.orden_pedido.infrastructure.adapter.in.web.exception;

public class PurchaseOrderAlreadyExistsException extends RuntimeException {
    public PurchaseOrderAlreadyExistsException(String purchaseOrder) {
      super(String.format("La Orden de Compra: %s ya existe",purchaseOrder));
    }
}
