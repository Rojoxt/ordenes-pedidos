package com.example.orden_pedido.infrastructure.interceptor;

import com.example.orden_pedido.infrastructure.adapter.out.monitoring.MonitorClientAdapter;
import com.example.orden_pedido.infrastructure.adapter.out.monitoring.dto.MonitoringDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringContextFilter extends HttpFilter {

    private final MonitorClientAdapter monitorClient;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        ContentCachingRequestWrapper reqWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper(response);
        String status = "ERROR";
        String message = "Ha ocurrido un error inesperado";
        // 🔹 Crear contexto
        MonitoringContext context = new MonitoringContext();
        MonitoringContextHolder.set(context);
        LocalDateTime startTime = LocalDateTime.now();
        context.setStartTime(startTime);

        try {
            chain.doFilter(reqWrapper, resWrapper); // Pasar al controller
            status = "OK";
            message="Proceso finalizado correctamente";
        } catch (Exception e) {
            message = "Error en el procesamiento de la solicitud";
        }finally {
            try {
                // 🔹 Captura body
                String requestBody = new String(reqWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                String responseBody = new String(resWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

                context.setRequest(requestBody);
                context.setResponse(responseBody);

                String webServiceUrl =  request.getRequestURI();

                // 🔹 Enviar monitoreo
                MonitoringDTO dto = MonitoringDTO.fromContext(context,webServiceUrl,status,message);
                monitorClient.send(dto).subscribe();

                // 🔹 Copiar response para que llegue al cliente
                resWrapper.copyBodyToResponse();
            } catch (Exception e) {
                log.error("Error capturando request/response body", e);
            } finally {
                MonitoringContextHolder.clear();
            }
        }
    }
}