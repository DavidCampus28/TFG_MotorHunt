package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.Mensaje;
import org.tfg.model.entities.Usuario;
import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByRemitenteIdAndDestinatarioId(Long remitenteId, Long destinatarioId);
    List<Mensaje> findByDestinatarioId(Long destinatarioId);
    List<Mensaje> findByDestinatarioIdAndLeido(Long destinatarioId, Boolean leido);
    List<Mensaje> findByRemitenteId(Long remitenteId);

    @Query("SELECT m FROM Mensaje m WHERE (m.remitente = :usuario OR m.destinatario = :usuario) ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findByRemitenteOrDestinatario(@Param("usuario") Usuario usuario);
}
