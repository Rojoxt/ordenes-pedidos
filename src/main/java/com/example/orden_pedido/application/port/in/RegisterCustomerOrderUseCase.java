package com.example.orden_pedido.application.port.in;


import com.example.orden_pedido.infrastructure.adapter.in.web.dto.CustomerOrderRequestDTO;

public interface RegisterCustomerOrderUseCase {
    void register(CustomerOrderRequestDTO requestDTO);
}
