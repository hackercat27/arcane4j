package ca.hackercat.arcane.core.asset;

import ca.hackercat.arcane.core.io.ACFileUtils;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ACShader implements ACAsset {

    private static class Uniform {
        public String name;
        public String typeName;
        public Object value;
        public Uniform(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public String name;
    public String vertexPath, fragmentPath;
    public int vertexID, fragmentID, programID;
    private boolean disposable;
    private boolean registered;

    private int error = 0;
    private int ERROR_LINK_BIT = 1;
    private int ERROR_INVALID_PROGRAM_BIT = 2;

    public ACShader(String name, String vertexPath, String fragmentPath) {
        this.name = name == null? toString() : name;
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;

        ACAssetManager.register(this);
    }

    public int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    public void setUniform(String name, boolean value) {
        glUniform1i(getUniformLocation(name),
                value? 1 : 0);
    }

    public void setUniform(String name, int value) {
        glUniform1i(getUniformLocation(name),
                value);
    }

    public void setUniform(String name, double value) {
        glUniform1f(getUniformLocation(name),
                (float) value);
    }

    public void setUniform(String name, Vector2d value) {
        glUniform2f(getUniformLocation(name),
                (float) value.x(), (float) value.y());
    }

    public void setUniform(String name, Vector3d value) {
        glUniform3f(getUniformLocation(name),
                (float) value.x(), (float) value.y(), (float) value.z());
    }

    public void setUniform(String name, Vector4d value) {
        glUniform4f(getUniformLocation(name),
                (float) value.x(), (float) value.y(), (float) value.z(), (float) value.w());
    }

    public void setUniform(String name, Matrix4d value) {
        FloatBuffer buf = MemoryUtil.memAllocFloat(16);
        new Matrix4f(value).get(buf);
        glUniformMatrix4fv(getUniformLocation(name), false, buf);
        MemoryUtil.memFree(buf);
    }

    @Override
    public boolean registered() {
        return registered;
    }

    @Override
    public void register() {
        String vertexSource = ACFileUtils.readStringFromPath(this.vertexPath);
        String fragmentSource = ACFileUtils.readStringFromPath(this.fragmentPath);

        this.programID = glCreateProgram();
        this.vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(this.vertexID, vertexSource);
        glCompileShader(this.vertexID);

        if (glGetShaderi(this.vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            ACLogger.error(this.vertexPath + " couldn't compile\n"
                                   + glGetShaderInfoLog(this.vertexID));
        }

        this.fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(this.fragmentID, fragmentSource);
        glCompileShader(this.fragmentID);
        if (glGetShaderi(this.fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            ACLogger.error(this.fragmentPath + " couldn't compile\n"
                                   + glGetShaderInfoLog(this.fragmentID));
        }

        glAttachShader(this.programID, this.vertexID);
        glAttachShader(this.programID, this.fragmentID);

        glLinkProgram(this.programID);
        if (glGetProgrami(this.programID, GL_LINK_STATUS) == GL_FALSE) {
            ACLogger.error("Shader " + this.name + " initialization error - Couldn't link program\n"
                                   + glGetProgramInfoLog(this.programID));
            error |= ERROR_LINK_BIT;
        }
        glValidateProgram(this.programID);
        if (glGetProgrami(this.programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            ACLogger.error("Shader " + this.name + " initialization error Program is invalid\n"
                                   + glGetProgramInfoLog(this.programID));
            error |= ERROR_INVALID_PROGRAM_BIT;
        }

        registered = true;
    }

    @Override
    public boolean isDisposable() {
        return disposable;
    }

    @Override
    public void dispose() {
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
        glDeleteProgram(programID);
        registered = false;
    }

    public static String getGLSLTypeName(Object instance) {
        if (instance == null) {
            return null;
        }
        Class<?> clazz = instance.getClass();

        if (clazz == Integer.class || clazz == Boolean.class) {
            return "int";
        }
        if (clazz == Double.class || clazz == Float.class) {
            return "float";
        }
        if (clazz == Vector2d.class || clazz == Vector2f.class) {
            return "vec2";
        }
        if (clazz == Vector3d.class || clazz == Vector3f.class) {
            return "vec3";
        }
        if (clazz == Vector4d.class || clazz == Vector4f.class) {
            return "vec4";
        }
        if (clazz == Matrix4d.class || clazz == Matrix4f.class) {
            return "mat4";
        }

        return null;
    }
}
