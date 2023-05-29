package br.com.rodrigoproject.service.mapper;

import br.com.rodrigoproject.domain.Cliente;
import br.com.rodrigoproject.service.dto.ClienteDTO;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for the entity {@link Cliente} and its DTO {@link ClienteDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClienteMapper extends EntityMapper<ClienteDTO, Cliente> {}
