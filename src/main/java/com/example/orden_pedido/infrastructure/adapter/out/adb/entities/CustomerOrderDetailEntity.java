package com.example.orden_pedido.infrastructure.adapter.out.adb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "T_PSCM_PEDIDOS_CLIENT_DET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class CustomerOrderDetailEntity {

    @EmbeddedId
    private CustomerOrderDetailId id;

    @Column(name = "CANTIDAD")
    private double requestedQuantity;

    @Column(name = "FECHA_ENVIO")
    private Integer shippingDate;

    @Column(name = "FECHA_REQUERIDA")
    private Integer requiredDate;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime registrationDate;

    @Column(name = "ORDEN_FABRICA", length = 20)
    private String factoryOrder;

    @Column(name = "NRO_LOTE_PT", length = 25)
    private String batchNumber;

    @Column(name = "COD_PRODUCTO", length = 35)
    private String productCode;

    @Column(name = "UNIDAD_MEDIDA", length = 4)
    private String unitOfMeasure;

    @Column(name = "MONEDA", length = 3)
    private String currency;

    @Column(name = "CLASE_ORDEN")
    private String orderClass;
}
