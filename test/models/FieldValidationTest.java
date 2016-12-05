package models;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 05/12/2016.
 */
public class FieldValidationTest {
    @Test
    public void ok() throws Exception {
        FieldValidation ok = FieldValidation.ok();

        assertTrue(ok.isOk());
        assertEquals("", ok.errorMessage());
        assertEquals("", ok.cssClass());

    }

    @Test
    public void fail() throws Exception {
        FieldValidation fail = FieldValidation.fail("boo!");

        assertFalse(fail.isOk());
        assertEquals("boo!", fail.errorMessage());
        assertEquals("error", fail.cssClass());
    }

}