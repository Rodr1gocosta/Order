package br.com.rodrigoproject.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link br.com.rodrigoproject.domain.Tecnico} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TecnicoDTO implements Serializable {

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
            "id=" + getId() +
            "}";
    }
}
