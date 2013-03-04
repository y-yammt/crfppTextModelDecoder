package lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCrfModel implements CrfModel {
    
    protected static final int VERSION = 100;
    
    private static final String[] BosArray = { "_B-1", "_B-2", "_B-3", "_B-4", "_B-5", "_B-6", "_B-7", "_B-8" };
    
    private static final String[] EosArray = { "_B+1", "_B+2", "_B+3", "_B+4", "_B+5", "_B+6", "_B+7", "_B+8" };
    
    private static final Pattern P_REF = Pattern.compile(
                    "%x\\[(-?\\d+?),(\\d+?)\\]"
    );
    
    protected int version = 0;
    
    protected double costFactor = 0.0;
    
    protected int xSize = 0;
    
    protected int maxId = 0;
    
    protected String templs = "";
    
    protected List<Double> alphaList = new ArrayList<Double>();
    
    protected List<String> unigramTemplsList = new ArrayList<String>();
    
    protected List<String> bigramTemplsList = new ArrayList<String>();
    
    protected List<String> yList = new ArrayList<String>();
    
    protected Map<String, Integer> featureAlphaIndexMap = new HashMap<String, Integer>();
    
    private boolean getFeatureList(List<String> templsList, InputSequence seq, int curIndex, List<Integer> featureList) {
        
        featureList.clear();
        boolean success = true;
        for (int i = 0; i < templsList.size(); ++i) {
            String templ = templsList.get(i);
            List<String[]> x = seq.getXList();
            StringBuffer sb = new StringBuffer();
            Matcher mRef = P_REF.matcher(templ);
            while (mRef.find()) {
                try {
                    int row = Integer.parseInt(mRef.group(1)) + curIndex;
                    int column = Integer.parseInt(mRef.group(2));
                    if (row >= 0 && row < x.size()) {
                        mRef.appendReplacement(sb, Matcher.quoteReplacement(x.get(row)[column]));
                    }
                    else if (row < 0) {
                        row = -row - 1;
                        mRef.appendReplacement(sb, Matcher.quoteReplacement(BosArray[row]));
                    }
                    else {
                        row = row - x.size();
                        mRef.appendReplacement(sb, Matcher.quoteReplacement(EosArray[row]));
                    }
                }
                catch (Exception e) {
                    success = false;
                }
            }
            mRef.appendTail(sb);
            String featureStr = sb.toString();
            Integer feature = featureAlphaIndexMap.get(featureStr);
            if (feature != null) {
                featureList.add(feature);
            }
        }
        return success;
    }
    
    private double getUnigramCost(List<Integer> unigramFeatureList, int yIndex) {
        double cost = 0.0;
        for (int i = 0; i < unigramFeatureList.size(); ++i) {
            cost += alphaList.get(unigramFeatureList.get(i) + yIndex);
        }
        return cost;
    }
    
    private double getBigramCost(List<Integer> bigramFeatureList, int yLeftIndex, int yRightIndex) {
        double cost = 0.0;
        for (int i = 0; i < bigramFeatureList.size(); ++i) {
            cost += alphaList.get(bigramFeatureList.get(i) + yLeftIndex * yList.size() + yRightIndex);
        }
        return cost;
    }
    
    protected void initialize() {
        
        version = 0;
        costFactor = 0.0;
        maxId = 0;
        xSize = 0;
        templs = "";
        alphaList.clear();
        unigramTemplsList.clear();
        bigramTemplsList.clear();
        yList.clear();
        featureAlphaIndexMap.clear();
    }
    
    public int getSize() {
        return maxId;
    }
    
    public int getXSize() {
        return xSize;
    }
    
    public int getYSize() {
        return yList.size();
    }
    
    public String getY(int i) {
        return yList.get(i);
    }
    
    public List<String> getYList() {
        return yList;
    }
    
    public double getCostFactor() {
        return costFactor;
    }
    
    public boolean setLatticeCost(Lattice lattice) {
        
        if (lattice == null || !yList.equals(lattice.yList)) {
            return false;
        }
        InputSequence inputSequence = lattice.inputSequence;
        if (inputSequence == null || xSize != inputSequence.getXSize()) {
            return false;
        }
        
        List<List<Node>> node2dList = lattice.node2dList;
        if (node2dList == null) {
            return false;
        }
        
        int sequenceSize = inputSequence.getSequenceSize();
        int ySize = yList.size();
        List<Integer> unigramFeatureList = new ArrayList<Integer>();
        List<Integer> bigramFeatureList = new ArrayList<Integer>();
        for (int i = 0; i < sequenceSize; ++i) {
            if (!getFeatureList(unigramTemplsList, inputSequence, i, unigramFeatureList)) {
                return false;
            }
            List<Node> yNodeList = node2dList.get(i);
            for (int j = 0; j < ySize; ++j) {
                Node node = yNodeList.get(j);
                node.cost = getUnigramCost(unigramFeatureList, j);
                if (i > 0) {
                    if (!getFeatureList(bigramTemplsList, inputSequence, i, bigramFeatureList)) {
                        return false;
                    }
                    for (int k = 0; k < ySize; ++k) {
                        Node leftNode = node2dList.get(i - 1).get(k);
                        Node rightNode = node;
                        double pathCost = getBigramCost(bigramFeatureList, k, j);
                        leftNode.rightPathList.get(j).cost = pathCost;
                        rightNode.leftPathList.get(k).cost = pathCost;
                    }
                }
            }
        }
        return true;
    }
}