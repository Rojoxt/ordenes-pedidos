package com.example.orden_pedido.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CustomerOrderRequestDTO(
        @JsonProperty("PedidoVenta")
        @Valid @NotNull OrderSaleDTO orderSale,

        @JsonProperty("ListaMaterial")
        @Valid @Size(min = 1) List<MaterialDTO> materials
) {
    public record OrderSaleDTO(

            @JsonProperty("Cod_organizacion")
            @NotBlank @Size(max = 10)
            String organizationCode,

            @JsonProperty("Orden_compra")
            String purchaseOrder,

            @JsonProperty("Fecha_solicitud")// formato AAAAMMDD
            Integer requestDate,

            @JsonProperty("Total_lineas")
            @NotNull @Min(1)
            Integer totalLines,

            @JsonProperty("Lineas")
            @NotNull @Size(min = 1)
            @Valid List<OrderDetailDTO> orderDetailList
    ) {}
    public record OrderDetailDTO(

            @JsonProperty("Nro_linea")
            @NotNull @Min(1)
            Integer lineNumber,

            @JsonProperty("Orden_fabrica")
            @Size(max = 15)
            String factoryOrder,

            @JsonProperty("Clase_orden")
            @NotBlank @Size(max = 35)
            String orderClass,

            @JsonProperty("Material")
            @NotBlank @Size(max = 35)
            String material,

            @JsonProperty("Cantidad_Solicitud")
            @NotNull(message = "es obligatorio")
            @DecimalMin(value = "0.001", message = "debe ser mayor o igual a 0.001")
            @Digits(integer = 8, fraction = 3, message = "solo puede tener hasta 8 enteros y 3 decimales")
            BigDecimal requestedQuantity,

            @JsonProperty("Precio")
            BigDecimal price,

            @JsonProperty("Base_precio")
            Integer basePrice,

            @JsonProperty("Precio_Uni")
            BigDecimal uniPrice,

            @JsonProperty("Moneda")
            String currency,

            @JsonProperty("Unidad_medida")
            @NotBlank @Size(max = 4)
            String unitOfMeasure,

            @JsonProperty("Fecha_vencimiento")
            @NotNull @Digits(integer = 8, fraction = 0)
            Integer expirationDate,

            @JsonProperty("Fecha_lanzamiento")
            @NotNull @Digits(integer = 8, fraction = 0)
            Integer releaseDate,

            @JsonProperty("Fecha_entrega_pos_ped")
            Integer deliveryDatePostOrder,

            @JsonProperty("Nro_lote")
            @NotBlank @Size(max = 25)
            String batchNumber
    ) {}
    public record MaterialDTO(

            @JsonProperty("Codigo_padre")
            @NotBlank @Size(max = 35)
            String parentCode,

            @JsonProperty("Linea_orden")
            @NotNull @Min(1)
            Integer orderLine,

            @JsonProperty("Cant_base_fabricacion")
            @NotNull @Min(1)
            Integer manufacturingBaseQuantity,

            @JsonProperty("Unidad_medida_cant_base")
            @NotBlank @Size(max = 4)
            String baseQuantityUnit,

            @JsonProperty("Componentes")
            @NotNull @Size(min = 1)
            @Valid List<ComponentDTO> components
    ) {}
    public record ComponentDTO(

            @JsonProperty("Componente")
            @NotBlank @Size(max = 35)
            String component,

            @JsonProperty("Cantidad_requerida")
            @NotNull @DecimalMin("0.000001")
            Double requiredQuantity,

            @JsonProperty("Unidad_medida")
            @NotBlank @Size(max = 4)
            String unitOfMeasure,

            @JsonProperty("Factor_conversion")
            Double conversionFactor,

            @JsonProperty("Fecha_vigencia_desde")
            @NotNull @Digits(integer = 8, fraction = 0)
            Integer validityStartDate,

            @JsonProperty("Fecha_vigencia_hasta")
            @NotNull @Digits(integer = 8, fraction = 0)
            Integer validityEndDate,

            @JsonProperty("Factor_desperdicio")
            Double wasteFactor
    ) {}
}

