package com.example.orden_pedido.application.port.in;

import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersRequest;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersResponse;

public interface ProcessFactoryOrdersUseCase {
    FactoryOrdersResponse execute(FactoryOrdersRequest request);

}
