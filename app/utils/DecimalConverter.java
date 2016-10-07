package utils;

import java.math.BigDecimal;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Standardised way to deal with money
 */
public class DecimalConverter {
    public static BigDecimal getBigDecimal(double val) {
        BigDecimal bigDecimal = new BigDecimal(val);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getBigDecimal(BigDecimal val) {
        if (val == null)
            return null;
        return val.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
