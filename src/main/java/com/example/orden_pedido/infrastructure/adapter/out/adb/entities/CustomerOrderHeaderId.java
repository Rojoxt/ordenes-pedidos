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
public class CustomerOrderHeaderId implements Serializable {

    @Column(name = "COD_ORGANIZACION", length = 4, nullable = false)
    private String organizationCode;

    @Column(name = "NRO_OC", length = 20, nullable = false)
    private String purchaseOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerOrderHeaderId)) return false;
        CustomerOrderHeaderId that = (CustomerOrderHeaderId) o;
        return Objects.equals(organizationCode, that.organizationCode) &&
                Objects.equals(purchaseOrder, that.purchaseOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationCode, purchaseOrder);
    }
}
