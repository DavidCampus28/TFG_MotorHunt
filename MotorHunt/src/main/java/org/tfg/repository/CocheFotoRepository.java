package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.Coche;
import org.tfg.model.entities.CocheFoto;

import java.util.List;
import java.util.Optional;

@Repository
public interface CocheFotoRepository extends JpaRepository<CocheFoto, Long> {
    List<CocheFoto> findByCocheOrderByOrdenAsc(Coche coche);
    Optional<CocheFoto> findByCocheAndPortadaTrue(Coche coche);
    Optional<CocheFoto> findFirstByCocheOrderByOrdenAsc(Coche coche);
}

