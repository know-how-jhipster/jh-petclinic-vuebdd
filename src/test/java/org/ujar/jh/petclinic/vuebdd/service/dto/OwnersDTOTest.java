package org.ujar.jh.petclinic.vuebdd.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.ujar.jh.petclinic.vuebdd.web.rest.TestUtil;

class OwnersDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OwnersDTO.class);
        OwnersDTO ownersDTO1 = new OwnersDTO();
        ownersDTO1.setId(1L);
        OwnersDTO ownersDTO2 = new OwnersDTO();
        assertThat(ownersDTO1).isNotEqualTo(ownersDTO2);
        ownersDTO2.setId(ownersDTO1.getId());
        assertThat(ownersDTO1).isEqualTo(ownersDTO2);
        ownersDTO2.setId(2L);
        assertThat(ownersDTO1).isNotEqualTo(ownersDTO2);
        ownersDTO1.setId(null);
        assertThat(ownersDTO1).isNotEqualTo(ownersDTO2);
    }
}
