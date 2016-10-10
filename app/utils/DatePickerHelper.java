package utils;

import com.google.inject.Inject;
import utils.common.SelectOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DatePickerHelper {
    public List<SelectOption> years;
    public List<SelectOption> months;
    public List<SelectOption> days;

    public DatePickerHelper(TimeProvider timeProvider) {
        years = getYears(timeProvider);
        months = getMonths();
        days = getDays();
    }


    private static List<SelectOption> getDays() {
        List<SelectOption> rtn = new ArrayList<>();
        for (Integer i = 1; i < 32 ;i++) {
            rtn.add(new SelectOption(i.toString(), i.toString()));
        }
        return rtn;
    }

    private static List<SelectOption> getYears(TimeProvider timeProvider) {
        int thisYear = timeProvider.Now().get(Calendar.YEAR);

        List<SelectOption> rtn = new ArrayList<>();
        for (Integer i = thisYear; i > thisYear - 4; i--) {
            rtn.add(new SelectOption(i.toString(), i.toString()));
        }
        return rtn;
    }

    private static List<SelectOption> getMonths() {
        return Arrays.asList(
            new SelectOption("0", "January"),
            new SelectOption("1", "February"),
            new SelectOption("2", "March"),
            new SelectOption("3", "April"),
            new SelectOption("4", "May"),
            new SelectOption("5", "June"),
            new SelectOption("6", "July"),
            new SelectOption("7", "August"),
            new SelectOption("8", "September"),
            new SelectOption("9", "October"),
            new SelectOption("10", "November"),
            new SelectOption("11", "December"));
    }
}
