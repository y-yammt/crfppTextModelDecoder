package lib;

import java.util.List;

public interface CrfModel {
    
    /** Following methods are implemented in AbstractCrfModel **/
    public int getSize();
    public int getXSize();
    public int getYSize();
    public String getY(int i);
    public List<String> getYList();
    public double getCostFactor();
    public boolean setLatticeCost(Lattice lattice);
    
    public int getID(String feature);
}
