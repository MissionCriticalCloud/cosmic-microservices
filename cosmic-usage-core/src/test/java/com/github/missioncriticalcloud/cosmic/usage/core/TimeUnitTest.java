package com.github.missioncriticalcloud.cosmic.usage.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import org.junit.Test;

public class TimeUnitTest {

    @Test
    public void testConvert() {
        final BigDecimal tenDays = BigDecimal.valueOf(864000L);

        assertThat(TimeUnit.SECONDS.convert(tenDays))
                .isEqualByComparingTo(BigDecimal.valueOf(864000L));

        assertThat(TimeUnit.MINUTES.convert(tenDays))
                .isEqualByComparingTo(BigDecimal.valueOf(14400L));

        assertThat(TimeUnit.HOURS.convert(tenDays))
                .isEqualByComparingTo(BigDecimal.valueOf(240L));

        assertThat(TimeUnit.DAYS.convert(tenDays))
                .isEqualByComparingTo(BigDecimal.valueOf(10L));
    }
}
