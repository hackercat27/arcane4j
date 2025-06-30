package ca.hackercat.arcane.core.asset;

import ca.hackercat.arcane.core.ACThreadManager;

import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import java.util.ArrayList;
import java.util.List;

public class ACAssetManager {

    // TODO: something something use PhantomReference<> for proper implementation???
    private static final List<ACAsset> assets = new ArrayList<>();

    public static void register(ACAsset asset) {
        if (asset == null) {
            return;
        }
        synchronized (assets) {
            assets.add(asset);
        }
    }

    public static void registerAssets() {
        ACThreadManager.throwIfNotMainThread();
        synchronized (assets) {
            for (ACAsset asset : assets) {
                if (!asset.registered()) {
                    asset.register();
                }
            }
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
                    ACLogger.log(ACLevel.VERBOSE, "Cleaned asset %s", asset.getClass().getSimpleName());
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
                ACLogger.log(ACLevel.VERBOSE, "Forcibly cleaned asset %s", asset.getClass().getSimpleName());
            }
            assets.clear();
        }
    }

}
