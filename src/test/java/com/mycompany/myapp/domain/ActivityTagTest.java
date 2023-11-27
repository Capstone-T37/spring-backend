package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActivityTagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActivityTag.class);
        ActivityTag activityTag1 = new ActivityTag();
        activityTag1.setId(1L);
        ActivityTag activityTag2 = new ActivityTag();
        activityTag2.setId(activityTag1.getId());
        assertThat(activityTag1).isEqualTo(activityTag2);
        activityTag2.setId(2L);
        assertThat(activityTag1).isNotEqualTo(activityTag2);
        activityTag1.setId(null);
        assertThat(activityTag1).isNotEqualTo(activityTag2);
    }
}
