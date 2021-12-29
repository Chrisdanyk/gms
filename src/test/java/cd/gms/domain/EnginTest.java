package cd.gms.domain;

import static org.assertj.core.api.Assertions.assertThat;

import cd.gms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnginTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Engin.class);
        Engin engin1 = new Engin();
        engin1.setId(1L);
        Engin engin2 = new Engin();
        engin2.setId(engin1.getId());
        assertThat(engin1).isEqualTo(engin2);
        engin2.setId(2L);
        assertThat(engin1).isNotEqualTo(engin2);
        engin1.setId(null);
        assertThat(engin1).isNotEqualTo(engin2);
    }
}
