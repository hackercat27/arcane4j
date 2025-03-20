package ca.hackercat.arcane.core.io;

import ca.hackercat.arcane.core.asset.ACAsset;
import ca.hackercat.arcane.core.asset.ACShader;
import ca.hackercat.arcane.core.io.ACFileUtils.FileType;
import ca.hackercat.arcane.logging.ACLogger;

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

    private Map<String, ACAsset> cache = new HashMap<>();

    public ACAsset getAsset(String name) {

        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        ACLogger.log("Loading asset '%s' from disk", name);

        for (Element element : elements) {
            if (element.name.equals(name)) {

                ACAsset asset = null;
                FileType type = FileType.getFromValue(element.type);

                if (type == null) {
                    continue;
                }

                switch (type) {
                    case SHADER ->
                            asset = new ACShader(element.name,
                                                 element.vertex,
                                                 element.fragment);
                }

                if (asset == null) {
                    continue;
                }

                cache.put(name, asset);
                return asset;
            }
        }

        return null;
    }

}
