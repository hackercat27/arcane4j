package ca.hackercat.arcane.engine.io;

import ca.hackercat.arcane.engine.ACThreadManager;
import ca.hackercat.arcane.engine.asset.ACDisposable;
import ca.hackercat.arcane.logging.ACLogger;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ACFileUtils {

    public enum Directive {
        FILE("file:", ""),
        RESOURCE("res:", "assets");

        public final String value;
        public final String expansion;

        Directive(String value, String expansion) {
            this.value = value;
            this.expansion = expansion;
        }

        public static Directive getFromValue(String value) {
            for (Directive d : Directive.values()) {
                if (value.equals(d.value)) {
                    return d;
                }
            }
            return null;
        }
    }

    public enum FileType {
        TEXTURE("texture"),
        SHADER("shader"),
        SOUND("sound"),
        INDEX("index"),
        DICT("dict"), // properties file
        DICT_JSON("jdict"), // json file
        DICT_PLIST("pdict"); // plist file (xml)

        public final String value;

        FileType(String value) {
            this.value = value;
        }

        public static FileType getFromValue(String value) {
            for (FileType ft : FileType.values()) {
                if (value.equals(ft.value)) {
                    return ft;
                }
            }
            return null;
        }
    }

    public static String ASSET_INDEX_PATH = "res:/index.json";

    private static ACAssetIndex assetIndex;

    static {
        assetIndex = fromJson(readStringFromPath(ASSET_INDEX_PATH), ACAssetIndex.class);
    }

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

        // else, path is perfect and doesn't need changing
        // return as is
        return path;
    }

    public static String readStringFromStream(InputStream in) {
        if (in == null) {
            return "";
        }
        try {
            return new String(in.readAllBytes());
        }
        catch (IOException e) {
            return "";
        }
    }

    public static String readStringFromPath(String path) {
        return readStringFromStream(getInputStream(path));
    }

    public static InputStream getInputStream(String path) {
        /*
         * try to find classpath file first,
         * and if it doesn't exist, then try to find a file on disk.
         * jar resources can mask files since they take priority, but whatever.
         */

        String simplePath = simplifyPath(path);

        InputStream in = ACFileUtils.class.getResourceAsStream("/"+simplePath);
        if (in == null) {
            try {
                in = new FileInputStream(simplePath);
            }
            catch (FileNotFoundException ignored) {}
        }

        // if the inputstream is STILL null, then that means we didn't find anything.
        if (in == null) {
            ACLogger.warn("Couldn't find file '" + simplePath + "'");
        }
        return in;
    }

    public static ACDisposable getAsset(String name) {
        // CAN NOT call on main thread because it'll lock up if true
        ACThreadManager.throwIfMainThread();
        return assetIndex.getAsset(name);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

}
