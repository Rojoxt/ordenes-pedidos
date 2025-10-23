package com.example.orden_pedido.infrastructure.adapter.out.adb;


import com.example.orden_pedido.application.port.out.CustomerOrderPersistencePort;
import com.example.orden_pedido.domain.aggregate.OrderDetail;
import com.example.orden_pedido.domain.model.customerOrder.CustomerOrder;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.CustomerOrderRequestDTO;
import com.example.orden_pedido.infrastructure.adapter.out.adb.entities.FactoryOrderClientEntity;
import com.example.orden_pedido.infrastructure.adapter.out.adb.mapper.CustomerOrderMapper;
import com.example.orden_pedido.infrastructure.adapter.out.adb.mapper.FactoryOrderMapper;
import com.example.orden_pedido.infrastructure.adapter.out.adb.repository.CustomerOrderDetailRepository;
import com.example.orden_pedido.infrastructure.adapter.out.adb.repository.CustomerOrderHeadRepository;
import com.example.orden_pedido.infrastructure.adapter.out.adb.repository.FactoryOrderClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerOrderPersistenceAdapter implements CustomerOrderPersistencePort {
    private final CustomerOrderHeadRepository customerOrderHeadRepository;
    private final CustomerOrderDetailRepository customerOrderDetailRepository;
    private final FactoryOrderClientRepository factoryOrderClientRepository;
    private final CustomerOrderMapper mapper;
    private final FactoryOrderMapper factoryOrderMapper;


    //@Async("taskExecutor")
    @Transactional(transactionManager = "adbTransactionManager")
    @Override
    public void registerClientOrder(CustomerOrder customerOrder) {
        String purchaseOrder = customerOrder.getOrderSale().getPurchaseOrder();
        long start = System.currentTimeMillis();
        //log.info("Iniciando el registro de orden pedido para PurchaseOrder {}", purchaseOrder);

        try {
            // 1. Mapeo y guardado de la cabecera
            var header = mapper.toHeaderEntity(customerOrder.getOrderSale());
            customerOrderHeadRepository.save(header);

            // 2. Mapeo y guardado de los detalles
            var details = mapper.toDetailEntityList(customerOrder.getOrderSale());
            customerOrderDetailRepository.saveAll(details);



        } catch (DataAccessException e) {
            throw new InternalServerErrorException(
                    String.format("Error al registrar la orden de pedido: %s", purchaseOrder), e
            );
        } finally {
            long end = System.currentTimeMillis(); // ⏱️ Fin del cronómetro
            long durationMs = end - start;
            log.info("⏱️ Tiempo total en registrar orden {}: {} ms ({} segundos)",
                    purchaseOrder, durationMs, durationMs / 1000.0);
        }
    }

    @Transactional(transactionManager = "adbTransactionManager", readOnly = true)
    @Override
    public boolean existsByPurchaseOrder(String purchaseOrder) {
        try {
            return customerOrderHeadRepository.existsById_PurchaseOrder(purchaseOrder);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException(
                    String.format("Error al verificar existencia de PurchaseOrder: %s", purchaseOrder), e
            );
        }
    }

    @Transactional(transactionManager = "adbTransactionManager")
    @Override
    public void registerFactoryOrder(CustomerOrder customerOrder) {
        List<OrderDetail> details = customerOrder.getOrderSale().getOrderDetailList();
        List<FactoryOrderClientEntity> detailsEntities = factoryOrderMapper.toEntityList(details);
        try {
            factoryOrderClientRepository.saveAll (detailsEntities);

        } catch (DataAccessException e) {
            throw new InternalServerErrorException(
                    String.format("Error al registrar la orden de fabrica: %s", detailsEntities.size()), e
            );
        }

    }

    @Transactional(transactionManager = "adbTransactionManager")
    @Override
    public List<String> existingFactoryOrders(CustomerOrderRequestDTO customerOrder) {
        List<String> requestedIds  = customerOrder.orderSale()
                .orderDetailList().stream()
                .map(CustomerOrderRequestDTO.OrderDetailDTO::factoryOrder)
                .distinct() // Es crucial verificar solo los IDs únicos
                .toList();

        if (requestedIds.isEmpty()) {
            return List.of(); // O false, dependiendo de tu regla de negocio (si no hay órdenes que verificar, asumimos true)
        }
        List<FactoryOrderClientEntity> existingEntitiesIterable = factoryOrderClientRepository.findAllById(requestedIds);

        return existingEntitiesIterable.stream()
                .map(FactoryOrderClientEntity::getFactoryOrderId)
                .toList();
    }

}
