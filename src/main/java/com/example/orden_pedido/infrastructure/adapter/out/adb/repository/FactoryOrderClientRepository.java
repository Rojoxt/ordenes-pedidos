package com.example.orden_pedido.infrastructure.adapter.out.adb.repository;

import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.FactoryOrderClientEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FactoryOrderClientRepository extends CrudRepository<FactoryOrderClientEntity,String> {
    List<FactoryOrderClientEntity> findAllById(Iterable<String> ids);
}
