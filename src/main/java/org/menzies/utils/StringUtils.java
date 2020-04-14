package org.menzies.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final Pattern withoutDotsAtEnd;

    static {
        withoutDotsAtEnd = Pattern.compile("^.+[^\\.]");
    }


    public static String makeFileSafe(String string) {

        if (string.length() == 0) {
            return string;
        }

        string = string.trim();

        string = string.replaceAll("[\\\\/:*?\"<>|]", " ");

        Matcher matcher = withoutDotsAtEnd.matcher(string);
        matcher.find();


        return matcher.group();
    }
}
