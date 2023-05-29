package br.com.rodrigoproject.repository;

import br.com.rodrigoproject.domain.Tecnico;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tecnico entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {}
