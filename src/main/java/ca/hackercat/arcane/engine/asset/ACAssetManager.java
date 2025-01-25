package ca.hackercat.arcane.engine.asset;

import ca.hackercat.arcane.engine.ACThreadManager;

import java.util.ArrayList;
import java.util.List;

public class ACAssetManager {

    private static final List<ACAsset> assets = new ArrayList<>();

    public static void register(ACAsset asset) {
        if (asset == null) {
            return;
        }
        synchronized (assets) {
            assets.add(asset);
        }
    }

    public static void clean() {
        ACThreadManager.throwIfNotMainThread();
        List<ACAsset> garbage = new ArrayList<>();
        synchronized (assets) {
            for (ACAsset asset : assets) {
                if (asset.isDisposable()) {
                    garbage.add(asset);
                    asset.dispose();
                }
            }
            assets.removeAll(garbage);
        }
    }

    public static void forceDisposeAll() {
        ACThreadManager.throwIfNotMainThread();
        synchronized (assets) {
            for (ACAsset asset : assets) {
                asset.dispose();
            }
            assets.clear();
        }
    }

}
