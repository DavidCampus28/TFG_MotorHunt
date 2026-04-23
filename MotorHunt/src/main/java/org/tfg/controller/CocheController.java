package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.Usuario;
import org.tfg.model.enums.*;
import org.tfg.repository.CocheRepository;
import org.tfg.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/coches")
@CrossOrigin(origins = "*")
public class CocheController {

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Coche> getAllCoches() {
        return cocheRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coche> getCocheById(@PathVariable Long id) {
        return cocheRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Coche> getCochesByUsuario(@PathVariable Long usuarioId) {
        return cocheRepository.findByUsuarioId(usuarioId);
    }

    @PostMapping
    public Coche createCoche(@RequestBody Coche coche) {
        return cocheRepository.save(coche);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coche> updateCoche(@PathVariable Long id, @RequestBody Coche cocheDetails) {
        return cocheRepository.findById(id).map(coche -> {
            coche.setMarca(cocheDetails.getMarca());
            coche.setModelo(cocheDetails.getModelo());
            coche.setMotor(cocheDetails.getMotor());
            coche.setColor(cocheDetails.getColor());
            coche.setTipoCambio(cocheDetails.getTipoCambio());
            coche.setCombustible(cocheDetails.getCombustible());
            coche.setNumeroPuertas(cocheDetails.getNumeroPuertas());
            coche.setUbicacion(cocheDetails.getUbicacion());
            coche.setCaballosPotencia(cocheDetails.getCaballosPotencia());
            coche.setKilometros(cocheDetails.getKilometros());
            coche.setPrecio(cocheDetails.getPrecio());
            coche.setNumeroPlazas(cocheDetails.getNumeroPlazas());
            coche.setCentimetrosCubicos(cocheDetails.getCentimetrosCubicos());
            coche.setEtiquetaAmbiental(cocheDetails.getEtiquetaAmbiental());
            coche.setUsuario(cocheDetails.getUsuario());
            return ResponseEntity.ok(cocheRepository.save(coche));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoche(@PathVariable Long id) {
        return cocheRepository.findById(id).map(coche -> {
            cocheRepository.delete(coche);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearCoche(@RequestBody Map<String, Object> request) {
        try {
            Coche coche = new Coche();
            coche.setMarca((String) request.get("marca"));
            coche.setModelo((String) request.get("modelo"));
            coche.setMotor((String) request.get("motor"));
            coche.setColor((String) request.get("color"));
            coche.setTipoCambio(TipoCambio.valueOf(request.get("tipoCambio").toString()));
            coche.setCombustible(Combustible.valueOf(request.get("combustible").toString()));
            
            if (request.get("numeroPuertas") != null) {
                coche.setNumeroPuertas(((Number) request.get("numeroPuertas")).intValue());
            }
            coche.setUbicacion((String) request.get("ubicacion"));
            if (request.get("caballosPotencia") != null) {
                coche.setCaballosPotencia(((Number) request.get("caballosPotencia")).intValue());
            }
            if (request.get("km") != null) {
                coche.setKilometros(((Number) request.get("km")).intValue());
            }
            if (request.get("precio") != null) {
                coche.setPrecio(((Number) request.get("precio")).doubleValue());
            }
            if (request.get("numeroPlazas") != null) {
                coche.setNumeroPlazas(((Number) request.get("numeroPlazas")).intValue());
            }
            if (request.get("centimetrosCubicos") != null) {
                coche.setCentimetrosCubicos(((Number) request.get("centimetrosCubicos")).intValue());
            }
            coche.setEtiquetaAmbiental(EtiquetaAmbiental.valueOf(request.get("etiquetaAmbiental").toString()));
            coche.setEstado(EstadoCoche.EN_VENTA);
            coche.setDescripcion((String) request.get("descripcion"));
            if (request.get("ano") != null) {
                coche.setAno(((Number) request.get("ano")).intValue());
            }

            Long usuarioId = Long.parseLong(request.get("usuarioId").toString());
            Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
            if (usuario.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
            }

            coche.setUsuario(usuario.get());
            coche.setFechaCreacion(LocalDateTime.now());

            cocheRepository.save(coche);
            return ResponseEntity.ok(Map.of("mensaje", "Coche creado", "id", coche.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage(), "tipo", e.getClass().getSimpleName()));
        }
    }
}