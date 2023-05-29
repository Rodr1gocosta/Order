package br.com.rodrigoproject.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServicoMapperTest {

    private OrderServicoMapper orderServicoMapper;

    @BeforeEach
    public void setUp() {
        orderServicoMapper = new OrderServicoMapperImpl();
    }
}
