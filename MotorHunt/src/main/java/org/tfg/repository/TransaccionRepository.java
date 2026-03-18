package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.entities.Transaccion;
import org.tfg.model.enums.TipoTransaccion;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByVendedorId(Long vendedorId);
    List<Transaccion> findByCompradorId(Long compradorId);
    List<Transaccion> findByCocheId(Long cocheId);
    List<Transaccion> findByTipo(TipoTransaccion tipo);
    List<Transaccion> findByCompletada(Boolean completada);
    List<Transaccion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
