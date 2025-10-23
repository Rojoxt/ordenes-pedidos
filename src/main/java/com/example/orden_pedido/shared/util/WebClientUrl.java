package com.example.orden_pedido.shared.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WebClientUrl {
    @Value("${maestros.service.url}")
    private String maestro;


}
