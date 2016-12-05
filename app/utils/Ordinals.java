package utils;

public class Ordinals {
    private static String[] wordOrdinals = {
            "zeroth",
            "first",
            "second",
            "third",
            "fourth",
            "fifth",
            "sixth",
            "seventh",
            "eighth",
            "ninth",
            "tenth",
            "eleventh",
            "twelfth",
            "thirteenth",
    };

    public static String forNumber(int i) throws IllegalArgumentException {
        if (i < 0) throw new IllegalArgumentException("number must be non-negative");
        if (i < wordOrdinals.length && i >= 0) return wordOrdinals[i];



        return i % 10 == 1 ? i + "st"
             : i % 10 == 2 ? i + "nd"
             : i % 10 == 3 ? i + "rd"
             : i + "th";
    }

    private int number;

    public Ordinals(int number) {
        if (number < 0) throw new IllegalArgumentException("number must be non-negative");
        this.number = number;
    }

    public String get() {return forNumber(number);}
}
