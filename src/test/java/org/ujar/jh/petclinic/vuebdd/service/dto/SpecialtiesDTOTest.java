package org.ujar.jh.petclinic.vuebdd.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.ujar.jh.petclinic.vuebdd.web.rest.TestUtil;

class SpecialtiesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpecialtiesDTO.class);
        SpecialtiesDTO specialtiesDTO1 = new SpecialtiesDTO();
        specialtiesDTO1.setId(1L);
        SpecialtiesDTO specialtiesDTO2 = new SpecialtiesDTO();
        assertThat(specialtiesDTO1).isNotEqualTo(specialtiesDTO2);
        specialtiesDTO2.setId(specialtiesDTO1.getId());
        assertThat(specialtiesDTO1).isEqualTo(specialtiesDTO2);
        specialtiesDTO2.setId(2L);
        assertThat(specialtiesDTO1).isNotEqualTo(specialtiesDTO2);
        specialtiesDTO1.setId(null);
        assertThat(specialtiesDTO1).isNotEqualTo(specialtiesDTO2);
    }
}
