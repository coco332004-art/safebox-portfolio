package com.proyecto.safebox.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.security.CustomUserDetails;

// Componente @ControllerAdvice que añade al modelo, en todas las vistas, la información del usuario actualmente autenticado. 
// Esto permite mostrar en los templates datos como el nombre, correo y rol del usuario logueado.


@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public Usuarios currentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getUsuario(); 
        }
        return null;
    }
}
