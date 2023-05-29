package br.com.rodrigoproject.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rodrigoproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderServicoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderServicoDTO.class);
        OrderServicoDTO orderServicoDTO1 = new OrderServicoDTO();
        orderServicoDTO1.setId(1L);
        OrderServicoDTO orderServicoDTO2 = new OrderServicoDTO();
        assertThat(orderServicoDTO1).isNotEqualTo(orderServicoDTO2);
        orderServicoDTO2.setId(orderServicoDTO1.getId());
        assertThat(orderServicoDTO1).isEqualTo(orderServicoDTO2);
        orderServicoDTO2.setId(2L);
        assertThat(orderServicoDTO1).isNotEqualTo(orderServicoDTO2);
        orderServicoDTO1.setId(null);
        assertThat(orderServicoDTO1).isNotEqualTo(orderServicoDTO2);
    }
}
