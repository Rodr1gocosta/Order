package br.com.rodrigoproject.service.mapper;

import br.com.rodrigoproject.domain.Cliente;
import br.com.rodrigoproject.service.dto.ClienteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * Mapper for the entity {@link Cliente} and its DTO {@link ClienteDTO}.
 */
@Mapper(componentModel = "spring")
@Component
public interface ClienteMapper extends EntityMapper<ClienteDTO, Cliente> {
    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "telefone", source = "telefone")
    @Mapping(target = "sexo", source = "sexo")
    @Mapping(target = "dataCriacao", source = "dataCriacao")
    @Mapping(target = "ativo", source = "ativo")
    Cliente toEntity(ClienteDTO clienteDTO);
}
