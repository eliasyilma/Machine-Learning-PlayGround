package REG;

import GA.GA;
import static GA.GAPanel.crossover_prob;
import static GA.GAPanel.g;
import static GA.GAPanel.maxX;
import static GA.GAPanel.maxZ;
import static GA.GAPanel.minX;
import static GA.GAPanel.minZ;
import static GA.GAPanel.mutation_probability;
import static GA.GAPanel.population_size;
import static GA.GAPanel.selection_percentage;
import static GA.GAPanel.target_function;
import static GA.GAVis.BACKGROUNDCOLOR;
import NN.LossChart;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.rgb;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author user
 */
public class RegressionPanel extends JPanel implements MouseListener, MouseMotionListener {

    public static RegressionPanel dPanel;
    public Color BACKGROUNDC = new Color(230, 230, 230);
    public int width, height;
    public int mx, my;

    Linear lin;
    Logarithmic log;
    Exponential exp;
    Quadratic quad;
    Power pow;

    public boolean LINEAR = false;

    public boolean QUADRATIC = true;
    public boolean EXPONENTIAL = false;
    public boolean POWER = false;
    public boolean BEST_FIT = false;

    public Color LINEARC = new Color(143, 77, 243);

    public Color QUADC = new Color(253, 180, 32);
    public Color POWERC = new Color(243, 98, 77);
    public Color EXPC = new Color(48, 106, 198);
    public Color BEST_FITC = new Color(68, 68, 68);

    public LinkedList<Point2> data = new LinkedList<>();

    public RegressionPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        width = getWidth();
        height = getHeight();

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BACKGROUNDC);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.blue);
        g2.drawRect(5, 5, getWidth() - 10, getHeight() - 150);
        g2.setStroke(new BasicStroke(2));

        // draw X,Y,and Z axes.
        draw_regression_info(g2);

        //
        g2.setColor(new Color(213, 0, 0));
        g2.setStroke(new BasicStroke(0.6f));

    }

    public void draw_regression_info(Graphics2D g2d) {
        draw_axes(g2d);
        draw_crosshair(g2d);
        draw_regression_switches(g2d);
        draw_regression_curve(g2d);
          draw_data_points(g2d);
        //draw_data_points
        //draw_error_lines
        //draw_regression_equation
        //draw_correlation coefficient
    }

    public void draw_regression_switches(Graphics2D g2d) {
        //draw linear switch
        int btn_size_x = 130;
        int btn_size_y = 30;
        g2d.setFont(new Font("century gothic", Font.BOLD, 15));
        draw_switch(g2d, "LIN", 55, 600);
        draw_switch(g2d, "EXP", 195, 600);
        draw_switch(g2d, "QUAD", 335, 600);
        draw_switch(g2d, "POW", 475, 600);

        //draw exponential switch
        //draw logarithmic switch
        //draw quadratic switch
        //draw best_fit switch
    }

    public void draw_switch(Graphics2D g2d, String reg_type, int x, int y) {
        String str = "";
        boolean swtched = true;
        Color swtch_clr = Color.black;
        int btn_size_x = 130;
        int btn_size_y = 30;
        switch (reg_type) {
            case "LIN":
                swtch_clr = (LINEARC);
                str = "LINEAR";
                swtched = LINEAR;
                break;
            case "EXP":
                swtch_clr = (EXPC);
                str = "EXPONENTIAL";
                swtched = EXPONENTIAL;
                break;
            case "POW":
                swtch_clr = (POWERC);
                str = "POWER";
                swtched = POWER;
                break;
            case "QUAD":
                swtch_clr = (QUADC);
                str = "QUAD";
                swtched = QUADRATIC;
                break;
        }

        if (swtched) {
            g2d.setColor(swtch_clr);
            g2d.fillRect(x - 25, y - 20, btn_size_x, btn_size_y);
            g2d.setColor(Color.white);
            g2d.drawString(str, x, y);
        } else {
            g2d.setColor(swtch_clr);
            g2d.drawString(str, x, y);
            g2d.drawRect(x - 25, y - 20, btn_size_x, btn_size_y);
        }

    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    public void draw_regression_curve(Graphics2D g2d) {
        width = getWidth();
        height = getHeight() - 145;
        if (data.size() >= 2) {
            if (LINEAR) {
                lin = new Linear(data);
                lin.recompute_parameters();
                g2d.setColor(LINEARC);
                lin.draw_curve(g2d, width, height);
                g2d.drawString("BEST FIT: " + " y = " + lin.slope + " x +" + lin.intercept, 50, 650);

            } else if (EXPONENTIAL) {
                exp = new Exponential(data);
                exp.recompute_parameters();
                g2d.setColor(EXPC);
                exp.draw_curve(g2d, width, height);
                g2d.drawString("BEST FIT: " + " y = " + Math.exp(exp.intercept) + "* e ^ (" + exp.slope + " * x)", 50, 550);

            } else if (POWER) {
                pow = new Power(data);
                pow.recompute_parameters();
                g2d.setColor(POWERC);
                pow.draw_curve(g2d, width, height);
                g2d.drawString("BEST FIT: " + " y = " + Math.pow(10, pow.intercept) + "* (x ^" + pow.slope + ")", 50, 550);
            } else if (QUADRATIC && data.size() >= 3) {
                quad = new Quadratic(data);
                g2d.setColor(QUADC);
                quad.recompute_parameters();
                quad.draw_curve(g2d, width, height);
                g2d.drawString("BEST FIT: " + " y = " + quad.a2 + "*x^2 +" + quad.a1 + "*x + " + quad.a0, 50, 550);

            }
        }
    }

    public void draw_axes(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1.0f));
        width = getWidth();
        height = getHeight() - 145;
        g2d.drawLine(0, height + 5, width, height + 5);
        g2d.drawLine(width + 5, 0, width + 5, height);
        draw_axes_ticks(g2d);
    }

    public void draw_axes_ticks(Graphics2D g2d) {
        //draw horizontal ticks
        width = getWidth();
        height = getHeight() - 145;
        float major_tick_interval = width / 12f;
        float minor_tick_interval = width / 24f;
        for (int i = -12; i <= 12; i++) {
            g2d.setColor(Color.blue);
            g2d.drawLine((int) (minor_tick_interval * (i + 12)), height + 5, (int) (minor_tick_interval * (i + 12)), height + 5 + 4);
        }

        for (int i = -6; i <= 6; i++) {
            g2d.setColor(Color.red);
            g2d.drawLine((int) (major_tick_interval * (i + 6)), height + 5, (int) (major_tick_interval * (i + 6)), height + 5 + 7);
        }

        //draw vertical ticks
        major_tick_interval = width / 12f;
        minor_tick_interval = width / 24f;
        for (int i = -6; i <= 6; i++) {
            g2d.setColor(Color.blue);
            g2d.drawLine(0, (int) (minor_tick_interval * (i + 6)), 9, (int) (minor_tick_interval * (i + 6)));
        }

        for (int i = -3; i <= 3; i++) {
            g2d.setColor(Color.red);
            g2d.drawLine(0, (int) (major_tick_interval * (i + 3)), 12, (int) (major_tick_interval * (i + 3)));
        }

    }

    public void draw_curve_information(Graphics2D g2d) {
    }

    public void draw_crosshair(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(0.6f));
        g2d.drawLine(0, my, width, my);
        g2d.drawLine(mx, 0, mx, height);

    }

    public void draw_data_points(Graphics2D g2d) {
        for (Point2 p : data) {
            g2d.fillRect(p.pxlx - 2, p.pxly - 2, 4, 4);
            g2d.drawString("(" + round(p.x, 3) + "," + round(p.y, 3) + ")", p.pxlx + 2, p.pxly + 2);
        }
    }

    public Point2 find_near_point(Point2 p, float f) {
        Point2 nr = null;
        for (Point2 pt : data) {
            float dx = pt.pxlx - p.pxlx;
            float dy = pt.pxly - p.pxly;
            float dist = (float) Math.sqrt((dx * dx) + (dy * dy));
            if (dist < 5) {
                nr = pt;
                break;
            }
        }
        return nr;
    }

    public void handle_switches(int mx, int my) {
        System.out.println("hello");
        if (inside_rect(mx, my, 55, 600, 130, 30)) {
            LINEAR = true;
            EXPONENTIAL = false;
            QUADRATIC = false;
            POWER = false;
        } else if (inside_rect(mx, my, 195, 600, 130, 30)) {
            LINEAR = false;
            EXPONENTIAL = true;
            QUADRATIC = false;
            POWER = false;
        } else if (inside_rect(mx, my, 335, 600, 130, 30)) {
            LINEAR = false;
            EXPONENTIAL = false;
            QUADRATIC = true;
            POWER = false;
        } else if (inside_rect(mx, my, 475, 600, 130, 30)) {
            LINEAR = false;
            EXPONENTIAL = false;
            QUADRATIC = false;
            POWER = true;
        }
        repaint();
    }

    public boolean inside_rect(int px, int py, int rX, int rY, int sizeX, int sizeY) {
        if ((px - 25 > rX && px - 25 < rX + sizeX && py - 20 > rY && py - 20 < rY + sizeY)) {
            System.out.println("touched");
        }
        return (px > rX - 25 && px < rX + sizeX - 25 && py > rY - 20 && py < rY + sizeY - 20);
    }

    public static void main(String[] args) {

        JFrame dFrame = new JFrame("MACHINE LEARNING PLAYGROUND");
        dFrame.setLayout(null);
        dPanel = new RegressionPanel();
        dPanel.setDoubleBuffered(true);
        dFrame.setLocation(50, 50);
        dFrame.setSize(1030, 670);
        dPanel.setSize(1000, 650);
        dPanel.setLocation(5, 5);
        dFrame.add(dPanel);
        dFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dFrame.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent me) {

        if (SwingUtilities.isRightMouseButton(me)) {
            int ptx = me.getX();
            int pty = me.getY();
            if (pty < getHeight() - 145) {

                float ptx_f = 12 * (ptx / (float) width);
                float pty_f = 6 - 6 * (pty / (float) height);
                Point2 p = new Point2(ptx_f, pty_f, ptx, pty);
                Point2 nearest = find_near_point(p, 5);
                if (nearest != null) {
                    data.remove(nearest);
                }
                repaint();
            }
        } else if (SwingUtilities.isLeftMouseButton(me)) {
            int ptx = me.getX();
            int pty = me.getY();
            if (pty < getHeight() - 145) {
                float ptx_f = 12 * (ptx / (float) width);
                float pty_f = 6 - 6 * (pty / (float) height);
                Point2 p = new Point2(ptx_f, pty_f, ptx, pty);
                data.add(p);

            }
            System.out.println("ptx: " + ptx + " pty: " + pty);
            handle_switches(ptx, pty);
            repaint();
        }

//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        mx = me.getX();
        my = me.getY();
        repaint();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
