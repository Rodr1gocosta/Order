package br.com.rodrigoproject.service.dto;

import br.com.rodrigoproject.domain.enumeration.Prioridade;
import br.com.rodrigoproject.domain.enumeration.Status;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.rodrigoproject.domain.OrderServico} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderServicoDTO implements Serializable {

    private Long id;

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    private Prioridade prioridade;

    private String observacoes;

    private Status status;

    @NotNull
    private TecnicoDTO tecnico;

    @NotNull
    private ClienteDTO cliente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TecnicoDTO getTecnico() {
        return tecnico;
    }

    public void setTecnico(TecnicoDTO tecnico) {
        this.tecnico = tecnico;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderServicoDTO)) {
            return false;
        }

        OrderServicoDTO orderServicoDTO = (OrderServicoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderServicoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderServicoDTO{" +
            "id=" + getId() +
            ", dataAbertura='" + getDataAbertura() + "'" +
            ", dataFechamento='" + getDataFechamento() + "'" +
            ", prioridade='" + getPrioridade() + "'" +
            ", observacoes='" + getObservacoes() + "'" +
            ", status='" + getStatus() + "'" +
            ", tecnico=" + getTecnico() +
            ", cliente=" + getCliente() +
            "}";
    }
}
