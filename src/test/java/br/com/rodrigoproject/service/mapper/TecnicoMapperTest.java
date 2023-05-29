package br.com.rodrigoproject.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TecnicoMapperTest {

    private TecnicoMapper tecnicoMapper;

    @BeforeEach
    public void setUp() {
        tecnicoMapper = new TecnicoMapperImpl();
    }
}
