package com.example.orden_pedido.infrastructure.adapter.out.db2.spRegisterCustomerOrder;

import com.example.orden_pedido.application.port.out.SpDb2CustomerOrderPort;
import com.example.orden_pedido.domain.aggregate.Material;
import com.example.orden_pedido.domain.aggregate.OrderSale;
import com.example.orden_pedido.infrastructure.adapter.out.db2.dto.LxcfsoDTO;
import com.example.orden_pedido.infrastructure.adapter.out.db2.dto.LxcmbmDTO;
import com.example.orden_pedido.infrastructure.adapter.out.db2.dto.LxcordDTO;
import com.example.orden_pedido.infrastructure.adapter.out.db2.mapper.LxcfsoMapper;
import com.example.orden_pedido.infrastructure.adapter.out.db2.mapper.LxcmbmMapper;
import com.example.orden_pedido.infrastructure.adapter.out.db2.mapper.LxcordMapper;
import com.example.orden_pedido.shared.jdbc.Db2ProcedureExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpCustomerOrderAdapter implements SpDb2CustomerOrderPort {

    private final Db2ProcedureExecutor executor;
    private final LxcordMapper lxcordMapper;
    private final LxcmbmMapper lxcmbmMapper;
    private final LxcfsoMapper lxcfsoMapper;
    private final @Qualifier("taskExecutor")Executor taskExecutor;

    @Override
    public CompletableFuture<Void> registerOrderReceptionAsync(OrderSale orderSale, String gui) {
        String sql="CALL BPCPGM.PSCM_REGISTRAR_RECEPCION_PEDIDO(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?)";
        List<LxcordDTO> records = lxcordMapper.toLxcordDtoList(orderSale, gui);
        List<CompletableFuture<Void>> futures = records.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
                    executor.call(sql,
                            r.ordidprc(), r.ordpais(), r.ordfeprc(), r.ordidqpe(),
                            r.ordtotli(), r.ordhcust(), r.ordhcpo() == null ? "" : r.ordhcpo(), r.ordhsdte(),
                            r.ordhrdte(), r.ordhsstm(), r.ordhwhse(), r.ordhcurr(),
                            r.ordcomp(), r.ordlline(), r.ordlprod(), r.ordlqord(),
                            r.ordlrdte(), r.ordlsdte(), r.ordlnot4(), r.ordlnot5(),
                            r.ordhnot8(), r.ordhnot9(), r.ordlnet(), r.ordadi2(),
                            r.ordadi3());
                }, taskExecutor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("✅ Insertados {} registros para GUI {}", records.size(), gui);
                    } else {
                        log.error("❌ Error al insertar registros para GUI {}", gui, ex);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> registerClientOrderAsync(OrderSale orderSale, String gui) {
        String sql="CALL BPCPGM.PSCM_REGISTRAR_RECEPCION_ORDENFABRICA(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String warehouse= orderSale.getWarehouse();
        List<LxcfsoDTO> records = orderSale.getOrderDetailList().stream()
                .map(r -> lxcfsoMapper.toDto(r, gui, warehouse))
                .toList();

        List<CompletableFuture<Void>> futures = records.stream()
                .map(r -> CompletableFuture.runAsync(() -> {
                    executor.call(sql,
                            r.fsogui(), r.fsolin(), r.fsoslo(), r.fsofac(),
                            r.fsopro(), r.fsoalm(), r.fsoreq() , r.fsofdu(),
                            r.fsofre(), r.fsolot(), r.fsocom(), r.fsoco2()
                            );
                }, taskExecutor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("✅ Insertados {} registros factory para GUI {}", records.size(), gui);
                    } else {
                        log.error("❌ Error al insertar registros para GUI {}", gui, ex);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> registerMaterialListAsync(List<Material> materials, String gui) {
        String sql ="CALL BPCPGM.PSCM_REGISTRAR_LISTA_MATERIAL(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        List<LxcmbmDTO> records = lxcmbmMapper.toLxcmbmDtoList(materials, gui);
        // Crear un Future por cada record
        List<CompletableFuture<Void>> futures = records.stream()
                .map(record -> CompletableFuture.runAsync(() -> {
                    executor.call(sql,
                            record.mbmgui(), record.mbmlin(), record.mbmslo(), record.mbmeve(),
                            record.bprod(), record.bmwhs(), record.bseq(), record.bchld(),
                            record.bqreq(), record.bdeff(), record.bddis(),
                            record.bmscp(), record.bbubb(), record.bllot(),
                            record.bmddtm(), record.bulot(), record.bcstp(),
                            record.bopno()
                    );
                }, taskExecutor))
                .toList();

        // Combinar todos los futuros en uno solo
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("✅ Insertados {} registros para GUI {}",
                                records.size(), gui);
                    } else {
                        log.error("❌ Error al registrar lista de materiales para GUI {}", gui, ex);
                    }
                });
    }

    @Override
    public void triggerLXC9112R(String gui, String sublot, String flgPedBelcorp, String typeOrder) {
        String sql="{CALL BPCPGM.PSCM_SOMETER_JOB_ORDEN_PEDIDO(?, ?, ?, ?)}";

        try {
            executor.call(sql,gui,sublot,flgPedBelcorp,typeOrder);
            log.info("ejecutado correctamente LXC9112R");
        } catch (Exception e) {
            log.error("❌ Error al invocar BPCPGM.LXC9112R con GUI={} y sublot={}", gui, sublot, e);
            // Ya no puedes devolver un CompletableFuture.failedFuture. El error se debe manejar con logging.
        }
    }
}
