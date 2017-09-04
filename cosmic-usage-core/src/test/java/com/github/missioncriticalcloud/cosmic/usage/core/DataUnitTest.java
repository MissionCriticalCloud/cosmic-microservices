package com.github.missioncriticalcloud.cosmic.usage.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import org.junit.Test;

public class DataUnitTest {

    @Test
    public void testConvert() {
        final BigDecimal tenGiga = BigDecimal.valueOf(10737418240L);

        assertThat(DataUnit.BYTES.convert(tenGiga))
                .isEqualByComparingTo(BigDecimal.valueOf(10737418240L));

        assertThat(DataUnit.KB.convert(tenGiga))
                .isEqualByComparingTo(BigDecimal.valueOf(10485760L));

        assertThat(DataUnit.MB.convert(tenGiga))
                .isEqualByComparingTo(BigDecimal.valueOf(10240L));

        assertThat(DataUnit.GB.convert(tenGiga))
                .isEqualByComparingTo(BigDecimal.valueOf(10L));
    }
}
