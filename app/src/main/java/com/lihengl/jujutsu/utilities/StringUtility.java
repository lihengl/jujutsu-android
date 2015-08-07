package com.lihengl.jujutsu.utilities;

import java.util.Locale;

public class StringUtility {
    public static String capitalize(String s) {
        Locale locale = Locale.ENGLISH;
        String result;

        if (s.length() == 0) {
            result = s;
        } else if (s.length() == 1) {
            result = s.toUpperCase(locale);
        } else {
            result = s.substring(0, 1).toUpperCase(locale) + s.substring(1).toLowerCase(locale);
        }

        return result;
    }
}
