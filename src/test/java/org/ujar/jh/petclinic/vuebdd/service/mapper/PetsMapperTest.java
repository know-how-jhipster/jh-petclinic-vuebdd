package org.ujar.jh.petclinic.vuebdd.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PetsMapperTest {

    private PetsMapper petsMapper;

    @BeforeEach
    public void setUp() {
        petsMapper = new PetsMapperImpl();
    }
}
