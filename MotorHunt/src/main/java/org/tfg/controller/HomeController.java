package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.tfg.model.enums.EstadoCoche;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.UsuarioRepository;

@Controller
public class HomeController {

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("coches", cocheRepository.findAll());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "login";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("coches", cocheRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("totalCoches", cocheRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "admin-dashboard";
    }
}
