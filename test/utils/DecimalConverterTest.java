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
    public void conversions() throws Exception {
        assertEquals(new BigDecimal("123"), DecimalConverter.getBigDecimal("123.456"));
        assertEquals(new BigDecimal("123"), DecimalConverter.getBigDecimal(new BigDecimal("123.456")));
        assertEquals(new BigDecimal("123"), DecimalConverter.getBigDecimal(123.456));

        assertEquals(new BigDecimal("-123"), DecimalConverter.getBigDecimal("-123.456"));
        assertEquals(new BigDecimal("-123"), DecimalConverter.getBigDecimal(new BigDecimal("-123.456")));
        assertEquals(new BigDecimal("-123"), DecimalConverter.getBigDecimal(-123.456));
    }

    @Test
    public void badData() throws Exception {
        assertNull(DecimalConverter.getBigDecimal("blue"));
        assertNull(DecimalConverter.getBigDecimal(""));
    }

    @Test
    public void nullConversions() throws Exception {
        assertNull(DecimalConverter.getBigDecimal((String) null));
        assertNull(DecimalConverter.getBigDecimal((Double) null));
        assertNull(DecimalConverter.getBigDecimal((BigDecimal) null));
    }
}