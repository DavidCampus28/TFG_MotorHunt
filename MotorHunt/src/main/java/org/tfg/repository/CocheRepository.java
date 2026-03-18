package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.Coche;

import java.util.List;

@Repository
public interface CocheRepository extends JpaRepository<Coche, Long> {
    List<Coche> findByMarca(String marca);
    List<Coche> findByUsuarioId(Long usuarioId);
}