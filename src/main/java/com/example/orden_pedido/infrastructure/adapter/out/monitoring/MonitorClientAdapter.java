package com.example.orden_pedido.infrastructure.adapter.out.monitoring;

import com.example.orden_pedido.infrastructure.adapter.out.monitoring.dto.MonitoringDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MonitorClientAdapter  {

    private final WebClient monitorWebClient;

    public MonitorClientAdapter(@Qualifier("monitorWebClient")WebClient monitorWebClient) {
        this.monitorWebClient = monitorWebClient;
    }


    public Mono<Void> send(MonitoringDTO dto) {
        //log.info("request  {}",dto);
        return monitorWebClient
                .post()
                .bodyValue(dto)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("❌ Error {} con response JSON: {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Error " + clientResponse.statusCode() + ": " + errorBody));
                                })
                )
                .toEntity(String.class)
                .doOnSuccess(resp -> {
                   // log.info("✅ Status={}", resp.getStatusCode());
                    //log.info("📥 Response JSON: {}", resp.getBody());
                })
                .doOnError(ex -> log.error("❌ Error enviando evento de monitoreo", ex))
                .then();
    }
}
