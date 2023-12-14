package dev.knowhowto.jh.petclinic.vuebdd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import dev.knowhowto.jh.petclinic.vuebdd.web.rest.TestUtil;

class OwnersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Owners.class);
        Owners owners1 = new Owners();
        owners1.setId(1L);
        Owners owners2 = new Owners();
        owners2.setId(owners1.getId());
        assertThat(owners1).isEqualTo(owners2);
        owners2.setId(2L);
        assertThat(owners1).isNotEqualTo(owners2);
        owners1.setId(null);
        assertThat(owners1).isNotEqualTo(owners2);
    }
}
