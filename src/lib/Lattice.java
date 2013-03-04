package lib;

import java.util.ArrayList;
import java.util.List;

public class Lattice {
    
    public InputSequence inputSequence = null;
    public List<String> yList = new ArrayList<String>();
    public List<List<Node>> node2dList = new ArrayList<List<Node>>();
    public List<String> bestYSequence = new ArrayList<String>();
    
    public Lattice(InputSequence inputSequence, List<String> yList) {
        
        this.inputSequence = inputSequence;
        this.yList = yList;
        
        int sequenceSize = inputSequence.getSequenceSize();
        int ySize = yList.size();
        
        // nodes
        for (int i = 0; i < sequenceSize; ++i) {
            List<Node> yNodeList = new ArrayList<Node>();
            for (int j = 0; j < ySize; ++j) {
                Node node = new Node(i, j);
                yNodeList.add(node);
            }
            node2dList.add(yNodeList);
        }
        
        // paths
        for (int i = 0; i < sequenceSize; ++i) {
            List<Node> yNodeList = node2dList.get(i);
            for (int j = 0; j < ySize; ++j) {
                Node node = yNodeList.get(j);
                // leftPathList
                if (i > 0) {
                    for (int k = 0; k < ySize; ++k) {
                        node.leftPathList.add(new Path(node2dList.get(i - 1).get(k), node));
                    }
                }
                
                // rightPathList
                // Note: seqenceSize > 0
                if (i != sequenceSize - 1) {
                    for (int k = 0; k < ySize; ++k) {
                        node.rightPathList.add(new Path(node2dList.get(i + 1).get(k), node));
                    }
                }
            }
        }
    }
}
