package com.proyecto.safebox.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto.safebox.repository.AuditoriaRepository;
import com.proyecto.safebox.repository.MensajesRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MensajesRepository mensajesRepo;
    private final AuditoriaRepository auditoriaRepo;

    public AdminController(MensajesRepository mensajesRepo,
                           AuditoriaRepository auditoriaRepo) {
        this.mensajesRepo = mensajesRepo;
        this.auditoriaRepo = auditoriaRepo;
    }


    // Conveniencia: /admin redirige al panel real (evita 404/errores en enlaces)
    @GetMapping
    public String root() {
        return "redirect:/admin/panel";
    }

    @GetMapping("/panel")
    public String panel(
            @RequestParam(defaultValue = "0") int msgPage,
            @RequestParam(defaultValue = "10") int msgSize,
            @RequestParam(defaultValue = "0") int audPage,
            @RequestParam(defaultValue = "10") int audSize,
            Model model
    ) {
        var mensajes = mensajesRepo.findAllByOrderByIdDesc(
                PageRequest.of(msgPage, msgSize)
        );

        var auditorias = auditoriaRepo.findAllByOrderByTimestampDesc(
                PageRequest.of(audPage, audSize)
        );

        model.addAttribute("mensajesPage", mensajes);
        model.addAttribute("auditoriasPage", auditorias);
        model.addAttribute("totalMensajes", mensajesRepo.count());
        model.addAttribute("totalAuditorias", auditoriaRepo.count());

        return "safebox/admin";
    }
}