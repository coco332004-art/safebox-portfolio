
package com.proyecto.safebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controlador encargado de la pantalla de autenticación. Solo sirve la vista de login personalizada

@Controller
public class AuthController {

  @GetMapping("/login")
  public String login() {
    return "safebox/login";
  }
}
