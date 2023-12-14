package dev.knowhowto.jh.petclinic.vuebdd.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwnersMapperTest {

    private OwnersMapper ownersMapper;

    @BeforeEach
    public void setUp() {
        ownersMapper = new OwnersMapperImpl();
    }
}
