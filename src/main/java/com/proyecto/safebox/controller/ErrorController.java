package com.proyecto.safebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//Controlador simple para centralizar la vista de acceso denegado cuando Spring Security bloquea una operación por falta de permisos.

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "safebox/access-denied";
    }
}
