package br.com.rodrigoproject.repository;

import br.com.rodrigoproject.domain.Cliente;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cliente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpf(String cpf);
}
