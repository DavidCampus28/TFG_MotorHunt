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
import org.tfg.repository.MensajeRepository;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.Mensaje;
import org.tfg.model.entities.Usuario;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

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

    @GetMapping("/{usuarioId}/mis-coches")
    public ResponseEntity<?> getMisCoches(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Usuario propietario = usuarioOpt.get();
            List<Map<String, Object>> coches = cocheRepository.findByUsuarioId(usuarioId).stream()
                    .map(coche -> mapearCocheGestion(coche, propietario))
                    .toList();

            return ResponseEntity.ok(coches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    private Map<String, Object> mapearCocheGestion(Coche coche, Usuario propietario) {
        Map<String, Object> item = new HashMap<>();
        String portadaUrl = cocheFotoRepository.findByCocheAndPortadaTrue(coche)
                .or(() -> cocheFotoRepository.findFirstByCocheOrderByOrdenAsc(coche))
                .map(foto -> "/coches/fotos/" + foto.getId())
                .orElse("");

        item.put("id", coche.getId());
        item.put("marca", coche.getMarca());
        item.put("modelo", coche.getModelo());
        item.put("motor", coche.getMotor());
        item.put("color", coche.getColor());
        item.put("tipoCambio", coche.getTipoCambio().toString());
        item.put("combustible", coche.getCombustible().toString());
        item.put("numeroPuertas", coche.getNumeroPuertas());
        item.put("ubicacion", coche.getUbicacion());
        item.put("caballosPotencia", coche.getCaballosPotencia());
        item.put("ano", coche.getAno());
        item.put("kilometros", coche.getKilometros());
        item.put("precio", coche.getPrecio());
        item.put("numeroPlazas", coche.getNumeroPlazas());
        item.put("centimetrosCubicos", coche.getCentimetrosCubicos());
        item.put("etiquetaAmbiental", coche.getEtiquetaAmbiental().toString());
        item.put("estado", coche.getEstado().toString());
        item.put("descripcion", coche.getDescripcion() == null ? "" : coche.getDescripcion());
        item.put("fechaCreacion", coche.getFechaCreacion());
        item.put("fechaActualizacion", coche.getFechaActualizacion());
        item.put("fechaVenta", coche.getFechaVenta());
        item.put("numeroDenuncias", coche.getNumeroDenuncias() == null ? 0 : coche.getNumeroDenuncias());
        item.put("bloqueado", Boolean.TRUE.equals(coche.getBloqueado()));
        item.put("motivoBloqueado", coche.getMotivoBloqueado() == null ? "" : coche.getMotivoBloqueado());
        item.put("portadaUrl", portadaUrl);
        item.put("totalMeGustas", meGustaRepository.countByCoche(coche));

        List<Map<String, Object>> likes = meGustaRepository.findByCoche(coche).stream()
                .sorted(Comparator.comparing(mg -> mg.getFecha(), Comparator.nullsLast(Comparator.reverseOrder())))
                .map(mg -> {
                    Map<String, Object> like = new HashMap<>();
                    like.put("id", mg.getId());
                    like.put("usuarioId", mg.getUsuario().getId());
                    like.put("nombre", mg.getUsuario().getNombre());
                    like.put("email", mg.getUsuario().getEmail());
                    like.put("telefono", mg.getUsuario().getTelefono() == null ? "" : mg.getUsuario().getTelefono());
                    like.put("fecha", mg.getFecha());
                    return like;
                })
                .toList();
        item.put("likes", likes);

        Map<Long, Map<String, Object>> contactos = new LinkedHashMap<>();
        mensajeRepository.findAll().stream()
                .filter(m -> m.getCoche() != null && coche.getId().equals(m.getCoche().getId()))
                .filter(m -> m.getRemitente() != null && !m.getRemitente().getId().equals(propietario.getId()))
                .sorted(Comparator.comparing(Mensaje::getFechaEnvio, Comparator.nullsLast(Comparator.reverseOrder())))
                .forEach(m -> {
                    Usuario contacto = m.getRemitente();
                    Map<String, Object> info = contactos.computeIfAbsent(contacto.getId(), id -> {
                        Map<String, Object> nuevo = new HashMap<>();
                        nuevo.put("usuarioId", contacto.getId());
                        nuevo.put("nombre", contacto.getNombre());
                        nuevo.put("email", contacto.getEmail());
                        nuevo.put("telefono", contacto.getTelefono() == null ? "" : contacto.getTelefono());
                        nuevo.put("totalMensajes", 0);
                        nuevo.put("mensajesNoLeidos", 0);
                        nuevo.put("ultimoMensaje", "");
                        nuevo.put("ultimoMensajeFecha", null);
                        return nuevo;
                    });

                    info.put("totalMensajes", ((Integer) info.get("totalMensajes")) + 1);
                    if (Boolean.FALSE.equals(m.getLeido())) {
                        info.put("mensajesNoLeidos", ((Integer) info.get("mensajesNoLeidos")) + 1);
                    }
                    if (info.get("ultimoMensajeFecha") == null ||
                            (m.getFechaEnvio() != null && m.getFechaEnvio().isAfter((LocalDateTime) info.get("ultimoMensajeFecha")))) {
                        info.put("ultimoMensaje", m.getContenido());
                        info.put("ultimoMensajeFecha", m.getFechaEnvio());
                    }
                });
        item.put("contactos", contactos.values().stream().toList());

        return item;
    }
}

