package br.com.rodrigoproject.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.rodrigoproject.domain.Cliente} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClienteDTO implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClienteDTO)) {
            return false;
        }

        ClienteDTO clienteDTO = (ClienteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clienteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClienteDTO{" +
            "id=" + getId() +
            "}";
    }
}
