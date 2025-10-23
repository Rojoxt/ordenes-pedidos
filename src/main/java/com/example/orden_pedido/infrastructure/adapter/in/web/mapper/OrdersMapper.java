package com.example.orden_pedido.infrastructure.adapter.in.web.mapper;

import com.example.orden_pedido.domain.model.order.Orders;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdersMapper {
    @Mapping(target = "salesOrders", source = "salesOrders")
    @Mapping(target = "factoryOrders", source = "factoryOrders")
    Orders toDomain(FactoryOrdersRequest dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "yobPurchaseOrder", ignore = true)
    Orders.SalesOrder toDomain(FactoryOrdersRequest.SalesOrder dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "message", ignore = true)
    @Mapping(target = "yobFactoryOrder", ignore = true)
    Orders.FactoryOrder toDomain(FactoryOrdersRequest.FactoryOrder dto);

    List<Orders.SalesOrder> toDomainSalesOrders(List<FactoryOrdersRequest.SalesOrder> dto);
    List<Orders.FactoryOrder> toDomainFactoryOrders(List<FactoryOrdersRequest.FactoryOrder> dto);
}
