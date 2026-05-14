package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.CocheFotoRepository;
import org.tfg.repository.UsuarioRepository;
import org.tfg.repository.MeGustaRepository;
import org.tfg.model.entities.Usuario;
import java.util.Map;
import java.util.Optional;

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

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
class PerfilRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MeGustaRepository meGustaRepository;

    @Autowired
    private CocheFotoRepository cocheFotoRepository;

    @GetMapping("/{usuarioId}/me-gustas")
    public ResponseEntity<?> getMeGustasPerfil(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            var meGustas = meGustaRepository.findByUsuario(usuarioOpt.get()).stream()
                    .map(mg -> {
                        var coche = mg.getCoche();
                        String portadaUrl = cocheFotoRepository.findByCocheAndPortadaTrue(coche)
                                .or(() -> cocheFotoRepository.findFirstByCocheOrderByOrdenAsc(coche))
                                .map(foto -> "/coches/fotos/" + foto.getId())
                                .orElse(null);

                        return Map.of(
                            "id", mg.getId(),
                            "cocheId", coche.getId(),
                            "marca", coche.getMarca(),
                            "modelo", coche.getModelo(),
                            "ano", coche.getAno(),
                            "precio", coche.getPrecio(),
                            "estado", coche.getEstado().toString(),
                            "portadaUrl", portadaUrl == null ? "" : portadaUrl,
                            "fecha", mg.getFecha()
                        );
                    }).toList();

            return ResponseEntity.ok(meGustas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

