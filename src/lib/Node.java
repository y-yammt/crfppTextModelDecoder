package lib;

import java.util.ArrayList;
import java.util.List;

public class Node {
    
    public int xIndex = 0;
    
    public int yIndex = 0;
    
    public double alpha = 0.0;
    
    public double beta = 0.0;
    
    public double cost = 0.0;
    
    public double bestCost = 0.0;
    
    public Node previousNode = null;
    
    public List<Path> leftPathList = new ArrayList<Path>();
    
    public List<Path> rightPathList = new ArrayList<Path>();
    
    public Node(int xIndex, int yIndex) {
        
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        alpha = 0.0;
        beta = 0.0;
        cost = 0.0;
        bestCost = 0.0;
        previousNode = null;
        leftPathList.clear();
        rightPathList.clear();
    }
    
    public void calcAlpha() {
        
        alpha = 0.0;
        for (int i = 0; i < leftPathList.size(); ++i) {
            Path path = leftPathList.get(i);
            alpha = Util.calcLogSumExp(alpha, path.cost + path.leftNode.alpha, (i == 0));
        }
        alpha += cost;
    }
    
    public void calcBeta() {
        
        beta = 0.0;
        for (int i = 0; i < rightPathList.size(); ++i) {
            Path path = rightPathList.get(i);
            beta = Util.calcLogSumExp(beta, path.cost + path.rightNode.beta, (i == 0));
        }
        beta += cost;
    }
}
