package com.example.orden_pedido.application.port.out;


import com.example.orden_pedido.domain.aggregate.Material;
import com.example.orden_pedido.domain.aggregate.OrderSale;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SpDb2CustomerOrderPort {
    CompletableFuture<Void> registerOrderReceptionAsync(OrderSale orderSale, String gui);
    CompletableFuture<Void> registerClientOrderAsync(OrderSale orderSale, String gui);
    CompletableFuture< Void> registerMaterialListAsync(List<Material> materials, String gui);
    void  triggerLXC9112R(String gui, String sublot,String flgPedBelcorp,String typeOrder);

}
