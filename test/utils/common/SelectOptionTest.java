package utils.common;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.bcel.generic.Select;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Tests for SelectOption
 */
public class SelectOptionTest {
    private SelectOption no = new SelectOption("0", "No");
    private SelectOption yes = new SelectOption("1", "Yes");

    @Test
    public void fromEmpty() throws Exception {
        LinkedHashMap<SelectOption, Boolean> map = SelectOption.fromEmpty(Arrays.asList(no, yes));

        assertEquals(false, map.get(no));
        assertEquals(false, map.get(yes));
    }

    @Test
    public void fromEmpty_throwsIfMultiple() throws Exception {
        try {
            SelectOption.fromEmpty(Arrays.asList(no,no));
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void fromSelected() throws Exception {
        LinkedHashMap<SelectOption, Boolean> map = SelectOption.fromSelected(
                Arrays.asList(no, yes),
                Collections.singletonList("1"));

        assertEquals(false, map.get(no));
        assertEquals(true, map.get(yes));
    }

    @Test
    public void fromSelected_throwsIfMultiple() throws Exception {
        try {
            SelectOption.fromSelected(Arrays.asList(no,no), Collections.singletonList("1"));
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void test_hashCode() throws Exception {
        assertEquals(yes.hashCode(), new SelectOption("1", "Ja").hashCode());
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public void equals() throws Exception {
        assertEquals(yes, "1");
        assertEquals(no, "0");
    }

    @Test
    public void test_toString() throws Exception {
        assertEquals("SelectOption[1]", yes.toString());
        assertEquals("SelectOption[0]", no.toString());
    }

}