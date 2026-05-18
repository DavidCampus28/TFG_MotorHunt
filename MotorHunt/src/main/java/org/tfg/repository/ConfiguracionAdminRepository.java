package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.entities.ConfiguracionAdmin;
import java.util.Optional;

public interface ConfiguracionAdminRepository extends JpaRepository<ConfiguracionAdmin, Long> {
    Optional<ConfiguracionAdmin> findFirstByOrderByIdDesc();
}

