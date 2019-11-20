package GA;

import NN.LossChart;
import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.math.BigDecimal;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import static jdk.nashorn.internal.objects.NativeMath.round;

/**
 *
 * @author user
 */
public class GAVis extends JPanel implements MouseListener, MouseMotionListener {

    public float scale = 1.0f;
    public float scalemin = 0.05f;
    public float scalemax = 5f;
    int mx, my;  // the most recently recorded mouse coordinates
    public static GAVis dPanel;
    public static Color BACKGROUNDCOLOR = new Color(250, 250, 250);
    int width, height;
    int extent = 15; //extent of the canvas in both x and y directions
    int azimuth = 0, elevation = 0;
    public static Color xC = new Color(23, 240, 15);
    public static Color yC = new Color(230, 24, 15);
    public Point3[][] point_cloud;
    public static Color zC = new Color(33, 127, 188);
    public String function_type = "x^2+z^2";
    public GA g;
    public static boolean PAUSED = true;
    MouseEvent m;
    static int epoch = 0;
    public static LossChart l_c;
    public GAVis() {
        addMouseListener(this);
        addMouseMotionListener(this);
        generate_point_cloud(-6, 6, -6, 6, 0.3f);
           l_c = new LossChart();
        addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double delta = 0.05f * e.getWheelRotation();
                scale -= delta;
                if (scale > scalemax) {
                    scale = scalemax;
                }
                if (scale < scalemin) {
                    scale = scalemin;
                }
                revalidate();
                repaint();
            }

        });

    }

    public void drawAxes(Graphics2D g) {
        Point3 origin = new Point3(0, 0, 0);
        Point3 x_axis = new Point3(2, 0, 0);
        Point3 y_axis = new Point3(0, 2, 0);
        Point3 z_axis = new Point3(0, 0, 2);

        orthoProject(azimuth, elevation, origin);
        orthoProject(azimuth, elevation, x_axis);
        orthoProject(azimuth, elevation, y_axis);
        orthoProject(azimuth, elevation, z_axis);

        g.setStroke(new BasicStroke(2));
//x-axis
        g.setColor(xC);
        g.drawLine(origin.projection.x, origin.projection.y, x_axis.projection.x, x_axis.projection.y);
        drawText(g, "X", x_axis.projection.x + 5, x_axis.projection.y + 5);
//y-axis
        g.setColor(yC);
        g.drawLine(origin.projection.x, origin.projection.y, y_axis.projection.x, y_axis.projection.y);
        drawText(g, "Y", y_axis.projection.x + 5, y_axis.projection.y + 5);
//z-axis
        g.setColor(zC);
        g.drawLine(origin.projection.x, origin.projection.y, z_axis.projection.x, z_axis.projection.y);
        drawText(g, "Z", z_axis.projection.x + 5, z_axis.projection.y + 5);

    }

    public void drawText(Graphics2D g, String s, int x, int y) {
        g.drawString(s, x, y);
    }

    public void orthoProject(int azimuth, int elevation, Point3 point) {
        width = getSize().width;
        height = getSize().height;
        float scaleFactor = width * scale / extent;
        float near = 15f;  // distance from eye to near plane
        float nearToObj = 1.5f;  // distance from near plane to center of object
        float[] coordinates = new float[2];
        double x0 = point.x, y0 = point.y, z0 = point.z;
        double theta = Math.PI * azimuth / 180.0;
        double phi = Math.PI * elevation / 180.0;
        float cosT = (float) Math.cos(theta), sinT = (float) Math.sin(theta);
        float cosP = (float) Math.cos(phi), sinP = (float) -Math.sin(phi);
        float cosTcosP = cosT * cosP, cosTsinP = cosT * sinP,
                sinTcosP = sinT * cosP, sinTsinP = sinT * sinP;
        coordinates[0] = (float) (cosT * x0 + sinT * z0);
        coordinates[1] = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
        point.projection = new Point2((int) (width / 2 + scaleFactor * coordinates[0] + 0.5), (int) (height * 0.7 - scaleFactor * coordinates[1] + 0.5));

    }

    public void draw_population(Graphics2D g2d) {
        //get population from g
        Color blu = new Color(33, 127, 188);
        //use blue dots to draw the population
        g2d.setColor(blu);
        for (int i = 0; i < g.p.individuals.length; i++) {
            Individual ind = g.p.individuals[i];
            orthoProject(azimuth, elevation, ind.pt);
            g2d.fillOval(ind.pt.projection.x - 3, ind.pt.projection.y - 3, 6, 6);
            drawText(g2d, "i" + i, ind.pt.projection.x - 3, ind.pt.projection.y - 3);

        }
        //highlight the fittest individual using a square.
        g2d.setFont(new Font("century Gothic", Font.BOLD, 14));
        g2d.setColor(Color.GREEN);
        g.p.getFittest(function_type);
        Individual fittest = g.p.fittest;
        g2d.setStroke(new BasicStroke(2));
        orthoProject(azimuth, elevation, fittest.pt);
        g2d.fillOval(fittest.pt.projection.x - 3, fittest.pt.projection.y - 3, 6, 6);
        g2d.drawRect(fittest.pt.projection.x - 5, fittest.pt.projection.y - 5, 10, 10);
        g2d.setColor(Color.black);
        g2d.drawString("OPTIMUM: [" + round(fittest.pt.x, 4) + " , " + round(fittest.pt.z, 4) + "]", fittest.pt.projection.x - 5, fittest.pt.projection.y - 5);
        g2d.setFont(new Font("century Gothic", Font.BOLD, 15));
        g2d.drawString("FITNESS VALUE, Y:   " + round(fittest.fit_val, 3),385, 580);
        g2d.drawString("EPOCH:   " + epoch, 385, 600);
        g2d.drawString("AVERAGE POPULATION FITNESS: "+ g.p.average_fitness(), 385, 620);

    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BACKGROUNDCOLOR);
        g2.fillRect(0, 0, width, height);

        g2.setStroke(new BasicStroke(2));
        generate_point_cloud(-6, 6, -6, 6, 0.3f);

        // draw X,Y,and Z axes.
        drawAxes(g2);

        //
        g2.setColor(new Color(213, 0, 0));
        g2.setStroke(new BasicStroke(0.6f));
        step(g2);

    }

    public void step(Graphics2D g2d) {
        if (PAUSED) {
            draw_point_cloud(g2d);
            draw_population(g2d);
            // GA.draw_world(g2d, 50, 50);
            //    fitness.draw_loss_history(g2d, 900, 40);
            //    g2d.setColor(Color.black);
            //           g2d.drawString("Loss (Tr.):" + String.format("%.3f",(float) g.p.getFittest().fit_val), 800, 60);
            //           g2d.drawString("Epoch :" + epoch, 800, 80);
            l_c.draw_loss_history(g2d, 70, 570);
        } else {
            g.step();
            try {
                Thread.sleep(100);
            } catch (Exception e) {

            }

            epoch++;
            draw_point_cloud(g2d);
            draw_population(g2d);
            l_c.update_loss_data(g.p.average_fitness());
            l_c.draw_loss_history(g2d, 70, 570);
          //  g.p.print_population();
            //GA.draw_world(g2d, 50, 50);
//            fitness.update_loss_data((float) g.p.getFittest().fit_val);
//            fitness.draw_loss_history(g2d, 900, 40);
            //           g2d.setColor(Color.black);
//            g2d.drawString("Loss (Tr.):" + String.format("%.3f", g.p.getFittest().fit_val), 800, 60);
            //           g2d.drawString("Epoch :" + epoch, 800, 80);
            System.out.println("fittest: " + g.p.getFittest(function_type).pt.y + " mut: " + g.mutationProbability + " crsov: " + g.crossoverProbability + " pop: " + g.population_size);

            repaint(50);
        }
    }

    public void generate_point_cloud(float x_min, float x_max, float z_min, float z_max, float res) {
        int x_point_count = (int) ((x_max - x_min) / res);
        int z_point_count = (int) ((z_max - z_min) / res);
        point_cloud = new Point3[x_point_count][z_point_count];
        //create 2D point cloud
        for (int i = 0; i < x_point_count; i++) {
            float x_val = x_min + i * res;
            for (int j = 0; j < z_point_count; j++) {

                float z_val = z_min + j * res;
                Point3 p = new Point3(x_val, f(x_val, z_val), z_val);
                point_cloud[i][j] = p;
                orthoProject(azimuth, elevation, p);
            }
            x_val = x_min;
        }
        //for each point (x,z) in the point cloud
        //compute function value

    }

    public void draw_point_cloud(Graphics2D g2d) {
        for (int i = 0; i < point_cloud.length; i++) {
            for (int j = 1; j < point_cloud[0].length; j++) {
                Point3 p1 = point_cloud[i][j - 1];
                Point3 p2 = point_cloud[i][j];
                orthoProject(azimuth, elevation, p1);
                orthoProject(azimuth, elevation, p2);
                g2d.drawLine(p1.projection.x, p1.projection.y, p2.projection.x, p2.projection.y);
            }
        }
        for (int i = 1; i < point_cloud.length; i++) {
            for (int j = 0; j < point_cloud[0].length; j++) {
                Point3 p1 = point_cloud[i - 1][j];
                Point3 p2 = point_cloud[i][j];
                orthoProject(azimuth, elevation, p1);
                orthoProject(azimuth, elevation, p2);
                g2d.drawLine(p1.projection.x, p1.projection.y, p2.projection.x, p2.projection.y);
            }
        }
    }

    public float f(float x, float z) {
        float f = 0;
        switch (function_type) {
            case "x^2+z^2":
                f = (float) ((float) (x * x + z * z));
                break;
            case "sin(x)+sin(z)":
                f = (float) ((float) Math.sin(z) + Math.sin(x));
                break;
            case "x^2":
                f = (float) ((float) Math.sin(z));
                break;
            case "9*(x^2+y^2)*e^(-x^2-y^2)":
                f = (float) ((float) 4 * (x * x + z * z) * Math.exp((-x * x - z * z) / 6f));
                break;
            case "e^{-(x^2+y^2)^{0.5}} cos(4x) cos(4y)":
                f = (float) (Math.exp(-Math.sqrt(x * x / 12f + z * z / 12f) * 2f) * Math.cos(2 * x) * Math.cos(2 * z)) * 2f;

        }
        return f;
    }

 

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {

        } else if (SwingUtilities.isLeftMouseButton(e)) {
            // get the latest mouse position
            int new_mx = e.getX();
            int new_my = e.getY();

            // adjust angles according to the distance travelled by the mouse
            // since the last event
            azimuth += new_mx - mx;
            elevation += new_my - my;

            // update the backbuffer
//        drawWireframe(backg);
            // update our data
            mx = new_mx;
            my = new_my;
            repaint();
            e.consume();

            //       System.out.println("azimuth : " + azimuth + " elevation : " + elevation);
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

}
