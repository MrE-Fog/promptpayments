package utils;

public class Ordinals {
    private String[] wordOrdinals = {
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

    public String forNumber(int i) {
        if (i < wordOrdinals.length) return wordOrdinals[i];

        return i % 10 == 1 ? i + "st"
             : i % 10 == 2 ? i + "nd"
             : i % 10 == 3 ? i + "rd"
             : i + "th";
    }

    private int number;

    public Ordinals(int number) {
        this.number = number;
    }

    public String get() {return forNumber(number);}
}
