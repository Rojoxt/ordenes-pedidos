package com.example.orden_pedido.infrastructure.adapter.in.web.controller;


import com.example.orden_pedido.application.port.in.ProcessFactoryOrdersUseCase;
import com.example.orden_pedido.application.port.in.RegisterCustomerOrderUseCase;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.CustomerOrderRequestDTO;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.FactoryOrdersRequest;
import com.example.orden_pedido.shared.util.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@Slf4j
@RequiredArgsConstructor
public class CustomerOrderController {

    private final RegisterCustomerOrderUseCase registerCustomerOrderUseCase;
    private final ProcessFactoryOrdersUseCase processFactoryOrdersUseCase;


    @PostMapping("/orden-pedido/save")
    public ResponseEntity<?> save(@Valid @RequestBody CustomerOrderRequestDTO request) {

        registerCustomerOrderUseCase.register(request);
        BaseResponse<?> response = new BaseResponse<>("OK", "Orden enviada con éxito");
        return new ResponseEntity<>(response, HttpStatus.CREATED) ;
    }

    @PostMapping("/orden-pedido/consultar")
    public ResponseEntity<?> checkOrderNumber(@Valid @RequestBody FactoryOrdersRequest request) {

        var response= processFactoryOrdersUseCase.execute(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED) ;
    }
}
