package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.CocheFoto;

import java.util.List;
import java.util.Optional;

@Repository
public interface CocheFotoRepository extends JpaRepository<CocheFoto, Long> {
    List<CocheFoto> findByCocheIdOrderByIdAsc(Long cocheId);
    Optional<CocheFoto> findFirstByCocheIdOrderByIdAsc(Long cocheId);
    long countByCocheId(Long cocheId);
}
