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

    public static String resolve(String str, String customVars) {

        if (str == null) {
            return "";
        }

        String[] vars;
        if (customVars.isBlank()) {
            vars = new String[0];
        }
        else {
            vars = customVars.split(",");
        }

        Pattern pattern = Pattern.compile("\\$\\{(\\S+)}");
        Matcher matcher = pattern.matcher(str);

        return matcher.replaceAll(match -> {
            String varName = match.group(1);

            for (String var : vars) {
                String[] args = var.split("=");
                if (args.length != 2) {
                    continue;
                }
                String name = args[0];
                String value = args[1];
                if (varName.equals(name)) {
                    return value;
                }
            }

            return getProperty(varName);
        });

    }

    public static String resolve(String str) {
        return resolve(str, "");
    }

}
