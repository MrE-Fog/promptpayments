package utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Utility that uses reflection to test auto-generated getters, setters and public fields of Plain Old Container Objects
 */
public class ReflectiveObjectTester {

    /**
     * Scans the class for gettable fields and makes a field-by-field equality check
     * @param one the model object
     * @param two the compared object
     * @throws Exception when an assertion fails
     */
    public static <T> void assertFieldEquivalence(T one, T two) throws Exception {
        for (Field field : one.getClass().getDeclaredFields()) {
            Getter getter = getGetter(field);
            if (getter == null) continue;
            assertEquals(String.format("Values of field %s should be identical", field.getName()),
                    getter.get(one), getter.get(two));
        }
    }

    /**
     * One by one, sets properties in ONE to the corresponding values in TWO and tests
     * @param one the object to be manipulated
     * @param two the object containing the new values, all of which have to be different to the initial values in @one
     * @param assumedTestedFields Number of fields which shoudld be covered in this test
     * @throws Exception when an assertion fails
     */
    public static <T> void assertSetAndGetAllFields(T one, T two, int assumedTestedFields) throws Exception {
        int testedFields = 0;
        for (Field field: one.getClass().getDeclaredFields()) {
            Getter getter = getGetter(field);
            if (getter == null) continue;

            assertNotEquals(String.format("initial values of field %s should not be identical", field.getName()),
                    getter.get(one), getter.get(two));

            Setter setter = getSetter(field);
            if (setter == null) continue;

            setter.set(one, getter.get(two));
            assertEquals(String.format("Final values of field %s should be identical", field.getName()),
                    getter.get(one), getter.get(two));
            testedFields += 1;
        }

        assertEquals("assumedTestedFields", assumedTestedFields, testedFields);
    }

    private static Setter getSetter(Field field) {
        if (canSet(field.getModifiers())) return field::set;

        try {
            Method setter = field.getDeclaringClass().getMethod("set"+field.getName(), field.getType());
            return canSet(setter.getModifiers()) ? setter::invoke : null;
        } catch (NoSuchMethodException ignored) { }

        return null;
    }

    private static boolean canGet(int modifiers) {
        return (modifiers & Modifier.PUBLIC) == Modifier.PUBLIC
                && (modifiers & Modifier.STATIC) != Modifier.STATIC
                && (modifiers & Modifier.ABSTRACT) != Modifier.ABSTRACT;
    }

    private static boolean canSet(int modifiers) {
        return canGet(modifiers)
                && (modifiers & Modifier.FINAL) != Modifier.FINAL;
    }

    private static Getter getGetter(Field field) throws NoSuchMethodException {
        if (canGet(field.getModifiers())) return field::get;

        try {
            Method getter = field.getDeclaringClass().getMethod("get"+field.getName());
            return canGet(getter.getModifiers()) ? getter::invoke : null;
        } catch (NoSuchMethodException ignored) { }
        try {
            Method getter = field.getDeclaringClass().getMethod("is"+field.getName());
            return canGet(getter.getModifiers()) ? getter::invoke : null;
        } catch (NoSuchMethodException ignored) { }

        return null;
    }

    interface Getter {
        Object get(Object obj) throws Exception;
    }

    interface Setter {
        void set(Object obj, Object val) throws Exception;
    }
}
