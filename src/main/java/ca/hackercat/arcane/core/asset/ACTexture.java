package ca.hackercat.arcane.core.asset;

import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class ACTexture implements ACAsset {


    private InputStream textureStream;

    private int id = -1;
    private int width = -1;
    private int height = -1;

    /**
     * Creates a blank texture with the specified width and height
     *
     * @param width  width of the texture
     * @param height height of the texture
     */
    public ACTexture(int width, int height) {
        this.width = width;
        this.height = height;

        ACAssetManager.register(this);
    }

    public ACTexture(InputStream textureStream) {

        this.textureStream = textureStream;

        ACAssetManager.register(this);
    }

    @Override
    public boolean registered() {
        return id >= 0;
    }

    @Override
    public void register() {

        if (textureStream == null) {
            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            setFilter(GL_LINEAR);
            return;
        }

        ByteBuffer buffer = null;
        try {
            byte[] data = textureStream.readAllBytes();
            buffer = BufferUtils.createByteBuffer(data.length);
            buffer.put(data).flip();
        }
        catch (IOException | NullPointerException e) {
            ACLogger.log(ACLevel.WARN, "Could not load texture: %s", ACLogger.exceptionString(e));
        }

        if (buffer == null) {
            return;
        }

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        // wrap image in both x and y coords (s and t)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = stbi_load_from_memory(buffer, width, height, channels, STBI_rgb_alpha);

        if (image != null) {
            // something something mipmap levels go here
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0,
                         GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float maxAniso = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso);

                ACLogger.log(ACLevel.INFO, "maxAniso = %.2f", maxAniso);
            }

            stbi_image_free(image);


            this.width = width.get(0);
            this.height = height.get(0);

            ACLogger.log(ACLevel.INFO, "Registered texture with id %d of size %dx%d", getID(), getWidth(), getHeight());
        }
    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {
        if (!registered()) {
            return;
        }
        glDeleteTextures(getID());
        id = -1;
        width = -1;
        height = -1;
    }

    public void setFilter(int filter) {
        if (!registered()) {
            return;
        }
        setUpscaleFilter(filter);
        setDownscaleFilter(filter);
    }

    public void setUpscaleFilter(int filter) {
        glBindTexture(GL_TEXTURE_2D, getID());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setDownscaleFilter(int filter) {
        glBindTexture(GL_TEXTURE_2D, getID());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getID() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
