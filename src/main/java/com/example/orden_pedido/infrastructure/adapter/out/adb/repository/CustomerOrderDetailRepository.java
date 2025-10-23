package com.example.orden_pedido.infrastructure.adapter.out.adb.repository;

import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderDetailEntity;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.CustomerOrderDetailId;
import org.springframework.data.repository.CrudRepository;

public interface CustomerOrderDetailRepository extends CrudRepository<CustomerOrderDetailEntity, CustomerOrderDetailId> {
}
