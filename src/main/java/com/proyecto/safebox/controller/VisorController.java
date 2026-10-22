package com.proyecto.safebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controlador de la página principal de la aplicación. Define la ruta raíz y la vista de inicio.


@Controller
public class VisorController {
    @GetMapping({"/", "/index"}) //Root /home
    public String index() {
        return "safebox/index"; 
    }
}
