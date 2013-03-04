package lib;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractTagger implements Tagger {
    
    private CrfModel crfModel = null;
    
    protected Lattice lattice = null;
    
    protected void applyViterbi() {
        
        List<List<Node>> node2dList = lattice.node2dList;
        int sequenceSize = lattice.inputSequence.getSequenceSize();
        if (sequenceSize == 0) {
            return;
        }
        int ySize = lattice.yList.size();
        for (int i = 0; i < sequenceSize; ++i) {
            List<Node> yNodeList = node2dList.get(i);
            for (int j = 0; j < ySize; ++j) {
                Node node = yNodeList.get(j);
                double bestCost = -1.0e37;
                Node bestNode = null;
                List<Path> leftPathList = node.leftPathList;
                for (int k = 0; k < leftPathList.size(); ++k) {
                    Path leftPath = leftPathList.get(k);
                    double cost = leftPath.leftNode.bestCost + leftPath.cost + node.cost;
                    if (k == 0 || cost > bestCost) {
                        bestCost = cost;
                        bestNode = leftPath.leftNode;
                    }
                }
                node.previousNode = bestNode;
                node.bestCost = (bestNode != null) ? bestCost : node.cost;
            }
        }
        
        double bestCost = -1.0e37;
        Node bestNode = null;
        int tailIndex = sequenceSize - 1;
        for (int j = 0; j < ySize; ++j) {
            Node node = node2dList.get(tailIndex).get(j);
            if (j == 0 || bestCost < node.bestCost) {
                bestNode = node;
                bestCost = node.bestCost;
            }
        }
        
        List<String> yList = lattice.yList;
        String[] bestYSequenceArray = new String[sequenceSize];
        for (Node traverseNode = bestNode; traverseNode != null; traverseNode = traverseNode.previousNode) {
            bestYSequenceArray[traverseNode.xIndex] = yList.get(traverseNode.yIndex);
        }
        lattice.bestYSequence = Arrays.asList(bestYSequenceArray);
    }
    
    protected boolean setLattice(InputSequence inputSequence) {
        
        lattice = new Lattice(inputSequence, crfModel.getYList());
        if (!crfModel.setLatticeCost(lattice)) {
            return false;
        }
        return true;
    }
    
    public AbstractTagger(CrfModel crfModel) {
        this.crfModel = crfModel;
    }
    
    public InputSequence createInputSequence() {
        return new InputSequence(crfModel.getXSize());
    }
    
    public boolean parse(InputSequence inputSequence) {
        
        if (crfModel == null) {
            return false;
        }
        
        if (!setLattice(inputSequence)) {
            return false;
        }
        applyViterbi();
        
        return true;
    }
    
    public Lattice getLattice() {
        return lattice;
    }
}
