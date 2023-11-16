package dev.knowhowto.jh.petclinic.vuebdd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import dev.knowhowto.jh.petclinic.vuebdd.web.rest.TestUtil;

class VetsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vets.class);
        Vets vets1 = new Vets();
        vets1.setId(1L);
        Vets vets2 = new Vets();
        vets2.setId(vets1.getId());
        assertThat(vets1).isEqualTo(vets2);
        vets2.setId(2L);
        assertThat(vets1).isNotEqualTo(vets2);
        vets1.setId(null);
        assertThat(vets1).isNotEqualTo(vets2);
    }
}
