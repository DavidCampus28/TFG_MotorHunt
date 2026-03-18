package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.Mensaje;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByRemitenteIdAndDestinatarioId(Long remitenteId, Long destinatarioId);
    List<Mensaje> findByDestinatarioId(Long destinatarioId);
    List<Mensaje> findByDestinatarioIdAndLeido(Long destinatarioId, Boolean leido);
    List<Mensaje> findByRemitenteId(Long remitenteId);
}
