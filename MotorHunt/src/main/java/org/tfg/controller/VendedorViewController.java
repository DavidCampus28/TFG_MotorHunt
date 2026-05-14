package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tfg.model.entities.Usuario;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.UsuarioRepository;

import java.util.Optional;

@Controller
public class VendedorViewController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    @GetMapping("/perfil-vendedor")
    public String perfilVendedor(@RequestParam Long id, Model model) {
        Optional<Usuario> vendedorOpt = usuarioRepository.findById(id);
        if (vendedorOpt.isEmpty()) {
            return "redirect:/";
        }

        Usuario vendedor = vendedorOpt.get();
        var coches = cocheRepository.findByUsuarioId(id);
        model.addAttribute("vendedor", vendedor);
        model.addAttribute("cochesVendedor", coches);
        model.addAttribute("totalCoches", coches.size());
        return "vendedor-perfil";
    }
}
