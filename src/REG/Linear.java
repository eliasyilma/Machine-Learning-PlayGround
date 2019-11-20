package REG;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author user
 */
public class Linear {

    public float slope;
    public float intercept;
    public float correlation;
    public LinkedList<Point2> data = new LinkedList<>();

    public Linear(LinkedList<Point2> dta) {
        data = dta;
    }

    public void recompute_parameters() {
        float sum_x = 0;
        float sum_y = 0;
        float sum_xy = 0;
        float sum_x2 = 0;
        float sum_y2 = 0;
        float n = data.size();
        for (Point2 pt : data) {
            sum_x += pt.x;
            sum_y += pt.y;
            sum_xy += pt.x * pt.y;
            sum_x2 += pt.x * pt.x;
            sum_y2 += pt.y * pt.y;
        }
        float x_mean=sum_x/n;
        float y_mean=sum_y/n;
        slope=(n*sum_xy-sum_x*sum_y)/(n*sum_x2-sum_x*sum_x);
        intercept=y_mean-slope*x_mean;
        correlation=(float) ((n*sum_xy-sum_x*sum_y)/((Math.sqrt(n*sum_x2-sum_x*sum_x)*Math.sqrt(n*sum_y2-sum_y*sum_y))));
    }

    public void draw_curve(Graphics2D g2d,float w,float h) {
        float y1=0;
        float y2=6;
        float x1=x_estimate(0);
        float x2=x_estimate(6);
        g2d.setStroke(new BasicStroke(2.0f));
  //      System.out.println("x1: "+x1+"y1: "+y1+"x2: "+x2+"y2: "+y2);
        g2d.drawLine((int)((x1)/12*w), (int)((y1-6)/-6*h), (int)((x2)/12*w),(int) ((y2-6)/-6*h));
    }
    
    public float y_estimate(float x_val){
        return slope*x_val+intercept;
    }
    
    public float x_estimate(float y_val){
        return (y_val-intercept)/slope;
    }

}
