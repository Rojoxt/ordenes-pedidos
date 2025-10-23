package com.example.orden_pedido.infrastructure.adapter.out.db2.spRegisterCustomerOrder;

import com.example.orden_pedido.application.port.out.SpDb2FactoryOrdersPort;
import com.example.orden_pedido.shared.jdbc.Db2ProcedureExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessFactoryOrdersSpAdapter implements SpDb2FactoryOrdersPort {
    private final Db2ProcedureExecutor jdbcProcedureExecutor;

    @Override
    public void registerConsultOc(String gui, String org, String block, String order) {
        String sql= "{CALL BPCPGM.PSCM_REGISTRAR_CONSULTA_ORDEN_PEDIDO(?, ?, ?,?)}";
        List<Db2ProcedureExecutor.SqlParameter> params = List.of(
                Db2ProcedureExecutor.SqlParameter.in("PGUi",  gui),
                Db2ProcedureExecutor.SqlParameter.in("PORG",  org),
                Db2ProcedureExecutor.SqlParameter.in("PBLOQUE", block),
                Db2ProcedureExecutor.SqlParameter.in("PORDEN", order)
        );
        var result= jdbcProcedureExecutor.executeProcedure(sql, params);
    }

    @Override
    public void callLxc9119r(String gui) {
        String sql= "{CALL BPCPGM.LXC9119R(?,?)}";
        List<Db2ProcedureExecutor.SqlParameter> params = List.of(
                Db2ProcedureExecutor.SqlParameter.in("VGUI",  gui),
                Db2ProcedureExecutor.SqlParameter.out("VRTA",  Types.CHAR));
        var result= jdbcProcedureExecutor.executeProcedure(sql, params).outParams();


    }

    @Override
    public List<Map<String, Object>> callLxc9120r(String gui) {
        String sql= "{CALL BPCPGM.LXC9120R(?)}";
        List<Db2ProcedureExecutor.SqlParameter> params = List.of(
                Db2ProcedureExecutor.SqlParameter.inout("PGUI", Types.CHAR, gui));
        var result = jdbcProcedureExecutor.executeProcedure(sql, params);
        log.info("lx20 {}",result.getRows());

        return result.getRows();
    }
}

