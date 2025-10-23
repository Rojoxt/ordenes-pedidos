package com.example.orden_pedido.infrastructure.adapter.out.db2.mapper;

import com.example.orden_pedido.domain.aggregate.OrderDetail;
import com.example.orden_pedido.domain.aggregate.OrderSale;
import com.example.orden_pedido.infrastructure.adapter.out.db2.dto.LxcordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class LxcordMapper {

    // Mapeo principal que maneja la lista de detalles de la orden
    public List<LxcordDTO> toLxcordDtoList(OrderSale orderSale, String gui) {
        return orderSale.getOrderDetailList().stream()
                .map(orderDetail -> mapOrderDetailToDto(orderDetail, orderSale, gui))
                .collect(Collectors.toList());
    }

    @Mapping(target = "ordidprc", source = "gui")
    @Mapping(target = "ordpais", constant = "MX")
    @Mapping(target = "ordfeprc", expression = "java(getSystemDate())")
    @Mapping(target = "ordidqpe", expression = "java(\"MX\" + orderSale.getPurchaseOrder())")
    @Mapping(target = "ordtotli", source = "orderSale.totalLines")
    @Mapping(target = "ordhcust", source = "orderSale.codeClient")
    @Mapping(target = "ordhcpo", source = "orderSale.purchaseOrder")
    @Mapping(target = "ordhsdte", source = "orderSale.requestDate")
    @Mapping(target = "ordhrdte", source = "orderDetail.deliveryDatePostOrder")
    @Mapping(target = "ordhsstm", expression = "java(getSystemTime())")
    @Mapping(target = "ordhwhse", source = "orderSale.warehouse")
    @Mapping(target = "ordhcurr", expression = "java(orderDetail.getCurrency() != null ? orderDetail.getCurrency() : \" \")")
    @Mapping(target = "ordcomp", constant = "11")
    @Mapping(target = "ordlline", source = "orderDetail.lineNumber")
    @Mapping(target = "ordlprod", source = "orderDetail.material")
    @Mapping(target = "ordlqord", source = "orderDetail.requestedQuantity")
    @Mapping(target = "ordlrdte", source = "orderDetail.expirationDate")
    @Mapping(target = "ordlsdte", source = "orderDetail.releaseDate")
    @Mapping(target = "ordlnot4", source = "orderDetail.factoryOrder")
    @Mapping(target = "ordlnot5", source = "orderDetail.batchNumber")
    @Mapping(target = "ordhnot8", constant = "MX")
    @Mapping(target = "ordhnot9", source = "orderDetail.orderClass")
    @Mapping(target = "ordlnet", source = "orderDetail.price")
    @Mapping(target = "ordadi2", expression = "java(orderDetail.getBasePrice() != null ? orderDetail.getBasePrice().toString() : \" \")")
    @Mapping(target = "ordadi3", expression = "java(orderDetail.getUnitPrice() != null ? orderDetail.getUnitPrice().toString() : \" \")")
    public abstract LxcordDTO mapOrderDetailToDto(OrderDetail orderDetail, OrderSale orderSale, String gui);

    // Métodos para valores calculados
    protected int getSystemDate() {
        return Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    protected int getSystemTime() {
        return Integer.parseInt(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
    }


}