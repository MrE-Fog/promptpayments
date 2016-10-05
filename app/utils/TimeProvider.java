package utils;

import com.google.inject.ImplementedBy;

import java.util.Calendar;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Provides time info - abstraction for testing
 */
@ImplementedBy(GregorianTimeProvider.class)
public interface TimeProvider {
    Calendar Now();
}
