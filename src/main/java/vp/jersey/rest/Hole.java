package vp.jersey.rest;

/**
 * Class for holes.
 * @author Ville Piirainen
 */
public class Hole {
    /** Hole par. */
    private int par;
    /** Hole length. */
    private int lengthm;

    public Hole() {

    }

    public Hole(int par, int lengthm) {
        this.par = par;
        this.lengthm = lengthm;
    }

    public int getPar() {
        return par;
    }

    public void setPar(int par) {
        this.par = par;
    }

    public int getLengthm() {
        return lengthm;
    }

    public void setLengthm(int lengthm) {
        this.lengthm = lengthm;
    }
}
