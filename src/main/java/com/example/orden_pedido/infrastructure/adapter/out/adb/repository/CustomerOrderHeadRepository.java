package com.example.orden_pedido.infrastructure.adapter.out.adb.repository;

import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderHeaderEntity;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderHeaderId;
import org.springframework.data.repository.CrudRepository;



public interface CustomerOrderHeadRepository extends CrudRepository<CustomerOrderHeaderEntity, CustomerOrderHeaderId> {
    boolean existsById_PurchaseOrder(String purchaseOrder);
}
