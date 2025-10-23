package com.example.orden_pedido.infrastructure.adapter.out.adb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrderDetailId implements Serializable {

    @Column(name = "NRO_OC", length = 20, nullable = false)
    private String orderNumber;

    @Column(name = "LINEA", nullable = false)
    private Integer lineNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerOrderDetailId)) return false;
        CustomerOrderDetailId that = (CustomerOrderDetailId) o;
        return Objects.equals(orderNumber, that.orderNumber) &&
                Objects.equals(lineNumber, that.lineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, lineNumber);
    }
}
