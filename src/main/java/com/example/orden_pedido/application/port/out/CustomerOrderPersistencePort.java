package com.example.orden_pedido.application.port.out;


import com.example.orden_pedido.domain.model.customerOrder.CustomerOrder;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.CustomerOrderRequestDTO;

import java.util.List;

public interface CustomerOrderPersistencePort {
    void registerClientOrder(CustomerOrder customerOrder);
    boolean existsByPurchaseOrder(String purchaseOrder);
    void registerFactoryOrder(CustomerOrder customerOrder);
    List<String> existingFactoryOrders(CustomerOrderRequestDTO customerOrder);
}
