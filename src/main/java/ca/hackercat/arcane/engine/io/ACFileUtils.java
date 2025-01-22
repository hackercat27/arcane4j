package ca.hackercat.arcane.engine.io;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class ACFileUtils {

    public enum Directive {
        FILE("file:", ""),
        RESOURCE("res:", "assets"),
        SHADERS("shader:", "res:/shaders"),
        TEXTURE("tex:", "res:/textures"),
        SOUND("sound:", "res:/sounds"),
        LEVEL("lvl:", "res:/levels");

        public final String value;
        public final String expansion;

        Directive(String value, String expansion) {
            this.value = value;
            this.expansion = expansion;
        }
    }

    public enum FileType {
        TEXTURE("texture"),
        SOUND("sound"),
        DICT("dict"),
        DICT_JSON("jdict"),
        DICT_PLIST("pdict");

        public final String value;

        FileType(String value) {
            this.value = value;
        }
    }

    public static String ASSET_INDEX_PATH = "res:/index.json";

    public static String simplifyPath(String path) {

        boolean containsBadSeparators = path.matches(".*\\\\\\\\.*") // contains escaped backslash
                                     || path.matches(".*//.*") // contains double slashes
                                     || path.matches(".*\\\\/"); // contains escaped slash
        boolean containsBackreferences = path.matches(".*\\.\\..*");
        boolean containsDirectives = false;

        for (Directive d : Directive.values()) {
            if (path.contains(d.value)) {
                containsDirectives = true;
                break;
            }
        }

        if (containsBadSeparators) {
            String newPath = path.replaceAll("\\\\", "/")
                                 .replaceAll("//", "/");
            return simplifyPath(newPath);
        }
        else if (containsDirectives) {
            String newPath = path;
            for (Directive d : Directive.values()) {
                newPath = newPath.replaceAll(d.value, d.expansion);
            }
            return simplifyPath(newPath);
        }
        else if (containsBackreferences) {
            String[] dirs = path.split("/");
            for (int i = 1; i < dirs.length; i++) {
                if (dirs[i].matches("\\.\\.")) {
                    dirs[i] = null;
                    dirs[i - 1] = null;
                }
            }
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 0; i < dirs.length; i++) {
                if (dirs[i] == null) {
                    continue;
                }
                pathBuilder.append(dirs[i]);
                if (i < dirs.length - 1) {
                    pathBuilder.append("/");
                }
            }
            return simplifyPath(pathBuilder.toString());
        }

        // else, path is perfect and doesn't need changing - return as is
        return path;
    }

    public static String readAll(InputStream in) {
        try {
            return new String(in.readAllBytes());
        }
        catch (IOException e) {
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

}
