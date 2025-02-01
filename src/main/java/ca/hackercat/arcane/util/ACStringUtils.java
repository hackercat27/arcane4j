package ca.hackercat.arcane.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ACStringUtils {

    public static String getProperty(String key) {
        if (key.startsWith("system.")) {
            return System.getProperty(key.replaceFirst("system\\.", ""));
        }
        return "0";
    }

    public static String resolve(String str) {

        if (str == null) {
            return "";
        }

        Pattern pattern = Pattern.compile("\\$\\{(\\S+)}");
        Matcher matcher = pattern.matcher(str);

        return matcher.replaceAll(match -> {
            String varName = match.group(1);

            return getProperty(varName);
        });

    }

}
