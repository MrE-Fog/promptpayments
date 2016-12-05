package utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 02/12/2016.
 */
public class OrdinalsTest {
    @Test
    public void forNumber() throws Exception {
        assertEquals("zeroth", Ordinals.forNumber(0));
        assertEquals("first", Ordinals.forNumber(1));
        assertEquals("second", Ordinals.forNumber(2));
        assertEquals("third", Ordinals.forNumber(3));
        assertEquals("thirteenth", Ordinals.forNumber(13));
        assertEquals("14th", Ordinals.forNumber(14));
        assertEquals("21st", Ordinals.forNumber(21));
        assertEquals("22nd", Ordinals.forNumber(22));
        assertEquals("23rd", Ordinals.forNumber(23));
        assertEquals("24th", Ordinals.forNumber(24));
    }

    @Test
    public void forNumberThrows() throws Exception {
        try {
            Ordinals.forNumber(-1);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("should thrrow for negative numbers");
    }

    @Test
    public void get() throws Exception {
        for (int i : Arrays.asList(0,1,2,3,13,14,21,22,23,24)) {
            assertEquals(new Ordinals(i).get(), Ordinals.forNumber(i));
        }
    }

    @Test
    public void constructorThrows() throws Exception {
        try {
            new Ordinals(-1);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("should thrrow for negative numbers");
    }

}