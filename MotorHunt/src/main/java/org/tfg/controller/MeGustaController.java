package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.MeGusta;
import org.tfg.model.entities.Usuario;
import org.tfg.model.entities.Coche;
import org.tfg.repository.MeGustaRepository;
import org.tfg.repository.UsuarioRepository;
import org.tfg.repository.CocheRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me-gusta")
@CrossOrigin(origins = "*")
public class MeGustaController {

    @Autowired
    private MeGustaRepository meGustaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    // Agregar me gusta
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarMeGusta(@RequestParam Long usuarioId, @RequestParam Long cocheId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);

            if (usuarioOpt.isEmpty() || cocheOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario o coche no encontrado");
            }

            Usuario usuario = usuarioOpt.get();
            Coche coche = cocheOpt.get();

            if (meGustaRepository.existsByUsuarioAndCoche(usuario, coche)) {
                return ResponseEntity.badRequest().body("Ya le has dado me gusta a este coche");
            }

            MeGusta meGusta = new MeGusta();
            meGusta.setUsuario(usuario);
            meGusta.setCoche(coche);
            meGustaRepository.save(meGusta);

            return ResponseEntity.ok(Map.of("mensaje", "Me gusta agregado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Eliminar me gusta
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarMeGusta(@RequestParam Long usuarioId, @RequestParam Long cocheId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);

            if (usuarioOpt.isEmpty() || cocheOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario o coche no encontrado");
            }

            Optional<MeGusta> meGustaOpt = meGustaRepository.findByUsuarioAndCoche(usuarioOpt.get(), cocheOpt.get());
            if (meGustaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Me gusta no encontrado");
            }

            meGustaRepository.delete(meGustaOpt.get());
            return ResponseEntity.ok(Map.of("mensaje", "Me gusta eliminado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Obtener me gustas del usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getMeGustasPorUsuario(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            List<MeGusta> meGustas = meGustaRepository.findByUsuario(usuarioOpt.get());
            return ResponseEntity.ok(meGustas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Verificar si usuario tiene me gusta en un coche
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarMeGusta(@RequestParam Long usuarioId, @RequestParam Long cocheId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);

            if (usuarioOpt.isEmpty() || cocheOpt.isEmpty()) {
                return ResponseEntity.ok(Map.of("existe", false));
            }

            boolean existe = meGustaRepository.existsByUsuarioAndCoche(usuarioOpt.get(), cocheOpt.get());
            return ResponseEntity.ok(Map.of("existe", existe));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Contar me gustas del coche
    @GetMapping("/contar/{cocheId}")
    public ResponseEntity<?> contarMeGustas(@PathVariable Long cocheId) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Coche no encontrado");
            }

            long cantidad = meGustaRepository.countByCoche(cocheOpt.get());
            return ResponseEntity.ok(Map.of("cantidad", cantidad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

