package com.example.orden_pedido.application.port.out;

import java.util.List;
import java.util.Map;

public interface SpDb2FactoryOrdersPort {
    void registerConsultOc(String gui, String org, String block, String order);
    void callLxc9119r(String gui);
    List<Map<String, Object>> callLxc9120r(String gui);
}
