package ca.hackercat.arcane.core.asset;

// dumb af string wrapper class. probably a sign of bad design
public class ACTextFile implements ACDisposable {

    public String value;

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {

    }

    @Override
    public String toString() {
        return value;
    }
}
