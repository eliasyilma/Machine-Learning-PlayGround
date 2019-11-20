package K_MEANS;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javafx.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author CASE
 */
public class Visualizer extends JPanel implements MouseListener {

    public static K_MEANS kmn;
    public Color[] cluster_colors;
    public static Visualizer dPanel;
    public Visualizer() {
        kmn = new K_MEANS(7, 0.1f, 100, 1000);
        kmn.init_centroids();
        init_cluster_colors();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(231, 231, 231));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(Color.RED);
        draw_data_points(g2d);
        g2d.drawRect(0, 0, 5, 5);
        draw_centroids(g2d);
        cluster_step();
        draw_cluster(g2d);
    }

    public Color generate_random_color() {
        int rgb = Color.HSBtoRGB((float) Math.random(), 0.8f, 0.7f);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new Color(red, green, blue);
    }

    public void draw_point(Graphics2D g2d, Point p, Color clr) {
        int px = (int) p.x * 5;
        int py = (int) p.y * 5;
        g2d.setColor(clr);
        g2d.fillRect(px - 3, py - 3, 3, 3);
    }

    //draw points
    public void draw_data_points(Graphics2D g2d) {
        for (int i = 0; i < kmn.dta.length; i++) {
            Point p = kmn.dta[i];
            draw_point(g2d, p, Color.black);
        }
    }

    public void init_cluster_colors(){
         cluster_colors = new Color[kmn.num_of_clusters];
        for (int i = 0; i < kmn.centroid.length; i++) {
            Point p = kmn.dta[i];
            cluster_colors[i] = generate_random_color();
        }
    }
    
    //draw centroids
    public void draw_centroids(Graphics2D g2d) {
       

        for (int i = 0; i < kmn.centroid.length; i++) {
            Point p = kmn.dta[i];
            int px = (int) p.x * 5;
            int py = (int) p.y * 5;
            g2d.setColor(cluster_colors[i]);
            g2d.fillRect(px - 7, py - 7, 7, 7);
        }
    }

    //draw cluster lines
    public void draw_cluster(Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, 1000, 1000);
        for (int i = 0; i < kmn.num_of_clusters; i++) {
            //redraw points
            Cluster c = kmn.clusters[i];
            Color colr = cluster_colors[i];
            for (int j = 0; j < c.dta_points.size(); j++) {
                Point p = c.dta_points.get(j);
                draw_point(g2d, p, colr);
                g2d.setStroke(new BasicStroke(0.3f));
                g2d.drawLine((int) p.x * 5, (int) p.y * 5, (int) kmn.centroid[i].x * 5, (int) kmn.centroid[i].y * 5);
            }
            //draw line between points and centroid
        }
    }

    public static void cluster_step() {
        
            //reset clusters
            for(int i=0;i<kmn.num_of_clusters;i++){
                kmn.clusters[i]=new Cluster();
            }
            
            for (int j = 0; j < kmn.dta.length; j++) {
                int closest = kmn.find_closest_centroid_index(kmn.dta[j]);
                kmn.clusters[closest].dta_points.add(kmn.dta[j]);
            }

            //backup the previous centroidal data.
            Point[] previous = new Point[kmn.centroid.length];
            for (int k = 0; k < previous.length; k++) {
                previous[k] = new Point(kmn.centroid[k].x, kmn.centroid[k].y);
            }

            kmn.recalculate_centroids(kmn.clusters);

            //check the difference between the current and previous total gradients
            //if the gradient is small enough, then exit the loop.
            float diff = kmn.compute_centroid_gradient(previous, kmn.centroid);
            System.out.println("grad: " + diff);
           // if (diff < kmn.tolerance) {
           //     break;
           // }
            
        
    }
    //generate random colors and assign
    //draw cluster

    public static void main(String[] args) {

        JFrame dFrame = new JFrame("K-Means Visualizer");

        Button draw = new Button("step");
        dFrame.setLayout(null);
        dPanel = new Visualizer();
        dPanel.setDoubleBuffered(true);
        dFrame.setLocation(350, 90);
        dFrame.setSize(600, 600);
        dPanel.setSize(500, 500);
        draw.setSize(70, 25);
        dPanel.setLocation(50, 0);
        draw.setLocation(0, 500);
        dFrame.add(dPanel);
        dFrame.add(draw);
        //dFrame.add(nlpInput);
        long st = System.nanoTime();
        long en = System.nanoTime();
        System.out.println("blender is active!");
        System.out.println("elapsed: " + (en - st) / 1000000000.0f + " seconds");
        dFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dFrame.setVisible(true);

        draw.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                cluster_step();
                dPanel.repaint();
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
