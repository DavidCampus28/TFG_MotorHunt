package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.UsuarioRepository;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("coches", cocheRepository.findAll());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "perfil";
    }
}
