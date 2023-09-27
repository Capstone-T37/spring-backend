package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MeetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Meet.class);
        Meet meet1 = Meet.builder().build();
        meet1.setId(1L);
        Meet meet2 = Meet.builder().build();
        meet2.setId(meet1.getId());
        assertThat(meet1).isEqualTo(meet2);
        meet2.setId(2L);
        assertThat(meet1).isNotEqualTo(meet2);
        meet1.setId(null);
        assertThat(meet1).isNotEqualTo(meet2);
    }
}
