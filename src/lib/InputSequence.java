package lib;

import java.util.ArrayList;
import java.util.List;

public final class InputSequence {
    
    private int xSize = 0;
    
    private List<String[]> x = new ArrayList<String[]>();
    
    public InputSequence(int xSize) {
        
        this.xSize = xSize;
        x.clear();
    }
    
    public boolean add(String line) {
        return add(Util.splitWithTabOrSpace(line, -1));
    }
    
    public boolean add(String[] line) {
        
        if (line != null && line.length >= xSize) {
            x.add(line);
            return true;
        }
        return false;
    }
    
    public void clear() {
        x.clear();
    }
    
    public int getSequenceSize() {
        return x.size();
    }
    
    public List<String[]> getXList() {
        return x;
    }
    
    public int getXSize() {
        return xSize;
    }
}
