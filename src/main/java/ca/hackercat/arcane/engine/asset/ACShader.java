package ca.hackercat.arcane.engine.asset;

public class ACShader implements ACAsset {

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

    @Override
    public boolean isDisposable() {
        return disposable;
    }

    @Override
    public void dispose() {

    }
}
