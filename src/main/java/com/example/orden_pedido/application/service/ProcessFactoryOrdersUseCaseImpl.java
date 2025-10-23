package com.example.orden_pedido.application.service;

import com.example.orden_pedido.application.port.in.ProcessFactoryOrdersUseCase;
import com.example.orden_pedido.application.port.out.SpAdbFactoryOrderPort;
import com.example.orden_pedido.application.port.out.SpDb2FactoryOrdersPort;
import com.example.orden_pedido.domain.model.order.Orders;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersRequest;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersResponse;
import com.example.orden_pedido.infrastructure.adapter.in.web.mapper.OrdersMapper;
import com.example.orden_pedido.infrastructure.adapter.in.web.mapper.OrdersResponseMapper;
import com.example.orden_pedido.infrastructure.adapter.out.db2.mapper.OrdersSpMapper;
import com.example.orden_pedido.infrastructure.interceptor.MonitoringContext;
import com.example.orden_pedido.infrastructure.interceptor.MonitoringContextHolder;
import com.example.orden_pedido.shared.util.GuidGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessFactoryOrdersUseCaseImpl implements ProcessFactoryOrdersUseCase {

    private final SpDb2FactoryOrdersPort processFactoryOrdersSpPort;
    private final OrdersMapper mapper;
    private final OrdersSpMapper ordersSpMapper;
    private final OrdersResponseMapper ordersResponseMapper;

    private final SpAdbFactoryOrderPort spAdbFactoryOrderPort;
    private final String INTEGRATOR = "ORDEN_PEDIDO_CONSULTAR";

    @Override
    public FactoryOrdersResponse execute(FactoryOrdersRequest request) {


        //1. Crear gui
        String gui = GuidGenerator.generateGui();
        //2. Obtener organización
        String org = request.organization().organizationCode();

        MonitoringContext context = MonitoringContextHolder.get();
        if (context != null) {
            context.setMonitorHead(gui, INTEGRATOR, org);
        }

        // 4. Obtener listas seguras
        List<FactoryOrdersRequest.SalesOrder> salesOrders = Optional.ofNullable(request.salesOrders()).orElse(List.of());
        List<FactoryOrdersRequest.FactoryOrder> factoryOrders = Optional.ofNullable(request.factoryOrders()).orElse(List.of());

        // 5. Procesar pedidos de venta
        processSalesOrders(salesOrders, gui, org, context);

        // 6. Procesar órdenes de fábrica
        processFactoryOrders(factoryOrders, gui, org, context);
        Orders orders = mapper.toDomain(request);

        // 7. Ejecutar SPs en secuencia
        List<Map<String, Object>> result = runProcedures(gui, context);
        orders = ordersSpMapper.enrich(orders, result);


        // 5.ejecutamos sp para elimiar oc
        deleteCustomerOrder(org, orders, context);

        return ordersResponseMapper.toResponse(orders);
    }

    private void processSalesOrders(List<FactoryOrdersRequest.SalesOrder> salesOrders,
                                    String gui, String org,
                                    MonitoringContext context) {
        LocalDateTime start = LocalDateTime.now();
        String source = "DB2: registerConsultOc PedidosVenta";
        try {
            for (var order : salesOrders) {
                processFactoryOrdersSpPort.registerConsultOc(gui, org, "PedidosVenta", order.purchaseOrder());
            }
            context.addDetail("OK", source,
                    "Se registró pedido de venta: " + salesOrders.size(), start);
        } catch (Exception e) {
            context.addDetail("ERROR", source, e.getMessage(), start);
            throw e;
        }
    }

    private void processFactoryOrders(List<FactoryOrdersRequest.FactoryOrder> factoryOrders,
                                      String gui, String org,
                                      MonitoringContext context) {
        LocalDateTime start = LocalDateTime.now();
        String source = "DB2: registerConsultOc OrdenFabrica";
        try {
            for (var order : factoryOrders) {
                processFactoryOrdersSpPort.registerConsultOc(gui, org, "OrdenFabrica", order.factoryOrder());
            }
            context.addDetail("OK", source,
                    "Se registró orden de fábrica: " + factoryOrders.size(), start);
        } catch (Exception e) {
            context.addDetail("ERROR", source, e.getMessage(), start);
            throw e;
        }
    }


    private List<Map<String, Object>> runProcedures(String gui, MonitoringContext context) {
        LocalDateTime start = LocalDateTime.now();
        String source = "DB2: callSp";
        try {
            processFactoryOrdersSpPort.callLxc9119r(gui);
            List<Map<String, Object>> result = processFactoryOrdersSpPort.callLxc9120r(gui);

            context.addDetail("OK", source, "callLxc9119r ejecutado", start);
            return result;

        } catch (Exception e) {
            if (context != null) {
                context.addDetail("ERROR", source,
                        "Error en SP  " + e.getMessage(), start);
            }
            throw e;
        }
    }

    private void deleteCustomerOrder(String codOrg, Orders orders, MonitoringContext context) {
        LocalDateTime start = LocalDateTime.now();
        String source = "ADB: SP_ELIMINAR_PEDIDO_CLIENTE";
        StringBuilder allMessages = new StringBuilder();

        try {
            for (Orders.SalesOrder item : orders.getSalesOrders()) {
                if (!"ERROR".equalsIgnoreCase(item.getStatus())) {
                    continue;
                }
                // Ejecutar SP
                var result = spAdbFactoryOrderPort.deleteCustomerOrder(codOrg, item.getPurchaseOrder());
                // Actualizar contexto
                allMessages.append(result.message()).append("filas detalle:").append(result.detailRows());
            }
            context.addDetail("OK", source, allMessages.toString(), start);
        } catch (Exception e) {
            context.addDetail("ERROR", source, e.getMessage(), start);
        }

    }
}
