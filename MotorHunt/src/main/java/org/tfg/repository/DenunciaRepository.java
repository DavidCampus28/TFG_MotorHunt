package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.entities.Denuncia;
import org.tfg.model.entities.Usuario;
import org.tfg.model.entities.Coche;
import org.tfg.model.enums.EstadoDenuncia;
import java.util.List;
import java.time.LocalDateTime;

public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
    List<Denuncia> findByEstado(EstadoDenuncia estado);
    List<Denuncia> findByDenunciante(Usuario denunciante);
    List<Denuncia> findByUsuarioDenunciado(Usuario usuario);
    List<Denuncia> findByCocheDenunciado(Coche coche);
    List<Denuncia> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}

