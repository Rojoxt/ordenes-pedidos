package com.example.orden_pedido.infrastructure.adapter.out.adb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "T_PSCM_ORDEN_FABRICA_CLIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FactoryOrderClientEntity {

    // Identificador Principal (Primary Key)
    @Id
    @Column(name = "ORDEN_FABRICA", length = 20)
    private String factoryOrderId;

    @Column(name = "CANTIDAD", precision = 11, scale = 3)
    private BigDecimal quantity; // NUMBER(11,3) -> Se usa BigDecimal para precisión con decimales

    // Las fechas se mapean a String/Integer porque en la BD están como NUMBER(8,0) (ej: 20251231)
    // Se podrían convertir a java.time.LocalDate con un conversor, pero se mantiene el tipo base por ahora.
    @Column(name = "FECHA_VENCIMIENTO", length = 8)
    private String expirationDate; // NUMBER(8,0)

    @Column(name = "FECHA_ENVIO", length = 8)
    private String shipmentDate; // NUMBER(8,0)

    @Column(name = "CLASE_ORDEN", length = 20)
    private String orderClass;

    @Column(name = "NRO_LOTE_PT", length = 20)
    private String productionBatchNumber;

    @Column(name = "CODIGO_PRODUCTO", length = 35)
    private String productCode;

    @Column(name = "UNIDAD_MEDIDA", length = 5)
    private String measureUnit;

}
