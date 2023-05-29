package br.com.rodrigoproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rodrigoproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TecnicoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tecnico.class);
        Tecnico tecnico1 = new Tecnico();
        tecnico1.setId(1L);
        Tecnico tecnico2 = new Tecnico();
        tecnico2.setId(tecnico1.getId());
        assertThat(tecnico1).isEqualTo(tecnico2);
        tecnico2.setId(2L);
        assertThat(tecnico1).isNotEqualTo(tecnico2);
        tecnico1.setId(null);
        assertThat(tecnico1).isNotEqualTo(tecnico2);
    }
}
