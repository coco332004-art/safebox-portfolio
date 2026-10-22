package com.proyecto.safebox.controller;

import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.safebox.model.Mensajes;
import com.proyecto.safebox.security.CustomUserDetails;
import com.proyecto.safebox.service.MensajesService;
import com.proyecto.safebox.service.UsuariosService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/mensajes")
public class MensajesController {

    @InitBinder //Método @InitBinder que impide que ciertos campos (como fechas de creación/actualización) puedan ser modificados desde los formularios, evitando sobrescribir datos auditables
    public void initBinder(WebDataBinder binder) {
    // Evitar que Spring intente bindear estos campos desde el form
    binder.setDisallowedFields("fechaCreacion", "fechaActualizacion");
    }

    //Declaracion de dependencias de la clase Mensajescontroller
    private final MensajesService mensajesService;
    private final UsuariosService usuariosService;

    //Constru
    public MensajesController(MensajesService mensajesService, UsuariosService usuariosService) {
        this.mensajesService = mensajesService;
        this.usuariosService = usuariosService;
    }
    
    //Esto nos evita repetir lógica en cada método.
    private boolean isAdmin(UserDetails userDetails) {
    return userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
}

    //Filtra mensajes segun el usuario viene en la URL, el controlador usa el servicio para listar solo los mensajes de ese usuario.
    //Si no viene, entonces lista todos los mensajes.
    @GetMapping
    public String listar(@RequestParam(value = "usuarioId", required = false) Long usuarioId,
                     @RequestParam(value = "editId", required = false) Long editId,
                     @AuthenticationPrincipal CustomUserDetails currentUser,
                     Model model) {

    boolean admin = isAdmin(currentUser);

    if (admin) {
    if (usuarioId != null) {
        // ADMIN: lista sin descifrar
        model.addAttribute("mensajes", mensajesService.listarPorUsuarioSinDescifrar(usuarioId));
        model.addAttribute("usuarioId", usuarioId);
    } else {
        // ADMIN: lista sin descifrar
        model.addAttribute("mensajes", mensajesService.listarSinDescifrar());
    }
    model.addAttribute("usuarios", usuariosService.listar());
    } else {
        Long currentUserId = currentUser.getUsuario().getId();
        // USER: sigue usando el método que descifra
        model.addAttribute("mensajes", mensajesService.listarPorUsuario(currentUserId));
        model.addAttribute("usuarioId", currentUserId);
    }

    // Para el formulario de creación/edición
    Mensajes messageForm = new Mensajes();
    if (editId != null) {
    Optional<Mensajes> existenteOpt = mensajesService.obtener(editId);
    if (existenteOpt.isPresent()) {
        messageForm = existenteOpt.get();
    }
}
    model.addAttribute("esAdmin", admin);
    model.addAttribute("messageForm", new Mensajes());
    model.addAttribute("messageForm", messageForm);

    return "safebox/mensajes";
}

    //Usa @PostMapping porque el formulario se envía con POST.
    //Primero se valida el formulario usando @Valid y BindingResult.
    //Si hay errores, los devuelvo con RedirectAttributes para que se muestren en la vista.
    //Si los datos son correctos, llamo al servicio mensajesService.crear(), que asocia el mensaje al usuario y lo guarda en la base de datos.
    @PostMapping
    public String crear(@Valid @ModelAttribute("messageForm") Mensajes messageForm,
                    BindingResult result,
                    @RequestParam(value = "usuarioId", required = false) Long usuarioId,
                    @AuthenticationPrincipal CustomUserDetails currentUser,
                    RedirectAttributes ra,
                    Model model) {

    boolean admin = isAdmin(currentUser);
    Long targetUserId;

    if (admin) {
        // ADMIN: debe seleccionar explícitamente a qué usuario va el mensaje
        if (usuarioId == null) {
            result.rejectValue("usuario", "usuarioId.required", "Debe seleccionar un usuario");
        }
        targetUserId = usuarioId;
    } else {
        // USER: SIEMPRE crea mensajes para sí mismo
        targetUserId = currentUser.getUsuario().getId();
    }

    if (result.hasErrors()) {
        // Si hay errores, volvemos a cargar la lista de mensajes y el modelo necesario
        if (admin) {
            if (targetUserId != null) {
                model.addAttribute("mensajes", mensajesService.listarPorUsuario(targetUserId));
                model.addAttribute("usuarioId", targetUserId);
            } else {
                model.addAttribute("mensajes", mensajesService.listar());
            }
            model.addAttribute("usuarios", usuariosService.listar());
        } else {
            Long currentUserId = currentUser.getUsuario().getId();
            model.addAttribute("mensajes", mensajesService.listarPorUsuario(currentUserId));
            model.addAttribute("usuarioId", currentUserId);
        }

        model.addAttribute("messageForm", messageForm);
        return "safebox/mensajes";
    }

    mensajesService.crear(messageForm, targetUserId);
    ra.addFlashAttribute("successMessage", "Mensaje creado correctamente");

    return "redirect:/mensajes";
}

    //Edicion de un mensaje existente
    @GetMapping("/{id}/editar") //endpoint dinamico por el id
    public String editar(@PathVariable Long id, RedirectAttributes ra) {
        if (mensajesService.obtener(id).isEmpty()) {
            ra.addFlashAttribute("alertError", "Mensaje no encontrado.");
            return "redirect:/mensajes"; //Esto evita errores si el usuario intenta editar un mensaje que no existe.
        }
        ra.addAttribute("editId", id); //Si el mensaje sí existe, no lo edita directamente aquí. Lo que hace es agregar el parámetro editId al redirect:
        return "redirect:/mensajes";
    }
 
    //Actualizar el mensaje 
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, //Sirve para capturar valores directamente desde la URL y pasarlos como parámetros.
                             @Valid @ModelAttribute("messageForm") Mensajes messageForm,
                             BindingResult result,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.messageForm", result); //Se reenvían los datos del formulario (messageForm) para que el usuario no los pierda.
            ra.addFlashAttribute("messageForm", messageForm);
            ra.addFlashAttribute("editMode", true);
            ra.addFlashAttribute("alertError", "Revisa los campos del formulario.");
            return "redirect:/mensajes?editId=" + id; //Se redirige al mismo formulario en modo edición, donde el método listar() se encarga de recargar los datos en el formulario. 
        }
        try {
            mensajesService.actualizar(id, messageForm);
            ra.addFlashAttribute("alertSuccess", "Mensaje actualizado.");
        } catch (Exception ex) {
            ra.addFlashAttribute("alertError", ex.getMessage());
            ra.addFlashAttribute("messageForm", messageForm);
            ra.addFlashAttribute("editMode", true);
            return "redirect:/mensajes?editId=" + id; //Se redirige al mismo formulario en modo edición, donde el método listar() se encarga de recargar los datos en el formulario. 
        }
        return "redirect:/mensajes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id,
                       @AuthenticationPrincipal CustomUserDetails currentUser,
                       RedirectAttributes ra) {

    boolean admin = isAdmin(currentUser);

    // Buscar el mensaje
    Optional<Mensajes> mensajeOpt = mensajesService.obtener(id);
    if (mensajeOpt.isEmpty()) {
        ra.addFlashAttribute("errorMessage", "El mensaje no existe.");
        return "redirect:/mensajes";
    }

    Mensajes mensaje = mensajeOpt.get();

    // Si es USER, solo puede eliminar sus propios mensajes
    if (!admin) {
        Long currentUserId = currentUser.getUsuario().getId();
        if (!mensaje.getUsuario().getId().equals(currentUserId)) {
            ra.addFlashAttribute("errorMessage", "No puedes eliminar mensajes de otros usuarios.");
            return "redirect:/mensajes";
        }
    }

    // Si llegamos aquí, o es ADMIN, o es el dueño del mensaje
    mensajesService.eliminar(id);
    ra.addFlashAttribute("successMessage", "Mensaje eliminado correctamente.");

    return "redirect:/mensajes";
}
}
