package br.com.rodrigoproject.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rodrigoproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TecnicoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TecnicoDTO.class);
        TecnicoDTO tecnicoDTO1 = new TecnicoDTO();
        tecnicoDTO1.setId(1L);
        TecnicoDTO tecnicoDTO2 = new TecnicoDTO();
        assertThat(tecnicoDTO1).isNotEqualTo(tecnicoDTO2);
        tecnicoDTO2.setId(tecnicoDTO1.getId());
        assertThat(tecnicoDTO1).isEqualTo(tecnicoDTO2);
        tecnicoDTO2.setId(2L);
        assertThat(tecnicoDTO1).isNotEqualTo(tecnicoDTO2);
        tecnicoDTO1.setId(null);
        assertThat(tecnicoDTO1).isNotEqualTo(tecnicoDTO2);
    }
}
