package NN;

import K_MEANS.Point;

/**
 *
 * @author CASE
 */


public class DataPoint {
    public Point loc;
    public float label;
    
    public DataPoint(Point l,float lbl){
        loc=l;
        label=lbl;
    }
}
