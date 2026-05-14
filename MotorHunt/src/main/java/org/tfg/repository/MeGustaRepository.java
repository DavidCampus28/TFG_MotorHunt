package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.MeGusta;
import org.tfg.model.entities.Usuario;
import org.tfg.model.entities.Coche;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeGustaRepository extends JpaRepository<MeGusta, Long> {
    List<MeGusta> findByUsuario(Usuario usuario);
    List<MeGusta> findByCoche(Coche coche);
    Optional<MeGusta> findByUsuarioAndCoche(Usuario usuario, Coche coche);
    long countByUsuario(Usuario usuario);
    long countByCoche(Coche coche);
    boolean existsByUsuarioAndCoche(Usuario usuario, Coche coche);
}

