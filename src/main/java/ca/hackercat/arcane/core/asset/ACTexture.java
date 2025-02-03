package ca.hackercat.arcane.core.asset;

public class ACTexture implements ACAsset {


    private int id;

    private int width;
    private int height;

    /**
     * Creates a blank texture with the specified width and height
     * @param width width of the texture
     * @param height height of the texture
     */
    public ACTexture(int width, int height) {
        this.width = width;
        this.height = height;

        ACAssetManager.register(this);
    }


    @Override
    public boolean registered() {
        return false;
    }

    @Override
    public void register() {

    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {

    }
}
