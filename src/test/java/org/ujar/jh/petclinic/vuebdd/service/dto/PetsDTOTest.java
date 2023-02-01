package org.ujar.jh.petclinic.vuebdd.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.ujar.jh.petclinic.vuebdd.web.rest.TestUtil;

class PetsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PetsDTO.class);
        PetsDTO petsDTO1 = new PetsDTO();
        petsDTO1.setId(1L);
        PetsDTO petsDTO2 = new PetsDTO();
        assertThat(petsDTO1).isNotEqualTo(petsDTO2);
        petsDTO2.setId(petsDTO1.getId());
        assertThat(petsDTO1).isEqualTo(petsDTO2);
        petsDTO2.setId(2L);
        assertThat(petsDTO1).isNotEqualTo(petsDTO2);
        petsDTO1.setId(null);
        assertThat(petsDTO1).isNotEqualTo(petsDTO2);
    }
}
