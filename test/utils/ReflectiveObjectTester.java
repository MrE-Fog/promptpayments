package utils;

import models.ReportFilingModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Utility that uses reflection to test auto-generated getters, setters and public fields of Plain Old Container Objects
 */
public class ReflectiveObjectTester {

    /*

    Types of POCO:

    I)  Mutable
        - should have getters and setters for all fields
        - should have equality operators
    II) Immutable - should have a constructor for all fields; are either public
     */


    public static void assertGoodImmutablePoco(Class clazz) throws Exception {
        assertEquals("Immutable POCOs should have only one constructor", 1, clazz.getDeclaredConstructors().length);

        assertEquals("Constructor should take enough arguments to populate all fields",
                clazz.getDeclaredFields().length, clazz.getDeclaredConstructors()[0].getParameters().length);

        assertTrue("Immutable POCOs should have at least one field", clazz.getDeclaredFields().length > 0);

        for (Field field : clazz.getDeclaredFields()) {
            assertNotNull(String.format("Field %s should be gettable", field.getName()),
                    getGetter(field));
            assertNull(String.format("Field %should not be settable", field.getName()),
                    getSetter(field));
        }
    }

    public static void assertGoodMutablePoco(Class clazz) throws Exception {
        assertEquals("Immutable POCOs should have only one constructor", 1, clazz.getDeclaredConstructors().length);

        assertTrue("Constructor should either be inaccessible or deprecated",
                   clazz.getDeclaredConstructors()[0].getAnnotation(Deprecated.class) != null
                || (clazz.getDeclaredConstructors()[0].getModifiers() & Modifier.PRIVATE) == Modifier.PRIVATE
                || (clazz.getDeclaredConstructors()[0].getModifiers() & Modifier.PROTECTED) == Modifier.PROTECTED);

        assertEquals("Constructor should take no arguments (static maker methods should be used instead)",
                0, clazz.getDeclaredConstructors()[0].getParameters().length);

        assertTrue("Mutable POCOs should have at least one field", clazz.getDeclaredFields().length > 0);

        for (Field field : clazz.getDeclaredFields()) {
            assertNotNull(String.format("Field %s should be gettable", field.getName()),
                    getGetter(field));
            assertNotNull(String.format("Field %should not be settable", field.getName()),
                    getSetter(field));
        }
    }

    public static long countGettables(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(x -> getGetter(x) != null).count();
    }
    public static long countSettables(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(x -> getSetter(x) != null).count();
    }

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

    private static Getter getGetter(Field field) {
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
