package com.lihengl.jujutsu.utilities;

public class StringUtility {
    public static String capicalize(String original) {
        String result;

        if (original.length() == 0) {
            result = original;
        } else if (original.length() == 1) {
            result = original.toUpperCase();
        } else {
            result = original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
        }

        return result;
    }
}
