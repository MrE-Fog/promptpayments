package models;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Represents a full payment report.
 */
public class ReportModel {
    public ReportSummary Info;

    public BigDecimal NumberOne;
    public BigDecimal NumberTwo;
    public BigDecimal NumberThree;

    public ReportModel(ReportSummary info) {
        Info = info;
    }

    public ReportModel(ReportSummary info, BigDecimal numberOne, BigDecimal numberTwo, BigDecimal numberThree) {
        Info = info;
        NumberOne = numberOne;
        NumberTwo = numberTwo;
        NumberThree = numberThree;
    }
}

