package br.com.rodrigoproject.service.dto;

import br.com.rodrigoproject.domain.enumeration.Sexo;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.rodrigoproject.domain.Tecnico} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TecnicoDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Campo NOME obrigatório!")
    @NotNull(message = "NOME não deve ser nulo!")
    private String nome;

    @CPF
    private String cpf;

    private String telefone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private LocalDateTime dataCriacao;

    @NotNull
    private Boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TecnicoDTO)) {
            return false;
        }

        TecnicoDTO tecnicoDTO = (TecnicoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tecnicoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore

    @Override
    public String toString() {
        return "TecnicoDTO{" +
            "id=" + id +
            ", nome='" + nome + '\'' +
            ", cpf='" + cpf + '\'' +
            ", telefone='" + telefone + '\'' +
            ", sexo=" + sexo +
            ", dataCriacao=" + dataCriacao +
            ", ativo=" + ativo +
            '}';
    }
}
