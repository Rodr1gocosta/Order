package br.com.rodrigoproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rodrigoproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderServicoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderServico.class);
        OrderServico orderServico1 = new OrderServico();
        orderServico1.setId(1L);
        OrderServico orderServico2 = new OrderServico();
        orderServico2.setId(orderServico1.getId());
        assertThat(orderServico1).isEqualTo(orderServico2);
        orderServico2.setId(2L);
        assertThat(orderServico1).isNotEqualTo(orderServico2);
        orderServico1.setId(null);
        assertThat(orderServico1).isNotEqualTo(orderServico2);
    }
}
