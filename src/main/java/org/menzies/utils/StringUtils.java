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

    public static String shorten(String string, int length) {

        if (string.length() > length) {
            int segmentLength = length / 2;

            return string.substring(0, segmentLength -1) + "..."
                    + string.substring(string.length() - segmentLength - 1);

        }
        else return string;
    }
}
