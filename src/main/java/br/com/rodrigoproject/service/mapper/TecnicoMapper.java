package br.com.rodrigoproject.service.mapper;

import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tecnico} and its DTO {@link TecnicoDTO}.
 */
@Mapper(componentModel = "spring")
public interface TecnicoMapper extends EntityMapper<TecnicoDTO, Tecnico> {}
