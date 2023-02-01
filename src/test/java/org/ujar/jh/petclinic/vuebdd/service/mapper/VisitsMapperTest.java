package org.ujar.jh.petclinic.vuebdd.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitsMapperTest {

    private VisitsMapper visitsMapper;

    @BeforeEach
    public void setUp() {
        visitsMapper = new VisitsMapperImpl();
    }
}
