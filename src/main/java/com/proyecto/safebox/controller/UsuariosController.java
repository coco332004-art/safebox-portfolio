package com.proyecto.safebox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.proyecto.safebox.model.Usuarios;
import com.proyecto.safebox.service.UsuariosService;

import jakarta.validation.Valid;

// Controlador para la gestión de usuarios. Permite listar, crear, editar y eliminar usuarios a través de las rutas definidas bajo /usuarios.
// 


@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @GetMapping
    public String listar(Model model,
                 @RequestParam(value = "editId", required = false) Long editId) {

    model.addAttribute("isUserPerfil", false);
    model.addAttribute("usuarios", usuariosService.listar());
   

    if (!model.containsAttribute("userForm")) {
        if (editId != null) {
            usuariosService.obtener(editId).ifPresentOrElse(
                u -> model.addAttribute("userForm", u),
                () -> model.addAttribute("userForm", new Usuarios())
            );
            model.addAttribute("editMode", true);
        } else {
            model.addAttribute("userForm", new Usuarios());
            model.addAttribute("editMode", false);
        }
    }

    return "safebox/usuarios";
}
    
    @PostMapping
    public String crear(@Valid @ModelAttribute("userForm") Usuarios userForm,
                        BindingResult result,
                        RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.userForm", result);
            ra.addFlashAttribute("userForm", userForm);
            ra.addFlashAttribute("editMode", false);
            ra.addFlashAttribute("alertError", "Revisa los campos del formulario.");
            return "redirect:/usuarios";
        }
        try {
            usuariosService.crear(userForm);
            ra.addFlashAttribute("alertSuccess", "Usuario creado correctamente.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("alertError", ex.getMessage());
            ra.addFlashAttribute("userForm", userForm);
            ra.addFlashAttribute("editMode", false);
        } 
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, RedirectAttributes ra) {
        if (usuariosService.obtener(id).isEmpty()) {
            ra.addFlashAttribute("alertError", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
        ra.addAttribute("editId", id);
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                         @Valid @ModelAttribute("userForm") Usuarios userForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {

    // Errores de validación de anotaciones
    if (result.hasErrors()) {
        model.addAttribute("userForm", userForm);
        model.addAttribute("editMode", true);
        model.addAttribute("isUserPerfil", false);
        model.addAttribute("usuarios", usuariosService.listar());
        return "safebox/usuarios";
    }

    try {
        usuariosService.actualizar(id, userForm);
        ra.addFlashAttribute("alertSuccess", "Usuario actualizado correctamente.");
        return "redirect:/usuarios";

    } catch (IllegalArgumentException ex) {
        // Error de email duplicado u otro desde el servicio
        result.rejectValue("email", "email.duplicado", ex.getMessage());
        model.addAttribute("userForm", userForm);
        model.addAttribute("editMode", true);
        model.addAttribute("isUserPerfil", false);
        model.addAttribute("usuarios", usuariosService.listar());
        model.addAttribute("alertError", ex.getMessage());
        return "safebox/usuarios";
    }
}


    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuariosService.eliminar(id);
            ra.addFlashAttribute("alertSuccess", "Usuario eliminado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("alertError", ex.getMessage());
        }
        return "redirect:/usuarios";
    }
}
