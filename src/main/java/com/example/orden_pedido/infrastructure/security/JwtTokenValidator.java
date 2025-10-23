package com.example.orden_pedido.infrastructure.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JwtTokenValidator extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final JWTServiceUseCase jwtServiceUseCase;

    public JwtTokenValidator(JWTServiceUseCase jwtServiceUseCase) {
        this.jwtServiceUseCase = jwtServiceUseCase;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            try {
                DecodedJWT decodedJWT = jwtServiceUseCase.isTokenValid(jwtToken);
                String username = jwtServiceUseCase.extractUsername(decodedJWT);
                String role = jwtServiceUseCase.getSpecificClaim(decodedJWT, "role").asString();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // --- Logueo añadido aquí ---
                logger.debug("JWT authentication success for user='{}', authorities={}",
                        username, authentication.getAuthorities());

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                logger.warn("JWT validation failed: {}", e.getMessage());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                            {
                                "message": "ERROR",
                                "data": "Token inválido o expirado",
                            }
                        """);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}