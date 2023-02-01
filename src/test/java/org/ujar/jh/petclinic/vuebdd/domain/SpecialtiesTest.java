package org.ujar.jh.petclinic.vuebdd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.ujar.jh.petclinic.vuebdd.web.rest.TestUtil;

class SpecialtiesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Specialties.class);
        Specialties specialties1 = new Specialties();
        specialties1.setId(1L);
        Specialties specialties2 = new Specialties();
        specialties2.setId(specialties1.getId());
        assertThat(specialties1).isEqualTo(specialties2);
        specialties2.setId(2L);
        assertThat(specialties1).isNotEqualTo(specialties2);
        specialties1.setId(null);
        assertThat(specialties1).isNotEqualTo(specialties2);
    }
}
