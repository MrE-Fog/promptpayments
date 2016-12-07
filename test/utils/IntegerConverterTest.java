package utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerConverterTest {
    @Test
    public void tryConvertInt() throws Exception {
        assertInt("1", 1);
        assertInt(" 199", 199);
        assertInt("-1% ", -1);
        assertInt("10.0", 10);
        assertInt("42.000000000000000000000000000000000000000000000000000000000000", 42);
    }

    @Test
    public void tryConvertInt_fail() throws Exception {
        assertNoInt("1.1");
        assertNoInt("s1");
        assertNoInt("10.0001");
        assertNoInt("1x7");
        assertNoInt("blueberries");
        assertNoInt("");
        assertNoInt(null);
    }

    @Test
    public void construct() {
        new IntegerConverter();
    }

    private void assertInt(String s, Integer i) {
        assertEquals(i, IntegerConverter.tryConvertInt(s));
    }

    private void assertNoInt(String s) {
        assertNull(IntegerConverter.tryConvertInt(s));
    }

}