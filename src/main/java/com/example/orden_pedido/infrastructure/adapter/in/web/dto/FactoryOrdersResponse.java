package com.example.orden_pedido.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FactoryOrdersResponse(
        @JsonProperty("PedidosVenta")
        List<SalesOrderResponse> salesOrders,

        @JsonProperty("OrdenFabrica")
        List<FactoryOrderResponse> factoryOrders
) {
    public record SalesOrderResponse(
            @JsonProperty("Status")
            String status,

            @JsonProperty("Message")
            String message,

            @JsonProperty("Orden_compra")
            String purchaseOrder,

            @JsonProperty("Orden_ped_yob")
            String yobPurchaseOrder
    ) {}

    public record FactoryOrderResponse(
            @JsonProperty("Status")
            String status,

            @JsonProperty("Message")
            String message,

            @JsonProperty("Orden_fabrica")
            String factoryOrder,

            @JsonProperty("Orden_fab_yob")
            String yobFactoryOrder
    ) {}
}
