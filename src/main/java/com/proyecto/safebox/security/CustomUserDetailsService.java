
package com.proyecto.safebox.security;

import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.repository.UsuariosRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Servicio que implementa UserDetailsService para integrar la autenticación basada en base de datos con Spring Security. Carga usuarios por email.

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuariosRepository repo;

    public CustomUserDetailsService(UsuariosRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuarios u = repo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new CustomUserDetails(u);
    }
}
