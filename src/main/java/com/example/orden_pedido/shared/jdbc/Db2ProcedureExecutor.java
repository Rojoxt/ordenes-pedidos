package com.example.orden_pedido.shared.jdbc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad para invocar procedimientos almacenados en DB2 (AS/400)
 * con soporte para parámetros IN, OUT, INOUT y múltiples ResultSets dinámicos.
 *
 * <p>Características:
 * <ul>
 *   <li>Soporta parámetros IN, OUT e INOUT</li>
 *   <li>Soporta múltiples result sets dinámicos (DYNAMIC RESULT SETS n)</li>
 *   <li>Aplica {@code trim()} automático a parámetros OUT de tipo CHAR</li>
 *   <li>Compatible con JdbcTemplate de Spring</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 *
 * <pre>{@code
 * // 1. Parámetros de entrada/salida
 * var params = List.of(
 *     JdbcDb2ProcedureExecutor.SqlParameter.in("VGUI", "MX25090100001"),
 *     JdbcDb2ProcedureExecutor.SqlParameter.in("VSLOI", "001"),
 *     JdbcDb2ProcedureExecutor.SqlParameter.out("PRES", Types.CHAR),
 *     JdbcDb2ProcedureExecutor.SqlParameter.out("PMENSAJE", Types.CHAR)
 * );
 *
 * // 2. Ejecución
 * var result = executor.executeProcedure(
 *     "{CALL BPCPGM.LXC9112R(?, ?, ?, ?, ?, ?)}",
 *     params
 * );
 *
 * // 3. Acceso a resultados
 * log.info("Filas: {}", result.getRows());        // Primer result set
 * log.info("Todos los result sets: {}", result.resultSets()); // Todos
 * log.info("Parámetros OUT: {}", result.outParams());
 * }</pre>
 */

@Slf4j
@Component
public class Db2ProcedureExecutor {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor del ejecutor de procedimientos DB2.
     *
     * <p>⚠️ Importante:
     * <ul>
     *   <li>Si el proyecto usa <b>múltiples bases de datos</b> (ej. Oracle + DB2),
     *       es necesario anotar el parámetro con {@code @Qualifier("db2JdbcTemplate")}
     *       para asegurarse de que se inyecta el {@link JdbcTemplate} correcto.</li>
     *   <li>Si el proyecto usa <b>solo una base de datos</b> (ej. únicamente DB2),
     *       basta con usar {@code @RequiredArgsConstructor} y omitir el {@code @Qualifier},
     *       ya que Spring detectará automáticamente el único {@link JdbcTemplate} disponible.</li>
     * </ul>
     *
     * @param jdbcTemplate El {@link JdbcTemplate} asociado a la conexión DB2.
     */
    public Db2ProcedureExecutor(@Qualifier("db2JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Modos de los parámetros soportados */
    public enum SqlParameterMode {
        IN, OUT, INOUT
    }

    /**
     * Representa un parámetro de un procedimiento almacenado.
     * Usar los métodos de fábrica estáticos para mayor legibilidad:
     * <ul>
     *     <li>{@link #in(String, Object)} → Parámetro de entrada</li>
     *     <li>{@link #out(String, int)} → Parámetro de salida</li>
     *     <li>{@link #inout(String, int, Object)} → Parámetro de entrada/salida</li>
     * </ul>
     */
    @Getter
    public static class SqlParameter {
        private final String name;
        private final SqlParameterMode mode;
        private final int sqlType;
        private final Object value;

        private SqlParameter(String name, SqlParameterMode mode, int sqlType, Object value) {
            this.name = name;
            this.mode = mode;
            this.sqlType = sqlType;
            this.value = value;
        }

        // Métodos de fábrica para crear parámetros de forma legible
        public static SqlParameter in(String name, Object value) {
            return new SqlParameter(name, SqlParameterMode.IN, Types.NULL, value);
        }

        public static SqlParameter out(String name, int sqlType) {
            return new SqlParameter(name, SqlParameterMode.OUT, sqlType, null);
        }

        public static SqlParameter inout(String name, int sqlType, Object value) {
            return new SqlParameter(name, SqlParameterMode.INOUT, sqlType, value);
        }

    }

    /**
     * Encapsula el resultado de la ejecución de un procedimiento.
     * Incluye:
     * <ul>
     *     <li>Todos los result sets devueltos por el SP</li>
     *     <li>Parámetros de salida (OUT e INOUT)</li>
     * </ul>
     */
    public record ProcedureResult(
            List<List<Map<String, Object>>> resultSets,
            Map<String, Object> outParams,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        /**
         * Retorna el primer result set (para compatibilidad hacia atrás).
         */
        public List<Map<String, Object>> getRows() {
            return resultSets.isEmpty() ? List.of() : resultSets.getFirst();
        }

        /**
         * Retorna el el tiempo de ejecucion del procedimiento.
         */
        public long getExecutionMillis() {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }

    }

    /**
     * Ejecuta un procedimiento almacenado en DB2 AS/400.
     *
     * @param procedureCall Ejemplo: "{CALL BPCPGM.LXC9112R(?, ?, ?, ?, ?, ?)}"
     * @param parameters    Lista de parámetros con nombre, tipo y modo
     * @return Un objeto {@link ProcedureResult} con todos los resultados
     *
     * <h4>Casos de uso:</h4>
     * <ul>
     *   <li>SP con solo IN → usar {@link SqlParameter#in(String, Object)}</li>
     *   <li>SP con OUT → definir con {@link SqlParameter#out(String, int)}</li>
     *   <li>SP con INOUT → definir con {@link SqlParameter#inout(String, int, Object)}</li>
     *   <li>SP con múltiples result sets → acceder con {@link ProcedureResult#resultSets()}</li>
     * </ul>
     */
    public ProcedureResult executeProcedure(String procedureCall, List<SqlParameter> parameters) {
        return jdbcTemplate.execute(procedureCall, (CallableStatement cs) -> {
            var start = LocalDateTime.now();
            // 1. Configurar los parámetros IN, OUT, INOUT
            for (int i = 0; i < parameters.size(); i++) {
                SqlParameter p = parameters.get(i);
                int idx = i + 1; // Los parámetros en JDBC son 1-based
                switch (p.getMode()) {
                    case IN -> cs.setObject(idx, p.getValue());
                    case OUT -> cs.registerOutParameter(idx, p.getSqlType());
                    case INOUT -> {
                        cs.setObject(idx, p.getValue());
                        cs.registerOutParameter(idx, p.getSqlType());
                    }
                }
            }

            // 2. Ejecutar el procedimiento
            boolean hasResultSet = cs.execute();

            // 3. Procesar los resultados
            // Procesar el ResultSet dinámico
            List<List<Map<String, Object>>> resultSets = new ArrayList<>();
            while (true) {
                if (hasResultSet) {
                    try (ResultSet rs = cs.getResultSet()) {
                        resultSets.add(mapResultSet(rs));
                    }
                } else {
                    if (cs.getUpdateCount() == -1) break; // ya no hay más
                }
                hasResultSet = cs.getMoreResults();
            }

            // Procesar parámetros de salida (OUT e INOUT)
            Map<String, Object> outParams = new LinkedHashMap<>();
            for (int i = 0; i < parameters.size(); i++) {
                SqlParameter p = parameters.get(i);
                int idx = i + 1;
                if (p.getMode() == SqlParameterMode.OUT || p.getMode() == SqlParameterMode.INOUT) {
                    Object raw = cs.getObject(idx);
                    if (raw instanceof String s) {
                        outParams.put(p.getName(), s.trim()); // 🔑 aquí el trim automático
                    } else {
                        outParams.put(p.getName(), raw);
                    }
                }
            }
            var end = LocalDateTime.now();

            return new ProcedureResult(resultSets, outParams, start, end);
        });
    }

    /**
     * Convierte un {@link ResultSet} en una lista de mapas.
     * Cada fila es un Map con nombre de columna → valor.
     */
    private List<Map<String, Object>> mapResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                Object rawObject = rs.getObject(i);
                // Aplica trim() solo si el objeto es una cadena de caracteres
                if (rawObject instanceof String s) {
                    row.put(metaData.getColumnName(i), s.trim());
                } else {
                    log.warn("no es string {}", rawObject);
                    row.put(metaData.getColumnName(i), rawObject);
                }
            }
            rows.add(row);
        }
        // ⚠️ Añadir la lógica para el log aquí
        if (rows.isEmpty()) {
            // 🔹 Construir lista de headers
            List<String> headers = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String label = metaData.getColumnLabel(i);
                // Si el label viene con "000xx", usamos el nombre real
                if (label.matches("0+\\d+")) {
                    label = metaData.getColumnName(i);
                }
                headers.add(label);
            }

            log.info("📌 Cursor Vacio ResultSet Headers: {}", headers);
        }


        return rows;
    }
    // ------------------------------
// Métodos de conveniencia
// ------------------------------

    /**
     * Llama un SP solo para ejecutar lógica (ej. INSERT/UPDATE/DELETE) sin preocuparse por OUT ni cursores.
     */
    public void call(String call, Object... args) {
        jdbcTemplate.update(call, args);
    }

    /**
     * Llama un SP y devuelve solo los parámetros OUT/INOUT.
     */
    public Map<String, Object> callWithOut(String call, SqlParameter... params) {
        return executeProcedure(call, List.of(params)).outParams();
    }

    /**
     * Llama un SP que devuelve un solo cursor (el primero).
     */
    public List<Map<String, Object>> callCursor(String call, SqlParameter... params) {
        return executeProcedure(call, List.of(params)).getRows(); // 👈 usa el alias getRows()
    }

    /**
     * Llama un SP que devuelve múltiples cursores.
     */
    public List<List<Map<String, Object>>> callMultiCursor(String call, SqlParameter... params) {
        return executeProcedure(call, List.of(params)).resultSets();
    }
}