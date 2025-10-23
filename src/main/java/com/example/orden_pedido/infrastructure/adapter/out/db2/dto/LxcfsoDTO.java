package com.example.orden_pedido.infrastructure.adapter.out.db2.dto;

public record LxcfsoDTO(

        String fsogui,
        int fsolin,
        int fsoslo,
        String fsofac,
        String fsopro,
        String fsoalm,
        int fsoreq,
        // FSOFDU (int) -> Usado como String para representar la fecha numérica (AAAAMMDD)
        String fsofdu,
        // FSOFRE (int) -> Usado como String para representar la fecha numérica (AAAAMMDD)
        String fsofre,
        String fsolot,
        String fsocom,
        String fsoco2
) {
}
