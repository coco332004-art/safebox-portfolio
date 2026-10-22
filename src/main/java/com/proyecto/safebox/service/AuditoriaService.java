
package com.proyecto.safebox.service;

import com.proyecto.safebox.model.AuditoriaEvento;
import com.proyecto.safebox.repository.AuditoriaRepository;
import com.proyecto.safebox.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Servicio de negocio responsable de registrar eventos de auditoría relacionados con la sesión y las operaciones sobre ítems (mensajes). 
// Se apoya en AuditoriaRepository y en el contexto de seguridad actual.

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository repo;

    public AuditoriaService(AuditoriaRepository repo) {
        this.repo = repo;
    }

    private Long currentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails cud) {
            return cud.getUsuario().getId();
        }
        return null;
    }

    public void registrarLogin(Authentication auth, boolean exito) {
        AuditoriaEvento e = new AuditoriaEvento();
        e.setTipo(exito ? "LOGIN_SUCCESS" : "LOGIN_FAILURE");
        e.setUsuarioId(currentUserId(auth));
        e.setDetalle(auth != null ? auth.getName() : null);
        repo.save(e);
    }

    public void registrarAccionItem(String tipo, Long itemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuditoriaEvento e = new AuditoriaEvento();
        e.setTipo(tipo);
        e.setUsuarioId(currentUserId(auth));
        e.setItemId(itemId);
        repo.save(e);
    }
}
