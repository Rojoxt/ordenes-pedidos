package com.example.orden_pedido.application.port.out;


import com.example.orden_pedido.infrastructure.adapter.out.adb.dto.DeleteCustomerOrderResult;

public interface SpAdbFactoryOrderPort {
    DeleteCustomerOrderResult deleteCustomerOrder(String codOrg, String purchaseOrder);
}
