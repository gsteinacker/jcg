/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Guido Steinacker
 * @version %version: 28 %
 */
public final class DateUtil {
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String DATETIME_PATTERN = "dd.MM.yyyy hh:mm";

    public static String date() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(new Date());
    }

    public static String dateTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_PATTERN);
        return sdf.format(new Date());
    }

}
