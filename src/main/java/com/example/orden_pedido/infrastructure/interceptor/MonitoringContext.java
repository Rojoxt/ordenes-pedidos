package com.example.orden_pedido.infrastructure.interceptor;


import com.example.orden_pedido.infrastructure.adapter.out.monitoring.dto.MonitoringDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MonitoringContext {

    private final List<MonitoringDTO.MonitoringDetailDTO> details = new ArrayList<>();
    private String referenceDocument;
    private String integrator;
    private String codOrganization;
    private String request;
    private String response;
    private LocalDateTime startTime;


    public void addDetail(String status, String source, String message, LocalDateTime startTime) {
        this.details.add(new MonitoringDTO.MonitoringDetailDTO(status, source, message, startTime, LocalDateTime.now()));
    }

    public List<MonitoringDTO.MonitoringDetailDTO> getDetails() {
        return Collections.unmodifiableList(details);
    }
    public void setMonitorHead(String referenceDocument, String integrator,String codOrganization) {
        this.referenceDocument = referenceDocument;
        this.integrator=integrator;
        this.codOrganization=codOrganization;
    }

    public void setRequestAndResponse(String request, String response) {
        this.request = request;
        this.response=response;
    }

}