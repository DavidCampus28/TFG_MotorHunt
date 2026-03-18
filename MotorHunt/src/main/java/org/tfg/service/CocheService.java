package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tfg.model.entities.Coche;
import org.tfg.repository.CocheRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CocheService {

    @Autowired
    private CocheRepository cocheRepository;

    public List<Coche> getAllCoches() {
        return cocheRepository.findAll();
    }

    public Optional<Coche> getCocheById(Long id) {
        return cocheRepository.findById(id);
    }

    public Coche createCoche(Coche coche) {
        return cocheRepository.save(coche);
    }

    public Optional<Coche> updateCoche(Long id, Coche cocheDetails) {
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
            return cocheRepository.save(coche);
        });
    }

    public boolean deleteCoche(Long id) {
        return cocheRepository.findById(id).map(coche -> {
            cocheRepository.delete(coche);
            return true;
        }).orElse(false);
    }

    public List<Coche> getCochesByMarca(String marca) {
        return cocheRepository.findByMarca(marca);
    }
}