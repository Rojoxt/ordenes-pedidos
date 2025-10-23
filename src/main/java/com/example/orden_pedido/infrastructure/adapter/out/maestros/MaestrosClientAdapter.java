package com.example.orden_pedido.infrastructure.adapter.out.maestros;

import com.example.orden_pedido.application.port.out.MaestrosClientPort;
import com.example.orden_pedido.infrastructure.adapter.out.maestros.dto.MaestrosResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class MaestrosClientAdapter implements MaestrosClientPort {

    private final  WebClient webClient;

    public MaestrosClientAdapter(@Qualifier("maestrosWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<List<Map<String, Object>>> validateOrganization(String organization) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/validation/maestros-organization")
                        .queryParam("organization", organization)
                        .build()
                )
                .retrieve()
                .bodyToMono(MaestrosResponse.class)
                .map(MaestrosResponse::object)
                .doOnError(ex -> log.error("Error al obtener variables para {}", organization));
    }

    @Override
    public Mono<List<Map<String, Object>>> validateApproved(String organization, String table, String code) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/validation/maestros-approved")
                        .queryParam("organization", organization)
                        .queryParam("table", table)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(MaestrosResponse.class)
                .map(MaestrosResponse::object)
                .doOnError(ex -> log.error("Error al obtener variables para {} {}, {}", organization, table, code));
    }

    @Override
    public Mono<List<Map<String, Object>>> validateProduct(String organization, String productCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/validation/maestros-product")
                        .queryParam("organization", organization)
                        .queryParam("product", productCode)
                        .build())
                .retrieve()
                .bodyToMono(MaestrosResponse.class)
                .map(MaestrosResponse::object)
                .doOnError(ex -> log.error("Error al obtener variables para {} {}", organization, productCode));
    }

    @Override
    public Mono<List<Map<String, Object>>> getEmails(String codOrg, String integrator) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/obtener-correos")
                        .queryParam("organization", codOrg)
                        .queryParam("integrator", integrator)
                        .build()
                )
                .retrieve()
                .bodyToMono(MaestrosResponse.class)
                .map(MaestrosResponse::object) // 👈 directamente tu lista
                .doOnError(ex -> log.error("Error al obtener valor de correos. org={} integrador={} ",
                        codOrg, integrator, ex));
    }

    @Override
    public Mono<List<Map<String, Object>>> getVariables(String codOrg) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/obtener-variables")
                        .queryParam("organization", codOrg)
                        .build()
                )
                .retrieve()
                .bodyToMono(MaestrosResponse.class)
                .doOnNext(response -> log.info("📥 Response Maestros para {}: {}", codOrg, response))
                .map(MaestrosResponse::object) // 👈 solo nos quedamos con `object`
                .doOnError(ex -> log.error("Error al obtener variables para {}", codOrg, ex))
                ;
    }
}
