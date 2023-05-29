package br.com.rodrigoproject.repository;

import br.com.rodrigoproject.domain.OrderServico;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderServico entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderServicoRepository extends JpaRepository<OrderServico, Long> {}
