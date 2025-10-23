package com.example.orden_pedido.infrastructure.adapter.out.db2.dto;

public record LxcmbmDTO(
        String mbmgui,       // GUI generado (fijo para todos los registros)
        int mbmlin,          // Correlativo global (1, 2, 3...)
        String mbmslo,       // Sublote (valor fijo "1")
        String mbmeve,       // Siempre "CREATE"
        String bprod,        // Código padre
        String bmwhs,        // Fijo "MX"
        //String bmbomm,       // (null o no usado por ahora)
        int bseq,            // Correlativo por código padre (1, 2, ...)
        String bchld,        // Componente
        double bqreq,        // Cantidad requerida
        int bdeff,           // Fecha vigencia desde
        int bddis,           // Fecha vigencia hasta
        double bmscp,        // Factor desperdicio
        int bbubb,           // Correlativo 10, 20, ...
        String bllot,        // Unidad de medida
        int bmddtm,          //Cantidad base defabricación
        String bulot,        //Unidad de medida base de fabricacion
        double bcstp,        // Factor de conversion
        int bopno            // linea orden

) {
}
