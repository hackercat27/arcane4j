package ca.hackercat.arcane.engine.asset;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ACShader implements ACDisposable {

    private static class Uniform {
        public String name;
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
    public boolean registered;

    public ACShader(String name, String vertexPath, String fragmentPath) {
        this.name = name == null? toString() : name;
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
    }

    public int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    public void setUniform(String name, boolean value) {
        glUniform1i(getUniformLocation(name), value? 1 : 0);
    }

    public void setUniform(String name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform(String name, double value) {
        glUniform1f(getUniformLocation(name), (float) value);
    }

    public void setUniform(String name, Vector2d value) {
        glUniform2f(getUniformLocation(name), (float) value.x(), (float) value.y());
    }

    public void setUniform(String name, Vector3d value) {
        glUniform3f(getUniformLocation(name), (float) value.x(), (float) value.y(), (float) value.z());
    }

    public void setUniform(String name, Vector4d value) {
        glUniform4f(getUniformLocation(name), (float) value.x(), (float) value.y(), (float) value.z(), (float) value.w());
    }

    public void setUniform(String name, Matrix4d value) {
        FloatBuffer buf = MemoryUtil.memAllocFloat(16);
        new Matrix4f(value).get(buf);
        glUniformMatrix4fv(getUniformLocation(name), false, buf);
        MemoryUtil.memFree(buf);
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
}
