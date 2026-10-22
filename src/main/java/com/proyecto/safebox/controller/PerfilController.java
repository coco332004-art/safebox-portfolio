package com.proyecto.safebox.controller;

import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.security.CustomUserDetails;
import com.proyecto.safebox.service.UsuariosService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controlador para la gestión del perfil del usuario autenticado (rol USER o ADMIN). 
// Permite que cada usuario edite su propio nombre, correo y contraseña desde la vista "/perfil".


@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuariosService usuariosService;

    public PerfilController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @GetMapping
    public String verPerfil(@AuthenticationPrincipal CustomUserDetails currentUser,
                            Model model) {
        Usuarios u = currentUser.getUsuario();

        if (!model.containsAttribute("userForm")) {
            Usuarios form = new Usuarios();
            form.setId(u.getId());
            form.setNombreCompleto(u.getNombreCompleto());
            form.setEmail(u.getEmail());
            form.setRol(u.getRol());
            form.setEnabled(u.isEnabled());
            form.setPassword(null); // no mostramos la real
            model.addAttribute("userForm", form);
        }

        return "safebox/perfil";
    }

    @PostMapping
    public String actualizarPerfil(@AuthenticationPrincipal CustomUserDetails currentUser,
                                   @Valid @ModelAttribute("userForm") Usuarios userForm,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes ra) {

        Long idActual = currentUser.getUsuario().getId();

        if (result.hasErrors()) {
            model.addAttribute("userForm", userForm);
            return "safebox/perfil";
        }

        try {
            // Reutilizamos el mismo actualizar del service
            usuariosService.actualizar(idActual, userForm);
            ra.addFlashAttribute("alertSuccess", "Perfil actualizado correctamente.");
            return "redirect:/perfil";

        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "email.duplicado", ex.getMessage());
            model.addAttribute("userForm", userForm);
            model.addAttribute("alertError", ex.getMessage());
            return "safebox/perfil";
        }
    }
}
