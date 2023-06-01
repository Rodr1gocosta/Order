package br.com.rodrigoproject.service.mapper;

import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * Mapper for the entity {@link Tecnico} and its DTO {@link TecnicoDTO}.
 */
@Mapper(componentModel = "spring")
@Component
public interface TecnicoMapper extends EntityMapper<TecnicoDTO, Tecnico> {
    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "telefone", source = "telefone")
    @Mapping(target = "sexo", source = "sexo")
    @Mapping(target = "dataCriacao", source = "dataCriacao")
    @Mapping(target = "ativo", source = "ativo")
    Tecnico toEntity(TecnicoDTO tecnicoDTO);
}
