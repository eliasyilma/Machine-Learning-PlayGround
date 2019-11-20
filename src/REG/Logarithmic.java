package REG;

import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author user
 */


public class Logarithmic {
        public float slope;
    public float intercept;
    public float x_estimate;
    public float y_estimate;
        public float correlation;

    public LinkedList<Point2> data=new LinkedList<>();
    
    public Logarithmic(LinkedList<Point2> dta){
        data=dta;
    }
    
    public void recompute_parameters(){
        
    }
    
      public void draw_curve(Graphics2D g2d){
        
    }
}
