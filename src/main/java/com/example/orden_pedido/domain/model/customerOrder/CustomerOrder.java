package com.example.orden_pedido.domain.model.customerOrder;


import com.example.orden_pedido.domain.aggregate.Material;
import com.example.orden_pedido.domain.aggregate.OrderSale;

import java.util.List;

public class CustomerOrder {
    private final OrderSale orderSale;
    private final List<Material> materials;

    public CustomerOrder(OrderSale orderSale, List<Material> materials) {
        this.orderSale = orderSale;
        this.materials = materials;
    }

    public OrderSale getOrderSale() {
        return orderSale;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
