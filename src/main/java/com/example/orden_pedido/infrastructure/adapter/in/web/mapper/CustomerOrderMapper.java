package com.example.orden_pedido.infrastructure.adapter.in.web.mapper;

import com.example.orden_pedido.domain.aggregate.Component;
import com.example.orden_pedido.domain.aggregate.Material;
import com.example.orden_pedido.domain.aggregate.OrderDetail;
import com.example.orden_pedido.domain.aggregate.OrderSale;
import com.example.orden_pedido.domain.model.customerOrder.CustomerOrder;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.CustomerOrderRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",implementationName = "CustomerOrderDtoMapperImpl")
public interface CustomerOrderMapper {

    /**
     * Mapea el DTO de solicitud de la capa web al objeto de dominio CustomerOrder.
     */
    @Mapping(source = "orderSale", target = "orderSale")
    @Mapping(source = "materials", target = "materials")
    CustomerOrder toDomain(CustomerOrderRequestDTO dto);

    /**
     * Mapea OrderSaleDTO a OrderSale.
     * Los campos 'codeClient', 'warehouse' y 'currency' se ignoran
     * ya que serán enriquecidos más tarde por el servicio.
     */
    @Mapping(target = "codeClient", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    OrderSale toOrderSale(CustomerOrderRequestDTO.OrderSaleDTO dto);

    /**
     * Mapea OrderDetailDTO a OrderDetail.
     */
    OrderDetail toOrderDetail(CustomerOrderRequestDTO.OrderDetailDTO dto);
    List<OrderDetail> toOrderDetailList(List<CustomerOrderRequestDTO.OrderDetailDTO> dtoList);

    /**
     * Mapea MaterialDTO a Material.
     */
    Material toMaterial(CustomerOrderRequestDTO.MaterialDTO dto);
    List<Material> toMaterialList(List<CustomerOrderRequestDTO.MaterialDTO> dtoList);

    /**
     * Mapea ComponentDTO a Component.
     */
    Component toComponent(CustomerOrderRequestDTO.ComponentDTO dto);
    List<Component> toComponentList(List<CustomerOrderRequestDTO.ComponentDTO> dtoList);
}
