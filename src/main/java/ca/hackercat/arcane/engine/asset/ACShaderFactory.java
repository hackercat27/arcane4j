package ca.hackercat.arcane.engine.asset;

import ca.hackercat.arcane.engine.ACThreadManager;
import ca.hackercat.arcane.engine.io.ACFileUtils;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ACShaderFactory {

    private static final List<ACShader> shaders = new LinkedList<>();

    public static ACShader get(String name, String vertexPath, String fragmentPath) {
        ACThreadManager.throwIfMainThread();

        ACShader shader = new ACShader(name, vertexPath, fragmentPath);
        shaders.add(shader);

        synchronized (shader) {
            while (!shader.registered) {
                try {
                    shader.wait();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return shader;
    }

    public static void createShaders() {
        ACThreadManager.throwIfNotMainThread();
        synchronized (shaders) {
            List<ACShader> handledShaders = new LinkedList<>();

            for (ACShader shader : shaders) {

                createShader(shader);

                ACLogger.log("Instantiated shader %s with program id %d", shader
                        .name, shader.programID);
                shader.registered = true;
                handledShaders.add(shader);
                synchronized (shader) {
                    shader.notify();
                }
            }

            shaders.removeAll(handledShaders);
        }
    }

    private static void createShader(ACShader shader) {
        String vertexSource = ACFileUtils.readStringFromPath(shader.vertexPath);
        String fragmentSource = ACFileUtils.readStringFromPath(shader.fragmentPath);

        shader.programID = glCreateProgram();
        shader.vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(shader.vertexID, vertexSource);
        glCompileShader(shader.vertexID);

        if (glGetShaderi(shader.vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            ACLogger.error(shader.vertexPath + " couldn't compile\n"
                    + glGetShaderInfoLog(shader.vertexID));
        }

        shader.fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(shader.fragmentID, fragmentSource);
        glCompileShader(shader.fragmentID);
        if (glGetShaderi(shader.fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            ACLogger.error(shader.fragmentPath + " couldn't compile\n"
                    + glGetShaderInfoLog(shader.fragmentID));
        }

        glAttachShader(shader.programID, shader.vertexID);
        glAttachShader(shader.programID, shader.fragmentID);

        glLinkProgram(shader.programID);
        if (glGetProgrami(shader.programID, GL_LINK_STATUS) == GL_FALSE) {
            ACLogger.error("Shader " + shader.name + " initialization error - Couldn't link program\n"
                    + glGetProgramInfoLog(shader.programID));
        }
        glValidateProgram(shader.programID);
        if (glGetProgrami(shader.programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            ACLogger.error("Shader " + shader.name + " initialization error Program is invalid\n"
                    + glGetProgramInfoLog(shader.programID));
        }
    }

}
