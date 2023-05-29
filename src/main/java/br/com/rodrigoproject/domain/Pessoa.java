package br.com.rodrigoproject.domain;

import br.com.rodrigoproject.domain.enumeration.Sexo;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * A Pessoa.
 */

@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Pattern(regexp = "^\\d{3}\\x2E\\d{3}\\x2E\\d{3}\\x2D\\d{2}$")
    @Column(name = "cpf")
    private String cpf;

    @Column(name = "telefone")
    private String telefone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false)
    private Sexo sexo;

    @NotNull
    @Column(name = "data_criacao", nullable = false)
    private ZonedDateTime dataCriacao;

    @NotNull
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pessoa id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Pessoa nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return this.cpf;
    }

    public Pessoa cpf(String cpf) {
        this.setCpf(cpf);
        return this;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public Pessoa telefone(String telefone) {
        this.setTelefone(telefone);
        return this;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Sexo getSexo() {
        return this.sexo;
    }

    public Pessoa sexo(Sexo sexo) {
        this.setSexo(sexo);
        return this;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public ZonedDateTime getDataCriacao() {
        return this.dataCriacao;
    }

    public Pessoa dataCriacao(ZonedDateTime dataCriacao) {
        this.setDataCriacao(dataCriacao);
        return this;
    }

    public void setDataCriacao(ZonedDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public Pessoa ativo(Boolean ativo) {
        this.setAtivo(ativo);
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pessoa)) {
            return false;
        }
        return id != null && id.equals(((Pessoa) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pessoa{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", cpf='" + getCpf() + "'" +
            ", telefone='" + getTelefone() + "'" +
            ", sexo='" + getSexo() + "'" +
            ", dataCriacao='" + getDataCriacao() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
