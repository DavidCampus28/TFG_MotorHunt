package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.tfg.repository.UsuarioRepository;

@Controller
public class UsuarioViewController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/usuarios-view")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios-index";
    }
}
