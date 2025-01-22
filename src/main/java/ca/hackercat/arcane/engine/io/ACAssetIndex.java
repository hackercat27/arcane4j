package ca.hackercat.arcane.engine.io;

import ca.hackercat.arcane.engine.asset.ACAsset;
import ca.hackercat.arcane.engine.asset.ACShaderFactory;
import ca.hackercat.arcane.engine.io.ACFileUtils.FileType;

import java.io.FileNotFoundException;

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

    public ACAsset getAsset(String name) {
        for (Element element : elements) {
            if (element.name.equals(name)) {

                // load asset
                ACAsset asset = null;
                FileType type = FileType.getFromValue(element.type);

                if (type == null) {
                    continue;
                }

                switch (type) {
                    case SHADER -> {
                        return ACShaderFactory.get(element.name, element.vertex, element.fragment);
                    }
                }

            }
        }

        throw new RuntimeException(new FileNotFoundException("Couldn't find asset '" + name + "'!"));
    }

}
