package com.example.orden_pedido.infrastructure.adapter.out.db2.mapper;

import com.example.orden_pedido.domain.model.order.Orders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrdersSpMapper {
    public Orders enrich(Orders orders, List<Map<String, Object>> result) {
        for (Map<String, Object> row : result) {
            String type = (String) row.get("COYTIP");
            String orderNumber = (String) row.get("COYOSA");
            String yob = (String) row.get("COYOYO");
            String status = (String) row.get("COYEST");
            String message = (String) row.get("COYMES");

            if ("PedidosVenta".equalsIgnoreCase(type)) {
                orders.updateSalesOrder(orderNumber, status, message, yob);
            } else if ("OrdenFabrica".equalsIgnoreCase(type)) {
                orders.updateFactoryOrder(orderNumber, status, message, yob);
            }
        }
        return orders;
    }
}
