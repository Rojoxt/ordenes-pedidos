package com.example.orden_pedido.infrastructure.adapter.out.adb.mapper;

import com.example.orden_pedido.domain.aggregate.OrderDetail;
import com.example.orden_pedido.domain.aggregate.OrderSale;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderDetailEntity;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderDetailId;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderHeaderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",implementationName = "CustomerOrderEntityMapperImpl")
public abstract class CustomerOrderMapper {

    // --- Mapeo de la Cabecera ---
    @Mapping(target = "id.organizationCode", source = "organizationCode")
    @Mapping(target = "id.purchaseOrder", source = "purchaseOrder")
    @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "clientCode", source = "codeClient")
    // Los siguientes campos se mapean automáticamente por tener el mismo nombre:
    // - totalLines
    // - requestDate
    // - currency
    // - warehouse
    // - clientCode
    public abstract CustomerOrderHeaderEntity toHeaderEntity(OrderSale orderSale);

    // --- Mapeo del Detalle ---
    public List<CustomerOrderDetailEntity> toDetailEntityList(OrderSale orderSale) {
        return orderSale.getOrderDetailList().stream()
                .map(orderDetail -> mapDetail(orderDetail, orderSale))
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", expression = "java(mapDetailId(orderDetail, orderSale))")
    @Mapping(target = "requestedQuantity", source = "orderDetail.requestedQuantity")
    @Mapping(target = "shippingDate", source = "orderDetail.releaseDate")
    @Mapping(target = "requiredDate", source = "orderDetail.deliveryDatePostOrder")
    @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "factoryOrder", source = "orderDetail.factoryOrder")
    @Mapping(target = "batchNumber", source = "orderDetail.batchNumber")
    @Mapping(target = "productCode", source = "orderDetail.material")
    @Mapping(target = "orderClass", source = "orderDetail.orderClass")
    //@Mapping(target = "unitOfMeasure", expression = "orderDetail.unitOfMeasure")
    public abstract CustomerOrderDetailEntity mapDetail(OrderDetail orderDetail, OrderSale orderSale);

    protected CustomerOrderDetailId mapDetailId(OrderDetail orderDetail, OrderSale orderSale) {
        return new CustomerOrderDetailId(orderSale.getPurchaseOrder(), orderDetail.getLineNumber());
    }
}
