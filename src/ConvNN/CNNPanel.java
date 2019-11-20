package ConvNN;

import NN.LossChart;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author user
 */
public class CNNPanel extends JPanel implements MouseListener, MouseMotionListener {

    public static CNNPanel dPanel;
    public static Color BACKGROUNDCOLOR = new Color(250, 250, 250);
    int width, height;
    public static boolean PAUSED = true;
    public CNN net;
    public int epoch;
    static LossChart l_c;
    public static Color neg_color = new Color(243, 182, 112);
    public static Color pos_color = new Color(133, 183, 204);
    int best_accuracy=0;
    public CNNPanel() throws IOException {
        addMouseListener(this);
        addMouseMotionListener(this);
        net = new CNN();
        net.init();
        net.training_step();
        l_c = new LossChart();

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BACKGROUNDCOLOR);
        g2.fillRect(0, 0, 1000, 700);
        g2.setStroke(new BasicStroke(0.6f));
        draw_network(g2);
        try {
            step(g2);
        } catch (IOException ex) {
            Logger.getLogger(CNNPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void step(Graphics2D g2d) throws IOException {
        if (PAUSED) {
            draw_network(g2d);
            // GA.draw_world(g2d, 50, 50);
            //    fitness.draw_loss_history(g2d, 900, 40);
            //    g2d.setColor(Color.black);
            //           g2d.drawString("Loss (Tr.):" + String.format("%.3f",(float) g.p.getFittest().fit_val), 800, 60);
            //           g2d.drawString("Epoch :" + epoch, 800, 80);

        } else {

            try {
                Thread.sleep(30);
            } catch (Exception e) {

            }

            epoch++;
            l_c.update_loss_data(net.ce_loss);
            // draw_network(g2d);
            net.training_step();
            repaint(50);
        }
    }

    public void draw_network(Graphics2D g2d) {
        //input layer
        //convolution filters
        //convolved images
        //max_pooled images
        //softmax_layer
        //Loss_graph
        width = getWidth();
        height = getHeight();
        draw_input_layer(g2d, 10, 50);
        draw_convolution_filters(g2d, 400, 50);
        draw_convolution_output(g2d, 460, 50);
        draw_maxpool_layer(g2d, 530, 50);
        draw_softmax_layer(g2d, 610, 50);
        draw_output_layer(g2d, 670, 50);

    }

    public void draw_input_layer(Graphics2D g2d, int x, int y) {
        g2d.setColor(pos_color);
        y = height / 2 - 200;
  //      g2d.drawRect(x, y, x + 300, y + 300);

        int rect_size = 300 / (net.pxl_dta.length - 1) + 2;
        for (int i = 0; i < net.pxl_dta.length; i++) {
            for (int j = 0; j < net.pxl_dta[0].length; j++) {
                g2d.setColor(interpolate_color(neg_color, pos_color, 2 * net.pxl_dta[j][i] - 1));
                g2d.fillRect(i * rect_size + x, j * rect_size + y, rect_size, rect_size);
                g2d.setColor(Color.white);
               g2d.drawRect(i * rect_size + x, j * rect_size + y, rect_size, rect_size);
            }
        }
        g2d.setColor(pos_color.darker());
        g2d.setFont(new Font("century gothic", Font.BOLD, 15));
        g2d.drawString("INPUT MATRIX", x + 140, y - 7);

    }

    public void draw_output_layer(Graphics2D g2d, int x, int y) {
        //draw predicted output
        y = height / 2 - 90;
        x = x + 50;
        int correct_label = net.correct_label;
        int predicted_label = net.predicted_label;
        g2d.setColor(pos_color);
        g2d.drawRect(x, y, 70, 70);
        g2d.setFont(new Font("century Gothic", Font.BOLD, 82));
        g2d.drawString("" + correct_label, x + 10, y + 67);
        g2d.drawRect(x + 120, y, 70, 70);

        if (correct_label != predicted_label) {
            g2d.setColor(neg_color);
        }
        g2d.setFont(new Font("century gothic", Font.BOLD, 82));
        g2d.drawString("" + predicted_label, x + 130, y + 67);

        g2d.setFont(new Font("century gothic", Font.BOLD, 14));
        g2d.setColor(pos_color.darker());
        g2d.drawString("PREDICTED LABEL:", x + 120 - 20, y - 3);
        g2d.drawString("CORRECT LABEL:", x - 20, y - 3);
        g2d.setStroke(new BasicStroke(1.0f));
        l_c.draw_loss_history(g2d, 670, y + 80);
        g2d.drawString("ACCURACY: " + net.accuracy + "%", 700, y + 150);
        g2d.drawString("LOSS: " + net.ce_loss, 830, y + 150);
        g2d.setFont(new Font("century gothic", Font.BOLD, 17));
        g2d.setColor(pos_color.darker());
        best_accuracy=(net.accuracy< best_accuracy ? best_accuracy:net.accuracy);
        g2d.drawString("BEST ACCURACY: " + best_accuracy + "%", 730, y + 180);

        //draw actual output
        //draw accuracy graph
        //draw epoch number
        //draw datapoint number
    }

    public Color interpolate_color(Color c1, Color c2, float val) {
        float[] hsb1 = new float[3];
        float[] hsb2 = new float[3];
        Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), hsb1);
        Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), hsb2);

        float b_inter = 0, h_inter = 0, s_inter = 0;
        if (val > 0) {
            //0    1
            //0.6  x
            //1    hsb1[2]
            b_inter = 1 + (hsb1[2] - 1) * Math.abs(val);
            h_inter = hsb1[0];
            s_inter = (hsb1[1]) * Math.abs(val);
        } else {
            b_inter = 1 + (hsb2[2] - 1) * Math.abs(val);
            h_inter = hsb2[0];
            s_inter = hsb2[1] * Math.abs(val);
        }
        int c_rgb = Color.HSBtoRGB(h_inter, s_inter, b_inter);
        return new Color((c_rgb >> 16) & 0xFF, (c_rgb >> 8) & 0xFF, c_rgb & 0xFF);
    }

    public void draw_convolution_filters(Graphics2D g2d, int x, int y) {
        Convolution c = net.conv;
        int filter_pos = y = height / 2 - 210;
        for (int i = 0; i < c.filters.length; i++) {//for each filter

            for (int j = 0; j < c.filters[0].length; j++) {
                for (int k = 0; k < c.filters[0][0].length; k++) {

                    g2d.setColor(interpolate_color(neg_color, pos_color, c.filters[i][j][k]));
                    g2d.fillRect(j * 10 + x, k * 10 + filter_pos, 10, 10);
                }
            }
            g2d.setColor(pos_color.darker());
            g2d.setStroke(new BasicStroke(2.0f));

            g2d.drawRect(x, filter_pos, 30, 30);
            filter_pos += 50;
        }
        g2d.setFont(new Font("century gothic", Font.BOLD, 15));
        g2d.drawString("FILTERS", x - 7, y - 7);
    }

    public void draw_convolution_output(Graphics2D g2d, int x, int y) {
        Convolution c = net.conv;
        int filter_pos = y;
        for (int i = 0; i < c.output.length; i++) {//for each filter

            for (int j = 0; j < c.output[0].length; j++) {
                for (int k = 0; k < c.output[0][0].length; k++) {
                    g2d.setColor(interpolate_color(neg_color, pos_color, 0.4f * c.output[i][k][j] - 1));
                    g2d.fillRect(j * 2 + x, k * 2 + filter_pos, 2, 2);
                }
            }
            g2d.setColor(pos_color.darker());
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRect(x, filter_pos, 50, 50);
            filter_pos += 60;
        }
        g2d.setFont(new Font("century gothic", Font.BOLD, 15));
        g2d.drawString("CONV.", x - 7, y - 7);
    }

    public void draw_maxpool_layer(Graphics2D g2d, int x, int y) {
        MaxPool mx = net.pool;
        int pool_pos = y;
        g2d.setFont(new Font("century gothic", Font.BOLD, 15));
        g2d.drawString("POOLING", x - 7, y - 7);

        for (int i = 0; i < mx.output.length; i++) {//for each filter

            for (int j = 0; j < mx.output[0].length; j++) {
                for (int k = 0; k < mx.output[0][0].length; k++) {

                    g2d.setColor(interpolate_color(neg_color, pos_color, 0.4f * mx.output[i][k][j] - 1f));
                    g2d.fillRect(j * 4 + x, k * 4 + pool_pos, 4, 4);
                }
            }
            g2d.setColor(pos_color.darker());
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRect(x, pool_pos, 52, 52);
            pool_pos += 60;
        }

    }

    public void draw_softmax_layer(Graphics2D g2d, int x, int y) {
        float[][] sm = net.out_l;
        int sm_pos = y = height / 2 - 120;
        ;
        g2d.setFont(new Font("century gothic", Font.BOLD, 15));
        g2d.drawString("SOFTMAX", x - 7, y - 7);

        for (int i = 0; i < sm[0].length; i++) {
            g2d.setColor(interpolate_color(neg_color, pos_color, 2 * sm[0][i] - 1f));
            g2d.fillRect(x, i * 20 + sm_pos, 20, 20);
            g2d.setColor(Color.black);
            g2d.drawRect(x, sm_pos + i * 20, 20, 20);
//             g2d.drawString(sm[0][i]+"", x+30, sm_pos+i*20);
        }
    }

    public static void main(String[] args) throws IOException {

        JFrame dFrame = new JFrame("MACHINE LEARNING PLAYGROUND");
        dFrame.setLayout(null);
        dPanel = new CNNPanel();
        dPanel.setDoubleBuffered(true);
        dFrame.setLocation(150, 90);
        dFrame.setSize(1200, 600);
        dPanel.setSize(1100, 600);
        dPanel.setLocation(30, 0);
        dFrame.add(dPanel);
        dFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dFrame.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (PAUSED) {
            PAUSED = !PAUSED;
            dPanel.repaint();
        } else {
            PAUSED = !PAUSED;
        }
        //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseDragged(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseMoved(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
