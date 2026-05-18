package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tfg.dto.AuthResponse;
import org.tfg.dto.LoginRequest;
import org.tfg.dto.RegisterRequest;
import org.tfg.model.entities.Usuario;
import org.tfg.model.enums.Rol;
import org.tfg.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, String> request) {
        try {
            String nombre = request.get("nombre");
            String email = request.get("email");
            String password = request.get("password");
            String telefono = request.get("telefono");
            String direccion = request.get("direccion");
            String rolStr = request.getOrDefault("rol", "USUARIO");

            if (usuarioRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);

            // Asignar rol (USUARIO o EMPRESA)
            try {
                usuario.setRol(Rol.valueOf(rolStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                usuario.setRol(Rol.USUARIO);
            }

            usuario.setActivo(true);
            usuario.setFechaRegistro(LocalDateTime.now());

            usuarioRepository.save(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Registro exitoso");
            response.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRol().toString()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en el registro: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales inválidas");
            }

            Usuario usuario = usuarioOpt.get();

            if (!usuario.getActivo()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario desactivado");
            }

            if (Boolean.TRUE.equals(usuario.getBloqueado())) {
                String motivo = usuario.getMotivoBloqueado() != null && !usuario.getMotivoBloqueado().isBlank()
                        ? ": " + usuario.getMotivoBloqueado()
                        : "";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Cuenta bloqueada por administracion" + motivo);
            }

            if (!passwordEncoder.matches(password, usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales inválidas");
            }

            usuario.setUltimaActividad(LocalDateTime.now());
            usuarioRepository.save(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Login exitoso");
            response.put("token", "jwt_" + usuario.getId() + "_" + System.currentTimeMillis());
            response.put("usuario", Map.of(
                    "id", usuario.getId(),
                    "nombre", usuario.getNombre(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRol().toString(),
                    "telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "",
                    "direccion", usuario.getDireccion() != null ? usuario.getDireccion() : ""
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en login: " + e.getMessage());
        }
    }
}
