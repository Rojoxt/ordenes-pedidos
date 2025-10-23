package com.example.orden_pedido.infrastructure.adapter.out.monitoring.dto;

import com.example.orden_pedido.infrastructure.interceptor.MonitoringContext;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record MonitoringDTO(
        String jsonData,
        String referenceDocument,
        String status,
        String message,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String webServiceUrl,
        @Size(max=2) String country,
        String integrator,
        @Size(max=10)String organizationCode,
        String jsonResponse,
        List<MonitoringDetailDTO> monitoringDetail
) {
    public record MonitoringDetailDTO(
            @Size(max=20)String status,
            String webServiceUrl,
            String detail,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
    }
    public static MonitoringDTO fromContext(MonitoringContext context, String url, String status, String message) {
        if (context == null) {
            return null;
        }

        String countryCode = context.getCodOrganization() != null && context.getCodOrganization().length() >= 2
                ? context.getCodOrganization().substring(0, 2)
                : "SN";

        return new MonitoringDTO(
                context.getRequest(),
                context.getReferenceDocument(),
                status, // o se puede pasar dinámicamente si quieres
                message,
                context.getStartTime(), // puedes guardar startTime en el contexto si quieres medir duración
                LocalDateTime.now(),
                url,
                countryCode,
                context.getIntegrator(),
                context.getCodOrganization(),
                context.getResponse(),
                context.getDetails()
        );
    }
}
