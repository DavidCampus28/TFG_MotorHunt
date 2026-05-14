package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.Usuario;
import org.tfg.model.enums.*;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.UsuarioRepository;
import org.tfg.repository.MensajeRepository;
import org.tfg.repository.MeGustaRepository;
import org.tfg.model.entities.Mensaje;
import org.tfg.model.entities.MeGusta;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private MeGustaRepository meGustaRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // USUARIOS
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        try {
            var usuarios = usuarioRepository.findAll().stream().map(u -> Map.of(
                    "id", u.getId(),
                    "nombre", u.getNombre(),
                    "email", u.getEmail(),
                    "telefono", u.getTelefono() != null ? u.getTelefono() : "",
                    "direccion", u.getDireccion() != null ? u.getDireccion() : "",
                    "rol", u.getRol().toString(),
                    "activo", u.getActivo(),
                    "fechaRegistro", u.getFechaRegistro()
            )).toList();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> request) {
        try {
            String nombre = (String) request.get("nombre");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String telefono = (String) request.get("telefono");
            String direccion = (String) request.get("direccion");
            String rolStr = (String) request.get("rol");

            if (usuarioRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("Email ya registrado");
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            usuario.setRol(rolStr.equals("ADMINISTRADOR") ? Rol.ADMINISTRADOR : Rol.USUARIO);
            usuario.setActivo(true);
            usuario.setFechaRegistro(LocalDateTime.now());

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "id", usuarioGuardado.getId(),
                    "mensaje", "Usuario creado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Usuario u = usuario.get();
            return ResponseEntity.ok(Map.of(
                    "id", u.getId(),
                    "nombre", u.getNombre(),
                    "email", u.getEmail(),
                    "telefono", u.getTelefono() != null ? u.getTelefono() : "",
                    "direccion", u.getDireccion() != null ? u.getDireccion() : "",
                    "rol", u.getRol().toString(),
                    "activo", u.getActivo(),
                    "fechaRegistro", u.getFechaRegistro()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // COCHES
    @GetMapping("/coches")
    public ResponseEntity<?> listarCoches() {
        try {
            var coches = cocheRepository.findAll().stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("marca", c.getMarca());
                map.put("modelo", c.getModelo());
                map.put("ano", c.getAno());
                map.put("anio", c.getAno());
                map.put("motor", c.getMotor());
                map.put("tipoCambio", c.getTipoCambio().toString());
                map.put("combustible", c.getCombustible().toString());
                map.put("etiquetaAmbiental", c.getEtiquetaAmbiental().toString());
                map.put("precio", c.getPrecio());
                map.put("kilometros", c.getKilometros());
                map.put("km", c.getKilometros());
                map.put("color", c.getColor());
                map.put("descripcion", c.getDescripcion() != null ? c.getDescripcion() : "");
                map.put("usuarioId", c.getUsuario().getId());
                map.put("vendedor", c.getUsuario().getNombre());
                map.put("estado", c.getEstado().toString());
                return map;
            }).toList();
            return ResponseEntity.ok(coches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/coches")
    public ResponseEntity<?> crearCoche(@RequestBody Map<String, Object> request) {
        try {
            Coche coche = new Coche();
            coche.setMarca(requiredString(request, "marca"));
            coche.setModelo(requiredString(request, "modelo"));
            coche.setMotor(optionalString(request, "motor", "No especificado"));
            coche.setColor(optionalString(request, "color", ""));
            coche.setTipoCambio(TipoCambio.valueOf(request.get("tipoCambio").toString()));
            coche.setCombustible(Combustible.valueOf(request.get("combustible").toString()));
            coche.setNumeroPuertas(intValue(request, "numeroPuertas", 5));
            coche.setUbicacion(optionalString(request, "ubicacion", "No especificada"));
            coche.setCaballosPotencia(intValue(request, "caballosPotencia", 0));
            coche.setKilometros(intValue(request, "kilometros", "km", 0));
            coche.setPrecio(Double.parseDouble(request.get("precio").toString()));
            coche.setNumeroPlazas(intValue(request, "numeroPlazas", 5));
            coche.setCentimetrosCubicos(intValue(request, "centimetrosCubicos", 0));
            coche.setEtiquetaAmbiental(EtiquetaAmbiental.valueOf(request.get("etiquetaAmbiental").toString()));
            coche.setEstado(EstadoCoche.EN_VENTA);
            coche.setDescripcion(optionalString(request, "descripcion", ""));
            coche.setAno(intValue(request, "ano", "anio", 0));

            Long usuarioId = Long.parseLong(request.get("usuarioId").toString());
            Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
            if (usuario.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            coche.setUsuario(usuario.get());
            coche.setFechaCreacion(LocalDateTime.now());

            Coche cocheGuardado = cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of(
                    "id", cocheGuardado.getId(),
                    "mensaje", "Coche creado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/coches/{id}")
    public ResponseEntity<?> eliminarCoche(@PathVariable Long id) {
        try {
            Optional<Coche> coche = cocheRepository.findById(id);
            if (coche.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            cocheRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Coche eliminado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/coches/{id}")
    public ResponseEntity<?> obtenerCoche(@PathVariable Long id) {
        try {
            Optional<Coche> coche = cocheRepository.findById(id);
            if (coche.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Coche c = coche.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", c.getId());
            response.put("marca", c.getMarca());
            response.put("modelo", c.getModelo());
            response.put("motor", c.getMotor());
            response.put("color", c.getColor());
            response.put("tipoCambio", c.getTipoCambio().toString());
            response.put("combustible", c.getCombustible().toString());
            response.put("numeroPuertas", c.getNumeroPuertas());
            response.put("ubicacion", c.getUbicacion());
            response.put("caballosPotencia", c.getCaballosPotencia());
            response.put("kilometros", c.getKilometros());
            response.put("precio", c.getPrecio());
            response.put("numeroPlazas", c.getNumeroPlazas());
            response.put("centimetrosCubicos", c.getCentimetrosCubicos());
            response.put("etiquetaAmbiental", c.getEtiquetaAmbiental().toString());
            response.put("estado", c.getEstado().toString());
            response.put("descripcion", c.getDescripcion());
            response.put("ano", c.getAno());
            response.put("anio", c.getAno());
            response.put("km", c.getKilometros());
            response.put("vendedor", c.getUsuario().getNombre());
            response.put("vendedorId", c.getUsuario().getId());
            response.put("usuarioId", c.getUsuario().getId());
            response.put("fechaCreacion", c.getFechaCreacion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Usuario usuario = usuarioOpt.get();
            
            if (request.containsKey("nombre")) usuario.setNombre((String) request.get("nombre"));
            if (request.containsKey("email")) usuario.setEmail((String) request.get("email"));
            if (request.containsKey("telefono")) usuario.setTelefono((String) request.get("telefono"));
            if (request.containsKey("direccion")) usuario.setDireccion((String) request.get("direccion"));
            if (request.containsKey("rol")) {
                String rolStr = (String) request.get("rol");
                usuario.setRol(rolStr.equals("ADMINISTRADOR") ? Rol.ADMINISTRADOR : Rol.USUARIO);
            }
            if (request.containsKey("activo")) usuario.setActivo((Boolean) request.get("activo"));
            
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/coches/{id}")
    public ResponseEntity<?> editarCoche(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Coche> cocheOpt = cocheRepository.findById(id);
            if (cocheOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Coche coche = cocheOpt.get();
            
            if (request.containsKey("marca")) coche.setMarca((String) request.get("marca"));
            if (request.containsKey("modelo")) coche.setModelo((String) request.get("modelo"));
            if (request.containsKey("motor")) coche.setMotor((String) request.get("motor"));
            if (request.containsKey("color")) coche.setColor((String) request.get("color"));
            if (request.containsKey("tipoCambio")) {
                String tipoCambioStr = (String) request.get("tipoCambio");
                coche.setTipoCambio(TipoCambio.valueOf(tipoCambioStr));
            }
            if (request.containsKey("combustible")) {
                String combustibleStr = (String) request.get("combustible");
                coche.setCombustible(Combustible.valueOf(combustibleStr));
            }
            if (request.containsKey("numeroPuertas") && request.get("numeroPuertas") != null) {
                coche.setNumeroPuertas(((Number) request.get("numeroPuertas")).intValue());
            }
            if (request.containsKey("ubicacion")) coche.setUbicacion((String) request.get("ubicacion"));
            if (request.containsKey("caballosPotencia") && request.get("caballosPotencia") != null) {
                coche.setCaballosPotencia(((Number) request.get("caballosPotencia")).intValue());
            }
            if (request.containsKey("kilometros") && request.get("kilometros") != null) {
                coche.setKilometros(((Number) request.get("kilometros")).intValue());
            }
            if (request.containsKey("precio") && request.get("precio") != null) {
                coche.setPrecio(((Number) request.get("precio")).doubleValue());
            }
            if (request.containsKey("numeroPlazas") && request.get("numeroPlazas") != null) {
                coche.setNumeroPlazas(((Number) request.get("numeroPlazas")).intValue());
            }
            if (request.containsKey("centimetrosCubicos") && request.get("centimetrosCubicos") != null) {
                coche.setCentimetrosCubicos(((Number) request.get("centimetrosCubicos")).intValue());
            }
            if (request.containsKey("etiquetaAmbiental")) {
                String etiquetaStr = (String) request.get("etiquetaAmbiental");
                coche.setEtiquetaAmbiental(EtiquetaAmbiental.valueOf(etiquetaStr));
            }
            if (request.containsKey("estado")) {
                String estadoStr = (String) request.get("estado");
                coche.setEstado(EstadoCoche.valueOf(estadoStr));
            }
            if (request.containsKey("descripcion")) coche.setDescripcion((String) request.get("descripcion"));
            if (request.containsKey("ano") && request.get("ano") != null) {
                coche.setAno(((Number) request.get("ano")).intValue());
            }
            
            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Coche actualizado"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage(), "tipo", e.getClass().getSimpleName()));
        }
    }

    // Ver todos los mensajes (conversaciones)
    @GetMapping("/conversaciones")
    public ResponseEntity<?> verConversaciones() {
        try {
            List<Mensaje> mensajes = mensajeRepository.findAll();
            var resultado = mensajes.stream().map(m -> Map.of(
                    "id", m.getId(),
                    "remitente", m.getRemitente().getNombre(),
                    "remitenteId", m.getRemitente().getId(),
                    "destinatario", m.getDestinatario().getNombre(),
                    "destinatarioId", m.getDestinatario().getId(),
                    "contenido", m.getContenido(),
                    "coche", m.getCoche() != null ? m.getCoche().getMarca() + " " + m.getCoche().getModelo() : "Sin coche",
                    "leido", m.getLeido(),
                    "fecha", m.getFechaEnvio()
            )).toList();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Ver conversación específica entre dos usuarios
    @GetMapping("/conversacion/{usuarioId1}/{usuarioId2}")
    public ResponseEntity<?> verConversacion(@PathVariable Long usuarioId1, @PathVariable Long usuarioId2) {
        try {
            Optional<Usuario> usuario1Opt = usuarioRepository.findById(usuarioId1);
            Optional<Usuario> usuario2Opt = usuarioRepository.findById(usuarioId2);

            if (usuario1Opt.isEmpty() || usuario2Opt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            Usuario usuario1 = usuario1Opt.get();
            Usuario usuario2 = usuario2Opt.get();

            List<Mensaje> mensajes = mensajeRepository.findAll().stream()
                    .filter(m -> (m.getRemitente().getId().equals(usuario1.getId()) && 
                                 m.getDestinatario().getId().equals(usuario2.getId())) ||
                                (m.getRemitente().getId().equals(usuario2.getId()) && 
                                 m.getDestinatario().getId().equals(usuario1.getId())))
                    .toList();

            var resultado = mensajes.stream().map(m -> Map.of(
                    "id", m.getId(),
                    "remitente", m.getRemitente().getNombre(),
                    "contenido", m.getContenido(),
                    "leido", m.getLeido(),
                    "fecha", m.getFechaEnvio()
            )).toList();

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Ver me gustas de todos los usuarios
    @GetMapping("/me-gustas")
    public ResponseEntity<?> verMeGustas() {
        try {
            List<MeGusta> meGustas = meGustaRepository.findAll();
            var resultado = meGustas.stream().map(mg -> Map.of(
                    "id", mg.getId(),
                    "usuario", mg.getUsuario().getNombre(),
                    "usuarioId", mg.getUsuario().getId(),
                    "coche", mg.getCoche().getMarca() + " " + mg.getCoche().getModelo() + " (" + mg.getCoche().getAno() + ")",
                    "cocheId", mg.getCoche().getId(),
                    "fecha", mg.getFecha()
            )).toList();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Ver me gustas de un usuario específico
    @GetMapping("/me-gustas/{usuarioId}")
    public ResponseEntity<?> verMeGustasPorUsuario(@PathVariable Long usuarioId) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            List<MeGusta> meGustas = meGustaRepository.findByUsuario(usuarioOpt.get());
            var resultado = meGustas.stream().map(mg -> Map.of(
                    "id", mg.getId(),
                    "coche", mg.getCoche().getMarca() + " " + mg.getCoche().getModelo(),
                    "cocheId", mg.getCoche().getId(),
                    "precio", mg.getCoche().getPrecio(),
                    "fecha", mg.getFecha()
            )).toList();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private String requiredString(Map<String, Object> request, String key) {
        Object value = request.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("El campo " + key + " es obligatorio");
        }
        return value.toString();
    }

    private String optionalString(Map<String, Object> request, String key, String defaultValue) {
        Object value = request.get(key);
        return value == null || value.toString().isBlank() ? defaultValue : value.toString();
    }

    private int intValue(Map<String, Object> request, String key, int defaultValue) {
        Object value = request.get(key);
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }
        return ((Number) value).intValue();
    }

    private int intValue(Map<String, Object> request, String primaryKey, String fallbackKey, int defaultValue) {
        Object value = request.containsKey(primaryKey) ? request.get(primaryKey) : request.get(fallbackKey);
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }
        return ((Number) value).intValue();
    }
}
