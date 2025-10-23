package com.example.orden_pedido.infrastructure.adapter.out.adb.entities;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_PSCM_PEDIDOS_CLIENT_CAB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrderHeaderEntity {

    @EmbeddedId
    private CustomerOrderHeaderId id;

    @Column(name = "NRO_LINEAS", nullable = false)
    private Integer totalLines;

    @Column(name = "FECHA_SOLICITUD")
    private Integer requestDate;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime registrationDate;

    @Column(name = "ALMACEN", length = 10)
    private String warehouse;

    @Column(name = "COD_CLIENTE", length = 20)
    private String clientCode;

    @Column(name = "NRO_PEDIDO_YOBEL")
    @Builder.Default
    private String orderYobelNumber =" ";

}
