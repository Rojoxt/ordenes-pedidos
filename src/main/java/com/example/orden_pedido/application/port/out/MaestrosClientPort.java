package com.example.orden_pedido.application.port.out;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface MaestrosClientPort {
    Mono<List<Map<String, Object>>> validateOrganization(String organization);
    Mono<List<Map<String, Object>>>  validateApproved(String organization, String table, String code);
    Mono<List<Map<String, Object>>> validateProduct(String organization, String product);
    Mono<List<Map<String, Object>>> getEmails(String codOrg, String integrator);
    Mono<List<Map<String, Object>>> getVariables(String codOrg);
}
