package models;

/**
 * Created by daniel.rothig on 18/10/2016.
 *
 * Signifies whether a field is valid or not
 */
public class FieldValidation {
    private final boolean isValid;
    private final String errorMessage;

    public String errorMessage() {
        return isValid ? "" : errorMessage;
    }

    public String cssClass() {
        //slightly naughty convenience method
        return isValid ? "" : "error";
    }

    public boolean isOk() {
        return isValid;
    }

    public static FieldValidation ok() {
        return new FieldValidation(true, null);
    }

    public static FieldValidation fail(String e) {
        return new FieldValidation(false, e);
    }

    private FieldValidation(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }
}
