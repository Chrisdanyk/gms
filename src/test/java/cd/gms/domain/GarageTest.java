package cd.gms.domain;

import static org.assertj.core.api.Assertions.assertThat;

import cd.gms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GarageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Garage.class);
        Garage garage1 = new Garage();
        garage1.setId(1L);
        Garage garage2 = new Garage();
        garage2.setId(garage1.getId());
        assertThat(garage1).isEqualTo(garage2);
        garage2.setId(2L);
        assertThat(garage1).isNotEqualTo(garage2);
        garage1.setId(null);
        assertThat(garage1).isNotEqualTo(garage2);
    }
}
