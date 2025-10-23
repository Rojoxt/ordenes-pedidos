package com.example.orden_pedido.shared.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GuidGenerator {
    /**
     * El constructor es privado para evitar la instanciación de esta clase.
     */
    private GuidGenerator() {
        // No se permite la creación de instancias.
    }

    /**
     * Genera un identificador único en formato de cadena.
     * La cadena resultante sigue el patrón: "MX" + "yyMM" + "HHmmss" + "sss".
     * Ejemplo: MX2508083630713
     * * @return Una cadena de 17 caracteres que actúa como identificador.
     */
    public static String generateGui() {
        LocalTime now = LocalTime.now();

        // Formatea la fecha a "año de dos dígitos" + "mes" (ej. "2508")
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"));

        // Formatea la hora a "horas" + "minutos" + "segundos" (ej. "083630")
        String time = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        // Extrae los milisegundos de la parte nano y los formatea a tres dígitos con ceros a la izquierda.
        String millis = String.format("%03d", now.getNano() / 1_000_000);

        return "MX" + date + time + millis;
    }
}

