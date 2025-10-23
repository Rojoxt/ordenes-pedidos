package com.example.orden_pedido.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * Clase de configuración para la creación de beans de WebClient.
 * Define instancias de WebClient dedicadas para cada servicio externo,
 * asegurando una configuración modular y reutilizable.
 * <p>
 * Incluye un filtro común para medir el tiempo de respuesta de las
 * peticiones salientes y registrarlo con un marcador de log específico.
 */

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${monitoring.service.url}")
    private String monitorBaseUrl;

    @Value("${maestros.service.url}")
    private String maestrosBaseUrl;

    // --- Otros servicios ---
    // Agrega más URLs de servicios externos aquí.
    // Ejemplo: @Value("${otro.servicio.service.url}")
    //          private String otroServicioBaseUrl;


    @Bean("monitorWebClient")
    public WebClient monitorWebClient(List<ExchangeFilterFunction> exchangeFilterFunctions) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(monitorBaseUrl);

        exchangeFilterFunctions.forEach(builder::filter);

        return builder.build();
    }

    @Bean("maestrosWebClient")
    public WebClient maestrosWebClient(List<ExchangeFilterFunction> exchangeFilterFunctions) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(maestrosBaseUrl);

        exchangeFilterFunctions.forEach(builder::filter);

        return builder.build();
    }

    // --- Otros clientes ---
    // Agrega más beans de WebClient para otros servicios externos aquí.
    // Ejemplo:
    // @Bean
    // public WebClient otroServicioWebClient() {
    //     return WebClient.builder()
    //             .baseUrl(otroServicioBaseUrl)
    //             .filter(timerLoggingFilter())
    //             // Puedes agregar otros filtros o configuraciones específicas
    //             .build();
    // }


    @Bean
    @ConditionalOnProperty(name = "app.performance.interceptor.enabled", havingValue = "true")
    public ExchangeFilterFunction timerLoggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            long start = System.currentTimeMillis();
            return Mono.just(request)
                    .doFinally(signal -> {
                        long end = System.currentTimeMillis();
                        double durationSeconds = (end - start) / 1000.0;
                        log.info(
                                "⏱️ Petición {} a {} ejecutada en {}s",
                                request.method(),
                                request.url(),
                                durationSeconds);
                    });
        });
    }
}
