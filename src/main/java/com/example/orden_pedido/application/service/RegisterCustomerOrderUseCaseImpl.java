package com.example.orden_pedido.application.service;

import com.example.orden_pedido.application.port.in.RegisterCustomerOrderUseCase;
import com.example.orden_pedido.application.port.out.CustomerOrderPersistencePort;
import com.example.orden_pedido.application.port.out.MaestrosClientPort;
import com.example.orden_pedido.application.port.out.SpDb2CustomerOrderPort;
import com.example.orden_pedido.domain.model.customerOrder.CustomerOrder;
import com.example.orden_pedido.infrastructure.adapter.in.web.dto.CustomerOrderRequestDTO;
import com.example.orden_pedido.infrastructure.adapter.in.web.exception.FactoryOrderAlreadyExistsException;
import com.example.orden_pedido.infrastructure.adapter.in.web.exception.PurchaseOrderAlreadyExistsException;
import com.example.orden_pedido.infrastructure.adapter.in.web.exception.ResourceValidationException;
import com.example.orden_pedido.infrastructure.adapter.in.web.mapper.CustomerOrderMapper;
import com.example.orden_pedido.infrastructure.interceptor.MonitoringContext;
import com.example.orden_pedido.infrastructure.interceptor.MonitoringContextHolder;
import com.example.orden_pedido.shared.util.GuidGenerator;
import com.example.orden_pedido.shared.util.WebClientUrl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class RegisterCustomerOrderUseCaseImpl implements RegisterCustomerOrderUseCase {

    private final CustomerOrderPersistencePort customerOrderPersistencePort;
    private final CustomerOrderValidationService customerOrderValidationService;
    private final CustomerOrderMapper mapper;
    private final SpDb2CustomerOrderPort spCustomerOrderPort;
    private final WebClientUrl url;
    private final String INTEGRATOR = "ORDEN_PEDIDO";
    private final MaestrosClientPort maestrosClient;


    @Override
    @Transactional(transactionManager = "adbTransactionManager", rollbackFor = Exception.class)
    public void register(CustomerOrderRequestDTO request) {


        String purchaseOrder = request.orderSale().purchaseOrder();
        String organizationCode = request.orderSale().organizationCode();

        MonitoringContext context = MonitoringContextHolder.get();
        if (context != null) {
            // 1. Crear un valor seguro: si purchaseOrder es null, usa una cadena vacía o "N/A"
            String safePurchaseOrder = (purchaseOrder != null) ? purchaseOrder : "N/A";
            context.setMonitorHead(safePurchaseOrder, INTEGRATOR, organizationCode);
        }

        String finalTypeOrder = orderExist(request, context);
        String flgPedBelcorp = getFlagOrder(organizationCode);

        var customerOrder = mapper.toDomain(request);

        customerOrder= validateAndHomologateOrder(customerOrder,context);

        saveCustomerOrderToAdb(customerOrder,context);

        String gui= saveCustomerOrderToDb2(customerOrder,finalTypeOrder,context);
        executeLXC9112R(gui,finalTypeOrder,flgPedBelcorp,context);


    }

    private String orderExist(CustomerOrderRequestDTO customerOrder, MonitoringContext context) {
        String source = "Validar existencia de orden pedido";
        LocalDateTime start = LocalDateTime.now();
        if(customerOrder.orderSale().purchaseOrder().isBlank()){
            var errorResult =customerOrderPersistencePort.existingFactoryOrders(customerOrder);
            if (errorResult.isEmpty()){
                context.addDetail("OK", source, "Orden fabrica nuevo", start);
            }else {
                context.addDetail("OK", source, "ya existen las siguientes odenes de fabrica", start);
                throw new FactoryOrderAlreadyExistsException("Ya existen a siguientes ordenes de fabrica",errorResult);
            }
            return "FAB";
        }else {
            if (customerOrderPersistencePort.existsByPurchaseOrder(customerOrder.orderSale().purchaseOrder())) {
                context.addDetail("OK", source, "ya existe el order pedido", start);
                throw new PurchaseOrderAlreadyExistsException(customerOrder.orderSale().purchaseOrder());
            } else {
                context.addDetail("OK", source, "Orden pedido nuevo", start);
            }
            return "PED";
        }

    }

    private CustomerOrder validateAndHomologateOrder(CustomerOrder order, MonitoringContext context){
        LocalDateTime start = LocalDateTime.now();
        return customerOrderValidationService.validateAndHomologate(order)
                .flatMap(r->{
                    context.addDetail("OK", url.getMaestro(), "Validaciones y homologaciones finalizada existosamente",start);
                    return Mono.just(r);
                })
                .doOnError(e->{
                    if (e instanceof ResourceValidationException ex) {
                        // Aquí tienes la lista de errores detallados
                        context.addDetail("ERROR", url.getMaestro(), String.join(", ", ex.getErrors()), start);
                    } else {
                        context.addDetail("ERROR", url.getMaestro(), e.getMessage(), start);
                    }
                }).block();

    }

    private void saveCustomerOrderToAdb(CustomerOrder order,MonitoringContext context){
        LocalDateTime start = LocalDateTime.now();
        String source = "ADB: Persistencia de datos";
        try{
            if(order.getOrderSale().getPurchaseOrder().isBlank()) {
                customerOrderPersistencePort.registerFactoryOrder(order);
            }else {
                customerOrderPersistencePort.registerClientOrder(order);
            }
            context.addDetail("OK",source,"se guardo correctamente",start);
        }
        catch (Exception e){
            context.addDetail("ERROR",source,"Error:"+e.getMessage(),start);
        }
    }

    private String saveCustomerOrderToDb2(CustomerOrder order, String typeOrder, MonitoringContext context){
        LocalDateTime start = LocalDateTime.now();
        String source = "DB2: Persistencia de datos";
        String gui = GuidGenerator.generateGui();

        try {
            if(typeOrder.equals("PED")){
                CompletableFuture<Void> f1 = spCustomerOrderPort.registerOrderReceptionAsync(order.getOrderSale(), gui);
                CompletableFuture<Void> f2 = spCustomerOrderPort.registerMaterialListAsync(order.getMaterials(), gui);

                // Espera a que terminen ambas tareas (bloqueante)
                CompletableFuture.allOf(f1, f2).join();
                context.addDetail("OK", source, "Se guardó correctamente orden pedido: " + gui, start);
            }else {
                CompletableFuture<Void> f3= spCustomerOrderPort.registerClientOrderAsync(order.getOrderSale(),gui);
                f3.join();
                context.addDetail("OK", source, "Se guardó correctamente orden fabrica: " + gui, start);
            }

            return gui;
        } catch (Exception ex) {
            Throwable actualCause = (ex.getCause() != null) ? ex.getCause() : ex;
            context.addDetail("ERROR", source,
                    "Error al guardar " + gui + ": " + actualCause.getMessage(), start);
            throw new RuntimeException("Error guardando en DB2:"+actualCause.getMessage(), actualCause);
        }
    }

    private void executeLXC9112R(String gui,String typeOrder,String flgPedBelcorp,MonitoringContext context){
        LocalDateTime start = LocalDateTime.now();
        String source = "SP: LXC9112R";
        try {
            log.info("{},1,{},{}",gui,typeOrder,flgPedBelcorp);
            spCustomerOrderPort.triggerLXC9112R(gui,"1", flgPedBelcorp, typeOrder);
            context.addDetail("OK",source,"Executado correctamente",start);
        }catch (Exception e){
            context.addDetail("ERROR",source,"Error"+e.getMessage(),start);
        }

    }
    private String getFlagOrder(String organizationCode) {

        final String TARGET_VARIABLE = "@FlgPedBelcorp";
        // 1. Define el flujo reactivo
        Mono<String> flagMono = maestrosClient.getVariables(organizationCode)
                .flatMap(list -> {
                    if (list == null || list.isEmpty()) {
                        return Mono.just("");
                    }
                    // 2. Busca la variable y extrae el valor
                    return list.stream()
                            .filter(item -> TARGET_VARIABLE.equals(item.get("VARIABLE")))
                            .findAny()
                            .map(item -> (String) item.get("VALOR"))
                            .map(Mono::just)
                            .orElseGet(() -> {
                                // Si no se encuentra, devuelve el valor por defecto
                                System.out.println("ADVERTENCIA: Variable " + TARGET_VARIABLE + " no encontrada para " + organizationCode);
                                return Mono.just("");
                            });
                });

        String flagPedido = flagMono.block();
        return (flagPedido != null) ? flagPedido : "";
    }

}
