package com.example.orden_pedido.infrastructure.adapter.in.web.mapper;

import com.example.orden_pedido.domain.model.order.Orders;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdersResponseMapper {

    // Map principal
    FactoryOrdersResponse toResponse(Orders orders);

    // Map de subclases (MapStruct infiere automáticamente si los nombres coinciden)
    FactoryOrdersResponse.SalesOrderResponse toSalesOrderResponse(Orders.SalesOrder salesOrder);
    FactoryOrdersResponse.FactoryOrderResponse toFactoryOrderResponse(Orders.FactoryOrder factoryOrder);
}
