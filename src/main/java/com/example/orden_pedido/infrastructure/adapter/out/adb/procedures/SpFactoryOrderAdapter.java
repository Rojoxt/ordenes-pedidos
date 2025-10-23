package com.example.orden_pedido.infrastructure.adapter.out.adb.procedures;


import com.example.orden_pedido.application.port.out.SpAdbFactoryOrderPort;
import com.example.orden_pedido.infrastructure.adapter.out.adb.dto.DeleteCustomerOrderResult;
import com.example.orden_pedido.shared.jdbc.OracleProcedureExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpFactoryOrderAdapter implements SpAdbFactoryOrderPort {

    private final OracleProcedureExecutor executor;
    @Override
    public DeleteCustomerOrderResult deleteCustomerOrder(String codOrg, String purchaseOrder) {
        String sql="call PKG_ORDEN_PEDIDO.SP_ELIMINAR_PEDIDO_CLIENTE(?,?,?)";
        List<Map<String,Object>> result = executor.callCursor(
                sql,
                OracleProcedureExecutor.SqlParameter.in("P_COD_ORGANIZACION", codOrg),
                OracleProcedureExecutor.SqlParameter.in("P_ORDEN_COMPRA", purchaseOrder),
                OracleProcedureExecutor.SqlParameter.cursor("P_RESULTADO")
        );

        log.info("Delete result: {}", result);

        if (result.isEmpty()) {
            return new DeleteCustomerOrderResult("ERROR", "No results returned", 0, 0);
        }

        Map<String, Object> row = result.getFirst();
        return new DeleteCustomerOrderResult(
                (String) row.get("ESTADO_SALIDA"),
                (String) row.get("MENSAJE"),
                ((Number) row.get("FILAS_CABECERA")).intValue(),
                ((Number) row.get("FILAS_DETALLE")).intValue()
        );
    }
}
