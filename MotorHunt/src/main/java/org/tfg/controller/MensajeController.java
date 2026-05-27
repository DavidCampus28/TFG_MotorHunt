package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.Mensaje;
import org.tfg.model.entities.Usuario;
import org.tfg.model.entities.Coche;
import org.tfg.repository.MensajeRepository;
import org.tfg.repository.UsuarioRepository;
import org.tfg.repository.CocheRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "*")
public class MensajeController {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    // Enviar mensaje
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarMensaje(@RequestParam Long remitenteId,
                                          @RequestParam Long destinatarioId,
                                          @RequestParam String contenido,
                                          @RequestParam(required = false) Long cocheId) {
        try {
            Optional<Usuario> remitenteOpt = usuarioRepository.findById(remitenteId);
            Optional<Usuario> destinatarioOpt = usuarioRepository.findById(destinatarioId);

            if (remitenteOpt.isEmpty() || destinatarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Mensaje mensaje = new Mensaje();
            mensaje.setRemitente(remitenteOpt.get());
            mensaje.setDestinatario(destinatarioOpt.get());
            mensaje.setContenido(contenido);

            if (cocheId != null) {
                Optional<Coche> cocheOpt = cocheRepository.findById(cocheId);
                if (cocheOpt.isPresent()) {
                    mensaje.setCoche(cocheOpt.get());
                }
            }

            Mensaje saved = mensajeRepository.save(mensaje);

            // Devolver un payload plano evita bucles de serialización JSON entre entidades relacionadas
            return ResponseEntity.ok(toMensajePayload(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Obtener conversación entre dos usuarios
    @GetMapping("/conversacion")
    public ResponseEntity<?> obtenerConversacion(@RequestParam Long usuarioId1,
                                                 @RequestParam Long usuarioId2) {
        try {
            Optional<Usuario> usuario1Opt = usuarioRepository.findById(usuarioId1);
            Optional<Usuario> usuario2Opt = usuarioRepository.findById(usuarioId2);

            if (usuario1Opt.isEmpty() || usuario2Opt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Usuario usuario1 = usuario1Opt.get();
            Usuario usuario2 = usuario2Opt.get();

            // Obtener la conversación entre ambos ordenada por fecha de envío (ascendente)
            List<Mensaje> mensajes = mensajeRepository.findConversationBetween(usuario1.getId(), usuario2.getId());

            // Marcar como leídos (para el usuario que solicita la conversación)
            mensajes.forEach(m -> {
                if (m.getDestinatario().getId().equals(usuarioId1) && !m.getLeido()) {
                    m.setLeido(true);
                    m.setFechaLectura(LocalDateTime.now());
                    mensajeRepository.save(m);
                }
            });

            List<Map<String, Object>> payload = mensajes.stream()
                    .map(this::toMensajePayload)
                    .toList();

            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Obtener chats del usuario (último mensaje con cada contacto)
    @GetMapping("/mis-chats/{usuarioId}")
    public ResponseEntity<?> obtenerMisChats(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // Obtener todos los mensajes donde el usuario es remitente o destinatario
            List<Mensaje> mensajes = mensajeRepository.findByRemitenteOrDestinatario(usuarioOpt.get());

            // Agrupar por contacto y obtener el último mensaje
            Map<Long, Map<String, Object>> chats = new HashMap<>();

            mensajes.forEach(m -> {
                Long contactoId = m.getRemitente().getId().equals(usuarioId) ?
                                  m.getDestinatario().getId() : m.getRemitente().getId();
                Usuario contacto = m.getRemitente().getId().equals(usuarioId) ?
                                  m.getDestinatario() : m.getRemitente();

                if (!chats.containsKey(contactoId) ||
                    m.getFechaEnvio().isAfter(
                        ((LocalDateTime) chats.get(contactoId).get("ultimoMensajeHora"))
                    )) {
                    Map<String, Object> chatInfo = new HashMap<>();
                    chatInfo.put("contactoId", contactoId);
                    chatInfo.put("contactoNombre", contacto.getNombre());
                    chatInfo.put("contactoEmail", contacto.getEmail());
                    chatInfo.put("ultimoMensaje", m.getContenido());
                    chatInfo.put("ultimoMensajeHora", m.getFechaEnvio());
                    chatInfo.put("noLeidos", 0);
                    chats.put(contactoId, chatInfo);
                }

                if (m.getDestinatario().getId().equals(usuarioId) && !m.getLeido()) {
                    Map<String, Object> chatInfo = chats.get(contactoId);
                    chatInfo.put("noLeidos", ((Integer) chatInfo.get("noLeidos")) + 1);
                }
            });

            List<Map<String, Object>> resultado = new ArrayList<>(chats.values());
            resultado.sort(Comparator.comparing(chat -> (LocalDateTime) chat.get("ultimoMensajeHora"), Comparator.reverseOrder()));

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Obtener mensaje por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMensaje(@PathVariable Long id) {
        try {
            Optional<Mensaje> mensajeOpt = mensajeRepository.findById(id);
            if (mensajeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mensajeOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Marcar mensaje como leído
    @PutMapping("/{id}/marcar-leido")
    public ResponseEntity<?> marcarLeido(@PathVariable Long id) {
        try {
            Optional<Mensaje> mensajeOpt = mensajeRepository.findById(id);
            if (mensajeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Mensaje mensaje = mensajeOpt.get();
            mensaje.setLeido(true);
            mensaje.setFechaLectura(LocalDateTime.now());
            mensajeRepository.save(mensaje);

            return ResponseEntity.ok(Map.of("mensaje", "Marcado como leído"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Eliminar mensaje
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMensaje(@PathVariable Long id) {
        try {
            Optional<Mensaje> mensajeOpt = mensajeRepository.findById(id);
            if (mensajeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            mensajeRepository.delete(mensajeOpt.get());
            return ResponseEntity.ok(Map.of("mensaje", "Mensaje eliminado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    private Map<String, Object> toMensajePayload(Mensaje mensaje) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", mensaje.getId());
        payload.put("contenido", mensaje.getContenido());
        payload.put("fechaEnvio", mensaje.getFechaEnvio());
        payload.put("leido", mensaje.getLeido());
        payload.put("fechaLectura", mensaje.getFechaLectura());
        payload.put("remitenteId", mensaje.getRemitente() != null ? mensaje.getRemitente().getId() : null);
        payload.put("destinatarioId", mensaje.getDestinatario() != null ? mensaje.getDestinatario().getId() : null);
        payload.put("cocheId", mensaje.getCoche() != null ? mensaje.getCoche().getId() : null);
        return payload;
    }
}

