package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.tfg.repository.CocheRepository;

@Controller
public class CocheViewController {

    @Autowired
    private CocheRepository cocheRepository;

    @GetMapping("/coches-view")
    public String listarCoches(Model model) {
        model.addAttribute("coches", cocheRepository.findAll());
        return "coches-index";
    }
}
