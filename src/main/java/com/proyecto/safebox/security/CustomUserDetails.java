
package com.proyecto.safebox.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.proyecto.safebox.model.Usuarios;

// Implementación personalizada de UserDetails de Spring Security. 
// Envuelve la entidad Usuarios y expone sus datos a la capa de seguridad (username, password, roles, estado de cuenta).

public class CustomUserDetails implements UserDetails {

    private final Usuarios usuario;

    public CustomUserDetails(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    // Convierte el rol de la entidad Usuarios en una colección de GrantedAuthority reconocida por Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { 
        String roleName = "ROLE_" + usuario.getRol().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return usuario.isEnabled();
    }

    // Para este proyecto no se realiza una gestión avanzada de caducidad o bloqueo de cuentas. 
    //@Override
    //public boolean isAccountNonExpired() { return true; }

    //@Override
    //public boolean isAccountNonLocked() { return true; }

    //@Override
    //public boolean isCredentialsNonExpired() { return true; }
}
