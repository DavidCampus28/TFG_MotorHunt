package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.Usuario;
import org.tfg.repository.CocheRepository;

import java.util.List;

@RestController
@RequestMapping("/coches")
public class CocheController {

    @Autowired
    private CocheRepository cocheRepository;

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
}