package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.Denuncia;
import org.tfg.model.entities.Usuario;
import org.tfg.model.enums.EstadoDenuncia;
import org.tfg.model.enums.TipoDenuncia;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.DenunciaRepository;
import org.tfg.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/denuncias")
@CrossOrigin(origins = "*")
public class DenunciaController {

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    @PostMapping("/coche/{cocheId}")
    public ResponseEntity<?> reportarCoche(@PathVariable Long cocheId, @RequestBody Map<String, Object> request) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Coche no encontrado");
            }

            Optional<Usuario> denunciante = obtenerDenunciante(request);
            if (denunciante.isEmpty()) {
                return ResponseEntity.badRequest().body("Debes iniciar sesión para denunciar");
            }

            Denuncia denuncia = new Denuncia();
            denuncia.setTipo(TipoDenuncia.valueOf((String) request.get("tipo")));
            denuncia.setEstado(EstadoDenuncia.PENDIENTE);
            denuncia.setDescripcion((String) request.get("descripcion"));
            denuncia.setCocheDenunciado(cocheOpt.get());
            denuncia.setDenunciante(denunciante.get());

            Denuncia guardada = denunciaRepository.save(denuncia);
            return ResponseEntity.ok(Map.of("id", guardada.getId(), "mensaje", "Anuncio reportado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/usuario/{usuarioDenunciadoId}")
    public ResponseEntity<?> reportarUsuario(@PathVariable Long usuarioDenunciadoId, @RequestBody Map<String, Object> request) {
        try {
            Optional<Usuario> usuarioDenunciadoOpt = usuarioRepository.findById(usuarioDenunciadoId);
            if (usuarioDenunciadoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Optional<Usuario> denunciante = obtenerDenunciante(request);
            if (denunciante.isEmpty()) {
                return ResponseEntity.badRequest().body("Debes iniciar sesión para denunciar");
            }

            Denuncia denuncia = new Denuncia();
            denuncia.setTipo(TipoDenuncia.valueOf((String) request.get("tipo")));
            denuncia.setEstado(EstadoDenuncia.PENDIENTE);
            denuncia.setDescripcion((String) request.get("descripcion"));
            denuncia.setUsuarioDenunciado(usuarioDenunciadoOpt.get());
            denuncia.setDenunciante(denunciante.get());

            Denuncia guardada = denunciaRepository.save(denuncia);
            return ResponseEntity.ok(Map.of("id", guardada.getId(), "mensaje", "Usuario reportado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}/recibidas")
    public ResponseEntity<?> verDenunciasRecibidas(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            List<Denuncia> denuncias = denunciaRepository.findByUsuarioDenunciado(usuarioOpt.get());
            return ResponseEntity.ok(denuncias.stream().map(this::mapearDenuncia).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}/reportadas")
    public ResponseEntity<?> verDenunciasReportadas(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            List<Denuncia> denuncias = denunciaRepository.findByDenunciante(usuarioOpt.get());
            return ResponseEntity.ok(denuncias.stream().map(this::mapearDenuncia).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}/todas")
    public ResponseEntity<?> verTodasDenunciasUsuario(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();
            List<Map<String, Object>> resultado = new ArrayList<>();

            denunciaRepository.findByDenunciante(usuario).forEach(d -> {
                Map<String, Object> map = mapearDenuncia(d);
                map.put("relacion", "ENVIADA");
                resultado.add(map);
            });

            denunciaRepository.findByUsuarioDenunciado(usuario).forEach(d -> {
                Map<String, Object> map = mapearDenuncia(d);
                map.put("relacion", "RECIBIDA");
                resultado.add(map);
            });

            cocheRepository.findByUsuarioId(usuarioId).forEach(coche ->
                    denunciaRepository.findByCocheDenunciado(coche).forEach(d -> {
                        Map<String, Object> map = mapearDenuncia(d);
                        map.put("relacion", "ANUNCIO_PROPIO");
                        resultado.add(map);
                    })
            );

            resultado.sort((a, b) -> ((LocalDateTime) b.get("fechaCreacion")).compareTo((LocalDateTime) a.get("fechaCreacion")));
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/coche/{cocheId}")
    public ResponseEntity<?> verDenunciasCoche(@PathVariable Long cocheId) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Coche no encontrado");
            }

            List<Denuncia> denuncias = denunciaRepository.findByCocheDenunciado(cocheOpt.get());
            return ResponseEntity.ok(denuncias.stream().map(this::mapearDenuncia).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private Optional<Usuario> obtenerDenunciante(Map<String, Object> request) {
        Object denuncianteId = request.get("denuncianteId");
        if (denuncianteId == null) {
            return Optional.empty();
        }
        Long id = ((Number) denuncianteId).longValue();
        return usuarioRepository.findById(id);
    }

    private Map<String, Object> mapearDenuncia(Denuncia d) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", d.getId());
        map.put("tipo", d.getTipo().toString());
        map.put("estado", d.getEstado().toString());
        map.put("descripcion", d.getDescripcion());
        map.put("denunciante", d.getDenunciante() != null ? d.getDenunciante().getNombre() : "Anonimo");
        map.put("denuncianteId", d.getDenunciante() != null ? d.getDenunciante().getId() : null);
        map.put("usuarioDenunciado", d.getUsuarioDenunciado() != null ? d.getUsuarioDenunciado().getNombre() : "-");
        map.put("usuarioDenunciadoId", d.getUsuarioDenunciado() != null ? d.getUsuarioDenunciado().getId() : null);
        map.put("coche", d.getCocheDenunciado() != null ? d.getCocheDenunciado().getMarca() + " " + d.getCocheDenunciado().getModelo() : "-");
        map.put("cocheId", d.getCocheDenunciado() != null ? d.getCocheDenunciado().getId() : null);
        map.put("fechaCreacion", d.getFechaCreacion());
        map.put("fechaResolucion", d.getFechaResolucion());
        map.put("resolucion", d.getResolucion());
        return map;
    }
}
