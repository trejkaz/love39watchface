package org.trypticon.dozenalwatchface;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * Formatter for dozenal dates.
 */
class DozenalDateFormat {
    private final DozenalNumberFormat dayOfMonthFormat = new DozenalNumberFormat(2);
    private DateFormatSymbols symbols;

    DozenalDateFormat() {
        updateLocale(Locale.getDefault());
    }

    String formatDate(DozenalTime time) {
        StringBuilder builder = new StringBuilder(16);

        //TODO: Can we use DateFormat directly somehow? I think we'd have to make a custom Calendar?

        int dayOfWeek = time.getDayOfWeek();
        if (dayOfWeek > 0) {
            dayOfWeek++; // skip Monday
        }
        builder.append(symbols.getShortWeekdays()[dayOfWeek]);
        builder.append(' ');

        builder.append(dayOfMonthFormat.format(time.getDayOfMonth()));
        builder.append(' ');

        builder.append(symbols.getShortMonths()[time.getMonth()]);

        return builder.toString();
    }

    void updateLocale(Locale locale) {
        symbols = DateFormatSymbols.getInstance(locale);
    }
}