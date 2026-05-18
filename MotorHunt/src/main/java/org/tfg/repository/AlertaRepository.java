package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.entities.Alerta;
import org.tfg.model.entities.Usuario;
import org.tfg.model.entities.Coche;
import org.tfg.model.enums.TipoAlerta;
import java.util.List;
import java.time.LocalDateTime;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByResueltaFalse();
    List<Alerta> findByTipo(TipoAlerta tipo);
    List<Alerta> findByUsuario(Usuario usuario);
    List<Alerta> findByCoche(Coche coche);
    List<Alerta> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Alerta> findByNivelRiesgoGreaterThanEqual(int nivelMinimo);
}

