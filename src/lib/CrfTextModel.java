package lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CrfTextModel extends AbstractCrfModel implements CrfModel {
    
    private final Map<String, Integer> featureToIndexMap = new HashMap<String, Integer>();
    
    public boolean open(File textModelFile) {
        
        initialize();
        
        FileReader fr = null;
        BufferedReader br = null;
        boolean success = true;
        try {
            fr = new FileReader(textModelFile);
            br = new BufferedReader(fr);
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    throw new RuntimeException("format error: " + textModelFile.toString());
                }
                else if (line.length() == 0) {
                    break;
                }
                
                String[] elementArray = Util.splitWithTabOrSpace(line, 2);
                if (elementArray.length != 2) {
                    throw new RuntimeException("format error: " + textModelFile.toString());
                }
                
                String attribute = elementArray[0];
                int intValue = Util.parseIntQuietly(elementArray[1], 0);
                double doubleValue = Util.parseDoubleQuietly(elementArray[1], 0.0);
                
                if (attribute.startsWith("version:")){
                    version = intValue;
                }
                else if (attribute.startsWith("cost-factor:")) {
                    costFactor = doubleValue;
                }
                else if (elementArray[0].startsWith("maxid:")) {
                    maxId = intValue;
                }
                else if (elementArray[0].startsWith("xsize:")) {
                    xSize = intValue;
                }
            }
            
            if (VERSION / 100 != version / 100) {
                throw new RuntimeException("model version is different. " + VERSION + " vs " + version);
            }
            
            if (maxId <= 0) {
                throw new RuntimeException("maxid is not defined: " + textModelFile.toString());
            }
            
            if (xSize <= 0) {
                throw new RuntimeException("xsize is not defined: " + textModelFile.toString());
            }
            
            while (true) {
                line = br.readLine();
                if (line == null) {
                    throw new RuntimeException("format error: " + textModelFile.toString());
                }
                else if (line.length() == 0) {
                    break;
                }
                yList.add(line);
            }
            
            while (true) {
                line = br.readLine();
                if (line == null) {
                    throw new RuntimeException("format error: " + textModelFile.toString());
                }
                else if (line.length() == 0) {
                    break;
                }
                
                if (line.charAt(0) == 'U') {
                    unigramTemplsList.add(line);
                }
                else if (line.charAt(0) == 'B') {
                    bigramTemplsList.add(line);
                }
                else {
                    throw new RuntimeException("unknown type: " + line + " " + textModelFile.toString());
                }
            }
            
            while (true) {
                line = br.readLine();
                if (line == null) {
                    throw new RuntimeException("format error: " + textModelFile.toString());
                }
                else if (line.length() == 0) {
                    break;
                }
                
                String[] elementArray = Util.splitWithTabOrSpace(line, 2);
                if (elementArray.length != 2) {
                    throw new RuntimeException("format error: " + textModelFile.toString());
                }
                
                int alphaIndex = 0;
                try {
                    alphaIndex = Integer.parseInt(elementArray[0]);
                }
                catch (NumberFormatException e) {
                    // throws an exception intentionally
                    throw new NumberFormatException("format error: " + textModelFile.toString());
                }
                if (alphaIndex >= maxId) {
                    throw new RuntimeException("file is broken: " + textModelFile.toString());
                }
                String feature = elementArray[1];
                featureAlphaIndexMap.put(feature, alphaIndex);
            }
            
            List<Double> doubleList = new ArrayList<Double>();
            while ((line = br.readLine()) != null) {
                double alpha = 0.0;
                try {
                    alpha = Double.parseDouble(line);
                }
                catch (NumberFormatException e) {
                    // throws an exception intentionally
                    throw new NumberFormatException("format error: " + textModelFile.toString());
                }
                doubleList.add(alpha);
            }
            if (doubleList.size() != maxId) {
                throw new RuntimeException("file is broken: " + textModelFile.toString());
            }
            alphaList = doubleList;
        }
        catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        finally {
            if (br != null) {
                try { br.close(); } catch(Exception e) {}
            }
            if (fr != null) {
                try { fr.close(); } catch(Exception e) {}
            }
        }
        return success;
    }
    
    public int getID(String feature) {
        return featureToIndexMap.containsKey(feature) ? featureToIndexMap.get(feature) : -1;
    }
}