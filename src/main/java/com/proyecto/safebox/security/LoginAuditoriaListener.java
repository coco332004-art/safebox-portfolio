package com.proyecto.safebox.security;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.proyecto.safebox.service.AuditoriaService;

// Componente que escucha eventos globales de autenticación en Spring Security (logins exitosos o fallidos) y delega en AuditoriaService para registrar dichas acciones.

@Component
public class LoginAuditoriaListener implements ApplicationListener<ApplicationEvent> {

    private final AuditoriaService auditoriaService;

    public LoginAuditoriaListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // Intercepta eventos de tipo AuthenticationSuccessEvent y AbstractAuthenticationFailureEvent. 
    // Según el caso, registra un evento de auditoría de login satisfactorio o fallido.

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent success) {
            Authentication auth = success.getAuthentication();
            auditoriaService.registrarLogin(auth, true);
        } else if (event instanceof AbstractAuthenticationFailureEvent failure) {
            Authentication auth = failure.getAuthentication();
            auditoriaService.registrarLogin(auth, false);
        }
    }
}
