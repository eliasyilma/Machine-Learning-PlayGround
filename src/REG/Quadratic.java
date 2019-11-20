package REG;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 *
 * @author user
 */
public class Quadratic {

    public float a0,a1,a2;
    public float intercept;
    public float x_estimate;
    public float y_estimate;
    public float correlation;

    public LinkedList<Point2> data = new LinkedList<>();

    public Quadratic(LinkedList<Point2> dta) {
        data = dta;
    }

    public void recompute_parameters() {
        float sum_x = 0;
        float sum_y = 0;
        float sum_xy = 0;
        float sum_x2y = 0;
        float sum_x2 = 0;
        float sum_x3 = 0;
        float sum_x4 = 0;
        float n = data.size();
        for (Point2 pt : data) {
            float lx = (float) (pt.x);
            float ly = (float) (pt.y);
            sum_x += lx;
            sum_y += ly;
            sum_xy += lx * ly;
            sum_x2 += lx * lx;
            sum_x3 += lx * lx * lx;
            sum_x4 += lx * lx * lx * lx;
            sum_x2y += lx * lx * ly;
        }
        float x_mean = sum_x / n;
        float y_mean = sum_y / n;
        solve_for_coefficients(n, sum_x, sum_x2, sum_x3, sum_x4, sum_y, sum_xy, sum_x2y);
        System.out.println("n: "+n+" sumx: " +sum_x+" sumx2: " +sum_x2+" sumx3: " + sum_x3+" sumx4: " + sum_x4+" sumy: " + sum_y+" sumxy: " + sum_xy+" sumx2y: " + sum_x2y);
    }

    public void solve_for_coefficients(float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8) {
        //use gauss-siedels iterative method
        //v1*a0+v2*a1+v3*a2=v6
        //v2*a0+v3*a1+v4*a2=v7
        //v3*a0+v4*a1+v5*a2=v8
        float a0 = 2, a1 = 2, a2 = 2;
        for (int i = 0; i < 50; i++) {
            a0 = (v6 - v2 * a1 - v3 * a2) / v1;
            a1 = (v7 - v2 * a0 - v4 * a2) / v3;
            a2 = (v8 - v3 * a0 - v4 * a1) / v5;
            System.out.println(""+a0+"  "+a1+" "+a2);
        }
        
        this.a0=a0;
        this.a1=a1;
        this.a2=a2;
    }

    public void draw_curve(Graphics2D g2d, float w, float h) {
        for (int i = 0; i < 96; i++) {
            float x1 = i * 0.125f;
            float x2 = (i + 1) * 0.125f;
            float y1 = y_estimate(x1);
            float y2 = y_estimate(x2);
            g2d.setStroke(new BasicStroke(2.0f));
            //      System.out.println("x1: "+x1+"y1: "+y1+"x2: "+x2+"y2: "+y2);
            g2d.drawLine((int) ((x1) / 12 * w), (int) ((y1 - 6) / -6 * h), (int) ((x2) / 12 * w), (int) ((y2 - 6) / -6 * h));
        }
    }

    public float y_estimate(float x_val) {
        return (float) (a2*x_val*x_val+a1*x_val+a0);
    }
//    public float x_estimate(float y_val) {
//        return (float) Math.pow(10, (Math.log10(y_val) - (intercept)) / slope);
//    }
}
