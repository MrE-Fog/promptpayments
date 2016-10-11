package utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * For that 100% test coverage!!1
 */
public class DecimalConverterTest {
    @Test
    public void constructor() throws Exception {
        new DecimalConverter();
    }

    @Test
    public void nullConversions() throws Exception {
        assertNull(DecimalConverter.getBigDecimal((Double) null));
        assertNull(DecimalConverter.getBigDecimal((BigDecimal) null));
    }
}