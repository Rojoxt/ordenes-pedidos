package com.example.orden_pedido.application.service;

import com.example.orden_pedido.application.port.out.MaestrosClientPort;
import com.example.orden_pedido.domain.aggregate.Material;
import com.example.orden_pedido.domain.model.customerOrder.CustomerOrder;
import com.example.orden_pedido.infrastructure.adapter.in.web.exception.ResourceValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerOrderValidationService {

    private final MaestrosClientPort maestrosClient;

    public Mono<CustomerOrder> validateAndHomologate(CustomerOrder customerOrder) {

        String organizationCode = customerOrder.getOrderSale().getOrganizationCode();

        //0. Variable
        Mono<String> variablesMono = maestrosClient.getVariables(organizationCode)
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.just("No se encontraron variables para: " + organizationCode);
                    }
                    // Convierte la lista en un mapa de forma más eficiente
                    Map<String, String> v = list.stream()
                            .collect(Collectors.toMap(
                                    item -> (String) item.get("VARIABLE"),
                                    item -> (String) item.get("VALOR")
                            ));
                    // Actualiza la orden fuera del stream, con los valores del mapa
                    customerOrder.getOrderSale().updateVariables(v.get("@COD_CLIENTE"),v.get("@ALM_PEDIDO"));

                    // Retorna un Mono vacío para indicar que no hay errores
                    return Mono.just("");
                })
                .onErrorResume(e -> Mono.just("Error al obtener variables: " + e.getMessage()));

        // 1. Validar y homologar los detalles de la orden (OrderSale)
        Mono<List<String>> orderSaleMono = Flux.fromIterable(customerOrder.getOrderSale().getOrderDetailList())
                .flatMap(detail -> {
                    // Validación del producto: un Mono que emite un error o está vacío
                    Mono<String> productValidation = maestrosClient.validateProduct(organizationCode, detail.getMaterial())
                            .then(Mono.<String>empty())
                            .onErrorResume(e -> Mono.just("Material no válido: " + detail.getMaterial()));

                    // Homologación de la unidad: un Mono que actualiza el detalle y emite un error o está vacío
                    Mono<String> unitHomologation = maestrosClient.validateApproved(organizationCode, "UOM", detail.getUnitOfMeasure())
                            .doOnSuccess(list -> detail.setUnitOfMeasure(list.getFirst().get("VALOR_HOM").toString()))
                            .then(Mono.<String>empty())
                            .onErrorResume(e -> Mono.just("Unidad de medida no hay homologado: " + detail.getUnitOfMeasure()));

                    // Ejecutar en paralelo con whenDelayError
                    return Mono.whenDelayError(productValidation, unitHomologation)
                            .thenMany(Flux.concat(productValidation, unitHomologation))
                            .filter(error -> !error.isEmpty());
                })
                .collectList();

        // 2. Validar y homologar la lista de materiales y sus componentes
        List<Material> materials = customerOrder.getMaterials();
        if (materials == null) {
            materials = List.of(); // Usar una lista inmutable vacía para evitar NPE
        }

        Mono<List<String>> materialMono = Flux.fromIterable(materials)
                .flatMap(material -> {
                    // Validación y homologación del código padre
                    Mono<String> parentValidation = maestrosClient.validateProduct(organizationCode, material.getParentCode())
                            .then(Mono.<String>empty())
                            .onErrorResume(e -> Mono.just("Código padre no válido: " + material.getParentCode()));

                    Mono<String> parentUnitHomologation = maestrosClient.validateApproved(organizationCode, "UOM", material.getBaseQuantityUnit())
                            .doOnSuccess(list -> material.setBaseQuantityUnit(list.getFirst().get("VALOR_HOM").toString()))
                            .then(Mono.<String>empty())
                            .onErrorResume(e -> Mono.just("Unidad de medida base no homologada: " + material.getBaseQuantityUnit()));

                    // Para cada componente del material
                    Flux<String> componentValidations = Flux.fromIterable(material.getComponents())
                            .flatMap(component -> {
                                Mono<String> compValidation = maestrosClient.validateProduct(organizationCode, component.getComponent())
                                        .then(Mono.<String>empty())
                                        .onErrorResume(e -> Mono.just("Componente no válido: " + component.getComponent()));

                                Mono<String> compUnitHomologation = maestrosClient.validateApproved(organizationCode, "UOM", component.getUnitOfMeasure())
                                        .doOnSuccess(list -> component.setUnitOfMeasure(list.getFirst().get("VALOR_HOM").toString()))
                                        .then(Mono.<String>empty())
                                        .onErrorResume(e -> Mono.just("Unidad de medida no homologada: " + component.getUnitOfMeasure()));
                                // Ejecutar ambas validaciones en paralelo
                                return Flux.concat(compValidation, compUnitHomologation);
                            });
                    // Combinar validaciones del material + sus componentes
                    return Flux.concat(parentValidation, parentUnitHomologation, componentValidations);
                })
                .filter(error -> !error.isEmpty())
                .collectList();

        return Mono.zip(orderSaleMono, materialMono,variablesMono)
                .flatMap(tuple -> {
                    List<String> errors = new ArrayList<>();
                    errors.addAll(tuple.getT1());
                    errors.addAll(tuple.getT2());
                    errors.add(tuple.getT3());
                    errors.removeIf(String::isBlank);

                    if (!errors.isEmpty()) {
                        return Mono.error(new ResourceValidationException("Validaciones fallidas", errors));
                    }
                    return Mono.just(customerOrder); // ya validado y homologado
                });
    }
}