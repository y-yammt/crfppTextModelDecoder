package lib;

public class Path {
    
    public Node leftNode = null;
    
    public Node rightNode = null;
    
    public double cost = 0.0;
    
    public Path(Node leftNode, Node rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }
}
