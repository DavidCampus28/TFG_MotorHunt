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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // USUARIOS
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

            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of("mensaje", "Usuario creado"));
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
    @PostMapping("/coches")
    public ResponseEntity<?> crearCoche(@RequestBody Map<String, Object> request) {
        try {
            Coche coche = new Coche();
            coche.setMarca((String) request.get("marca"));
            coche.setModelo((String) request.get("modelo"));
            coche.setMotor((String) request.get("motor"));
            coche.setColor((String) request.get("color"));
            coche.setTipoCambio(TipoCambio.valueOf(request.get("tipoCambio").toString()));
            coche.setCombustible(Combustible.valueOf(request.get("combustible").toString()));
            coche.setNumeroPuertas((Integer) request.get("numeroPuertas"));
            coche.setUbicacion((String) request.get("ubicacion"));
            coche.setCaballosPotencia((Integer) request.get("caballosPotencia"));
            coche.setKilometros((Integer) request.get("kilometros"));
            coche.setPrecio(Double.parseDouble(request.get("precio").toString()));
            coche.setNumeroPlazas((Integer) request.get("numeroPlazas"));
            coche.setCentimetrosCubicos((Integer) request.get("centimetrosCubicos"));
            coche.setEtiquetaAmbiental(EtiquetaAmbiental.valueOf(request.get("etiquetaAmbiental").toString()));
            coche.setEstado(EstadoCoche.EN_VENTA);
            coche.setDescripcion((String) request.get("descripcion"));
            coche.setAno((Integer) request.get("ano"));

            Long usuarioId = Long.parseLong(request.get("usuarioId").toString());
            Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
            if (usuario.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            coche.setUsuario(usuario.get());
            coche.setFechaCreacion(LocalDateTime.now());

            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Coche creado"));
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
            response.put("vendedor", c.getUsuario().getNombre());
            response.put("vendedorId", c.getUsuario().getId());
            response.put("fechaCreacion", c.getFechaCreacion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
