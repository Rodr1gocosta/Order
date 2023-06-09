package br.com.rodrigoproject.service.mapper;

import br.com.rodrigoproject.domain.Cliente;
import br.com.rodrigoproject.domain.OrderServico;
import br.com.rodrigoproject.domain.Tecnico;
import br.com.rodrigoproject.service.dto.ClienteDTO;
import br.com.rodrigoproject.service.dto.OrderServicoDTO;
import br.com.rodrigoproject.service.dto.TecnicoDTO;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for the entity {@link OrderServico} and its DTO {@link OrderServicoDTO}.
 */
@Mapper(componentModel = "spring")
@Component
public interface OrderServicoMapper extends EntityMapper<OrderServicoDTO, OrderServico> {
    @Mapping(target = "tecnico", source = "tecnico", qualifiedByName = "tecnicoId")
    @Mapping(target = "cliente", source = "cliente", qualifiedByName = "clienteId")
    OrderServicoDTO toDto(OrderServico s);

    @Named("tecnicoId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "telefone", source = "telefone")
    @Mapping(target = "sexo", source = "sexo")
    @Mapping(target = "dataCriacao", source = "dataCriacao")
    @Mapping(target = "ativo", source = "ativo")
    TecnicoDTO toDtoTecnicoId(Tecnico tecnico);

    @Named("clienteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cpf", source = "cpf")
    @Mapping(target = "telefone", source = "telefone")
    @Mapping(target = "sexo", source = "sexo")
    @Mapping(target = "dataCriacao", source = "dataCriacao")
    @Mapping(target = "ativo", source = "ativo")
    ClienteDTO toDtoClienteId(Cliente cliente);
}
