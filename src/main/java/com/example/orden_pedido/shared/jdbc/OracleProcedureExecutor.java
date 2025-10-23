package com.example.orden_pedido.shared.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OracleProcedureExecutor {

    private final JdbcTemplate jdbcTemplate;

    public OracleProcedureExecutor(@Qualifier("adbJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    // ==========================================================
    // Métodos de ejecución
    // ==========================================================

    public ProcedureResult executeProcedure(String procedureCall, List<SqlParameter> params) {
        return jdbcTemplate.execute(procedureCall, (CallableStatementCallback<ProcedureResult>) cs -> {

            // 1. Registrar parámetros
            int index = 1;
            for (SqlParameter p : params) {
                if (p.direction == Direction.IN || p.direction == Direction.INOUT) {
                    cs.setObject(index, p.value);
                }
                if (p.direction == Direction.OUT || p.direction == Direction.INOUT) {
                    cs.registerOutParameter(index, p.sqlType);
                }
                index++;
            }

            // 2. Ejecutar
            cs.execute();

            // 3. Recoger resultados
            Map<String, Object> outValues = new LinkedHashMap<>();
            Map<String, List<Map<String, Object>>> resultSets = new LinkedHashMap<>();

            index = 1;
            for (SqlParameter p : params) {
                if (p.direction == Direction.OUT || p.direction == Direction.INOUT) {
                    Object value = cs.getObject(index);

                    if (p.sqlType == Types.REF_CURSOR) {
                        // Mapear cursor a lista de filas
                        try (ResultSet rs = (ResultSet) value) {
                            List<Map<String, Object>> rows = new ArrayList<>();
                            ResultSetMetaData meta = rs.getMetaData();
                            int colCount = meta.getColumnCount();

                            while (rs.next()) {
                                Map<String, Object> row = new LinkedHashMap<>();
                                for (int col = 1; col <= colCount; col++) {
                                    row.put(meta.getColumnLabel(col), rs.getObject(col));
                                }
                                rows.add(row);
                            }
                            resultSets.put(p.name, rows);
                        }
                    } else {
                        outValues.put(p.name, value);
                    }
                }
                index++;
            }

            return new ProcedureResult(outValues, resultSets);
        });
    }

    // ==========================================================
    // Helper: Parámetros del procedimiento
    // ==========================================================

    public enum Direction {IN, OUT, INOUT}

    public record SqlParameter(
            String name,
            Direction direction,
            int sqlType,
            Object value
    ) {
        public SqlParameter {
            // Validación opcional, e.g.:
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be blank");
        }

        public static SqlParameter in(String name, Object value) {
            // Usamos Types.OTHER ya que el JdbcTemplate de Spring/driver infiere el tipo.
            return new SqlParameter(name, Direction.IN, Types.OTHER, value);
        }

        public static SqlParameter out(String name, int sqlType) {
            // El valor siempre es 'null' para un parámetro de salida.
            return new SqlParameter(name, Direction.OUT, sqlType, null);
        }

        public static SqlParameter inOut(String name, int sqlType, Object value) {
            return new SqlParameter(name, Direction.INOUT, sqlType, value);
        }

        public static SqlParameter cursor(String name) {
            return new SqlParameter(name, Direction.OUT, Types.REF_CURSOR, null);
        }
    }

    // ==========================================================
    // Result wrapper
    // ==========================================================

    public record ProcedureResult(
            Map<String, Object> outParams,
            Map<String, List<Map<String, Object>>> cursors
    ) {
        public List<Map<String, Object>> getFirstCursor() {
            return cursors.values().stream().findFirst().orElse(List.of());
        }

        public List<Map<String, Object>> getCursor(String name) {
            return cursors.getOrDefault(name, List.of());
        }

        public <T> T getOut(String name, Class<T> type) {
            return type.cast(outParams.get(name));
        }

        public boolean hasCursor(String name) {
            return cursors.containsKey(name);
        }
    }

    // ------------------------------
    // Métodos de conveniencia
    // ------------------------------
    public void call(String call, Object... args) {
        jdbcTemplate.update(call, args);
    }

    public Map<String, Object> callWithOut(String call, SqlParameter... params) {
        return executeProcedure(call, List.of(params)).outParams();
    }

    public List<Map<String, Object>> callCursor(String call, SqlParameter... params) {
        return executeProcedure(call, List.of(params)).getFirstCursor();
    }

    public Map<String, List<Map<String, Object>>> callMultiCursor(String call, SqlParameter... params) {
        return executeProcedure(call, List.of(params)).cursors();
    }
}
