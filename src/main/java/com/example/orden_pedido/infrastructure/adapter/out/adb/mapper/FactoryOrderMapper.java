package com.example.orden_pedido.infrastructure.adapter.out.adb.mapper;

import com.example.orden_pedido.domain.aggregate.OrderDetail;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.FactoryOrderClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",implementationName = "FactoryOrderEntityMapperImpl")
public interface FactoryOrderMapper {
    @Mapping(target = "factoryOrderId", source = "factoryOrder")
    @Mapping(target = "quantity", source = "requestedQuantity")
    // Mapeo de Integer a String. MapStruct lo maneja automáticamente con Integer.toString()
    @Mapping(target = "expirationDate", source = "expirationDate")
    // Mapeamos la fecha de entrega a la fecha de envío (FECHA_ENVIO)
    @Mapping(target = "shipmentDate", source = "deliveryDatePostOrder")
    @Mapping(target = "orderClass", source = "orderClass")
    @Mapping(target = "productionBatchNumber", source = "batchNumber")
    @Mapping(target = "productCode", source = "material") // material -> CODIGO_PRODUCTO
    @Mapping(target = "measureUnit", source = "unitOfMeasure")
    FactoryOrderClientEntity toEntity(OrderDetail orderDetail);

    List<FactoryOrderClientEntity> toEntityList(List<OrderDetail> orderDetails);
}
