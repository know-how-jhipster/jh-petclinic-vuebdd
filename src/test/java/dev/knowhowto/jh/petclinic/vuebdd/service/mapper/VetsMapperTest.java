package dev.knowhowto.jh.petclinic.vuebdd.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VetsMapperTest {

    private VetsMapper vetsMapper;

    @BeforeEach
    public void setUp() {
        vetsMapper = new VetsMapperImpl();
    }
}
