package lib;

import java.util.regex.Pattern;

public class Util {
    
    private static final Pattern P_TAB_OR_SPACE = Pattern.compile("[\t ]");
    
    private static double MinusLogEpsilon = 50.0;
    
    public static final String[] splitWithTabOrSpace(String str, int limit) {
        return P_TAB_OR_SPACE.split(str, limit);
    }
    
    public static int parseIntQuietly(String str, int defaultValue) {
        
        int value = defaultValue;
        if (str != null) {
            try {
                value = Integer.parseInt(str);
            }
            catch (RuntimeException e) {
                value = defaultValue;
            }
        }
        return value;
    }
    
    public static double parseDoubleQuietly(String str, double defaultValue) {
        
        double value = defaultValue;
        if (str != null) {
            try {
                value = Double.parseDouble(str);
            }
            catch (RuntimeException e) {
            }
        }
        return value;
    }
    
    public static double calcLogSumExp(double x, double y, boolean initFlag) {
        
        if (initFlag) {
            return y;
        }
        double vmin = Math.min(x, y);
        double vmax = Math.max(x, y);
        if (vmax > vmin + MinusLogEpsilon) {
            return vmax;
        }
        return vmax + Math.log(Math.exp(vmin - vmax) + 1.0);
    }
}
