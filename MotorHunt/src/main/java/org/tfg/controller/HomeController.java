package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.tfg.repository.CocheFotoRepository;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.UsuarioRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private CocheFotoRepository cocheFotoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String home(Model model) {
        var coches = cocheRepository.findAll();
        Map<Long, String> portadas = new HashMap<>();
        Map<Long, String> tiposVendedor = new HashMap<>();
        coches.forEach(coche -> cocheFotoRepository.findByCocheAndPortadaTrue(coche)
                .or(() -> cocheFotoRepository.findFirstByCocheOrderByOrdenAsc(coche))
                .ifPresent(foto -> portadas.put(coche.getId(), "/coches/fotos/" + foto.getId())));
        coches.forEach(coche -> {
            boolean esEmpresa = coche.getUsuario() != null
                    && (coche.getUsuario().getRol() == org.tfg.model.enums.Rol.EMPRESA
                    || coche.getUsuario().getTipoVendedor() == org.tfg.model.enums.TipoVendedor.EMPRESA);
            tiposVendedor.put(coche.getId(), esEmpresa ? "Empresa" : "Particular");
        });

        model.addAttribute("coches", coches);
        model.addAttribute("portadas", portadas);
        model.addAttribute("tiposVendedor", tiposVendedor);
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

    @GetMapping("/terminos")
    public String terminos() {
        return "terminos";
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
