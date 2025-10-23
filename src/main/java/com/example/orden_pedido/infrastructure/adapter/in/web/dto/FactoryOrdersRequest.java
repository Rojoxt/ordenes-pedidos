package com.example.orden_pedido.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FactoryOrdersRequest(
        @JsonProperty("Organización")
        @NotNull(message = "no puede ser nula o verifique el campo si tiene tilde")
        @Valid
        Organization organization,

        @JsonProperty("PedidosVenta")
        List< @Valid SalesOrder> salesOrders,

        @JsonProperty("OrdenFabrica")
        List<@Valid FactoryOrder> factoryOrders
) {
    public record Organization(
            @JsonProperty("Cod_Organización")
            @NotNull(message = "no puede ser nula o verifique el campo si tiene tilde")
            String organizationCode
    ) {}

    public record SalesOrder(
            @JsonProperty("Orden_compra")
            String purchaseOrder
    ) {}

    public record FactoryOrder(
            @JsonProperty("Orden_fabrica")
            String factoryOrder
    ) {}
}
