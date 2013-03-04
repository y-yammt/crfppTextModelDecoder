package lib;

public interface Tagger {
    
    /** Following methods are implemented in AbstractTagger **/
    public InputSequence createInputSequence();
    public boolean parse(InputSequence inputSequence);
    public Lattice getLattice();
}
