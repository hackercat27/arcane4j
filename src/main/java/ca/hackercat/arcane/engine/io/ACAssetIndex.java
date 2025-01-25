package ca.hackercat.arcane.engine.io;

import ca.hackercat.arcane.engine.asset.ACDisposable;
import ca.hackercat.arcane.engine.asset.ACShaderFactory;
import ca.hackercat.arcane.engine.io.ACFileUtils.FileType;
import ca.hackercat.arcane.logging.ACLogger;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ACAssetIndex {

    public static class Element {
        public String path;
        public String name;
        public String type;

        // texture specific
        public int[] position;
        public int[] size;

        // shader specific
        public String vertex;
        public String fragment;
    }

    public Element[] elements;

    private Map<String, ACDisposable> cache = new HashMap<>();

    public ACDisposable getAsset(String name) {

        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        ACLogger.log("Loading asset '%s' from disk", name);

        for (Element element : elements) {
            if (element.name.equals(name)) {

                ACDisposable asset = null;
                FileType type = FileType.getFromValue(element.type);

                if (type == null) {
                    continue;
                }

                switch (type) {
                    case SHADER ->
                            asset = ACShaderFactory.get(element.name,
                                    element.vertex, element.fragment);
                }

                if (asset == null) {
                    continue;
                }

                cache.put(name, asset);
                return asset;
            }
        }

        throw new RuntimeException(new FileNotFoundException("Couldn't find asset '" + name + "'!"));
    }

}
