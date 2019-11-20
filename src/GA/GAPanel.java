package GA;

import NN.LossChart;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

/**
 *
 * @author user
 */
public class GAPanel extends JPanel implements MouseListener {

    public static int population_size = 100;
    public static float mutation_probability = 0.001f;
    public static float crossover_prob = 0.02f;
    public static float selection_percentage = 0.8f;
    public static String target_function = "x^2+z^2";
    public static float minX = -5f, maxX = 5f, minZ = -5f, maxZ = 5f;
    public static GA g;
    public static LossChart fitness;
    public static boolean maximize;
    //color interpolator
    public static GAVis dPanel;
    public static boolean PAUSED = true;
    MouseEvent m;
    static int epoch = 0;

    public GAPanel() {
        addMouseListener(this);
        fitness = new LossChart();
    }

    private static Scene createScene() {
        Group root = new Group();
        Scene scene = new Scene(root);

        StackPane content = new StackPane();

        JFXDrawer settingsDrawer = new JFXDrawer();
        VBox settingsDrawerPane = new VBox();
        settingsDrawerPane.setSpacing(40.0);
        settingsDrawerPane.setPadding(new Insets(45, 45, 00, 40));

        JFXComboBox Crossover = new JFXComboBox();
        Crossover.setMaxWidth(200);
        Label ActivLabel = new Label("Crossover Probability");
        Crossover.setStyle("-fx-text-fill: BLACK;-fx-font-size: 13px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        VBox CrossoverPane = new VBox();
        CrossoverPane.setSpacing(5.0f);
        CrossoverPane.getChildren().addAll(new HBox(), ActivLabel, Crossover);
        CrossoverPane.setMinWidth(200);
        ObservableList<String> activation_functions = FXCollections.observableArrayList("0.25", "0.50", "0.75");
        Crossover.setItems(activation_functions);
        Crossover.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    PAUSED = true;
                    epoch = 0;
                    crossover_prob = Float.parseFloat(newValue.toString());
                    g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
                    dPanel.g.crossoverProbability = Float.parseFloat(newValue.toString());
                    dPanel.l_c.loss_history.clear();
                    g.step();
                    dPanel.g = g;
                    //fitness = new LossChart();
                    dPanel.repaint();
                }
        );
        Crossover.setValue(activation_functions.get(0));

        JFXComboBox function = new JFXComboBox();
        function.setMaxWidth(200);
        Label functionLabel = new Label("FUNCTION, y = f(x,z)");
        Crossover.setStyle("-fx-text-fill: BLACK;-fx-font-size: 13px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        VBox functionPane = new VBox();
        functionPane.setSpacing(5.0f);
        functionPane.getChildren().addAll(new HBox(), functionLabel, function);
        functionPane.setMinWidth(200);
        ObservableList<String> function_list = FXCollections.observableArrayList("x^2+z^2", "sin(x)+sin(z)", "x^2", "9*(x^2+y^2)*e^(-x^2-y^2)", "e^{-(x^2+y^2)^{0.5}} cos(4x) cos(4y)");
        function.setItems(function_list);
        function.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    PAUSED = true;
                    epoch = 0;
                    target_function = (newValue.toString());
                    dPanel.function_type = target_function;
                    dPanel.g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
                    dPanel.l_c.loss_history.clear();
                    System.out.println(" " + newValue.toString());
                    System.out.println(" " + dPanel.function_type);

                    dPanel.repaint();

                }
        );
        function.setValue(function_list.get(0));

        FontAwesomeIconView run_icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        run_icon.setStyle("-fx-font-size: 18px; -fx-fill: WHITE;");

        FontAwesomeIconView pause_icon = new FontAwesomeIconView(FontAwesomeIcon.PAUSE);
        pause_icon.setStyle("-fx-font-size: 18px; -fx-fill: WHITE;");

        FontAwesomeIconView reset_icon = new FontAwesomeIconView(FontAwesomeIcon.UNDO);
        reset_icon.setStyle("-fx-font-size: 18px; -fx-fill: WHITE;");

        FontAwesomeIconView step_icon = new FontAwesomeIconView(FontAwesomeIcon.STEP_FORWARD);
        step_icon.setStyle("-fx-font-size: 18px; -fx-fill: WHITE;");

        JFXButton run = new JFXButton();
        run.setButtonType(JFXButton.ButtonType.FLAT);
        run.setRipplerFill(rgb(238, 190, 182));
        run.setStyle("-fx-background-color:rgb(20,20,20);-fx-font-size: 24px;-fx-pref-width: 50px;-fx-pref-height: 50px;-fx-text-fill: WHITE; -fx-border-radius: 50px;-fx-background-radius: 50px;");
        run.setGraphic(run_icon);
        //     run.setFont(f);

        JFXButton reset = new JFXButton();
        reset.setButtonType(JFXButton.ButtonType.FLAT);
        reset.setRipplerFill(rgb(220, 220, 220));
        reset.setStyle("-fx-background-color:rgb(20,20,20);-fx-font-size: 12px;-fx-pref-width: 25px;-fx-pref-height: 25px;-fx-text-fill: WHITE; -fx-border-radius: 25px;-fx-background-radius: 25px;");
        reset.setGraphic(reset_icon);

        JFXButton step = new JFXButton();
        step.setButtonType(JFXButton.ButtonType.FLAT);
        step.setRipplerFill(rgb(220, 220, 220));
        step.setStyle("-fx-background-color:rgb(20,20,20);-fx-font-size: 12px;-fx-pref-width: 25px;-fx-pref-height: 25px;-fx-text-fill: WHITE; -fx-border-radius: 25px;-fx-background-radius: 25px;");
        step.setGraphic(step_icon);

        VBox buttonPane2 = new VBox();
        HBox buttonPane = new HBox();

        buttonPane.setSpacing(15.0);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.getChildren().addAll(reset, run, step);
        buttonPane.setPrefWidth(200);
        buttonPane.setPadding(new Insets(10, 00, 00, 00));
        buttonPane2.getChildren().addAll(buttonPane, new HBox(new Label("     ")));
        buttonPane2.setSpacing(20.0f);

        VBox pop = new VBox();
        pop.setSpacing(8);
        pop.setPadding(new Insets(10, 10, 0, 0));
        Label pop_size_label = new Label("Population Size");
        JFXSlider pop_size = new JFXSlider();
        pop_size.setMinWidth(200);
        pop_size.setMax(400);
        pop_size.setMin(50);
        pop_size.setBlockIncrement(50);
        pop_size.setSnapToTicks(true);
        pop_size.setMajorTickUnit(50);
        pop_size.setMinorTickCount(0);
        pop_size.setShowTickMarks(true);
        pop_size.setShowTickLabels(true);
        pop_size.setIndicatorPosition(JFXSlider.IndicatorPosition.LEFT);
        pop_size.addEventHandler(javafx.scene.input.MouseEvent.DRAG_DETECTED, (e) -> {
            population_size = (int) pop_size.getValue();
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.population_size = population_size;
            dPanel.g = g;
            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        pop_size.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            population_size = (int) pop_size.getValue();
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.population_size = population_size;
            dPanel.g = g;
            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        pop_size.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, (e) -> {
            population_size = (int) pop_size.getValue();
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.population_size = population_size;
            dPanel.g = g;
            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        pop_size.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            population_size = (int) pop_size.getValue();
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.population_size = population_size;
            dPanel.g = g;

            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        pop.getChildren().addAll(pop_size_label, pop_size);

        VBox mut = new VBox();
        mut.setSpacing(8);
        mut.setPadding(new Insets(10, 10, 0, 0));
        Label mut_prob_label = new Label("Mutation Probability %");
        JFXSlider mut_prob = new JFXSlider();
        mut_prob.setMinWidth(200);
        mut_prob.setMax(60);
        mut_prob.setMin(0);
        mut_prob.setBlockIncrement(10);
        mut_prob.setSnapToTicks(false);
        mut_prob.setMajorTickUnit(10);
        mut_prob.setMinorTickCount(1);
        mut_prob.setShowTickMarks(true);
        mut_prob.setShowTickLabels(true);
        mut_prob.setIndicatorPosition(JFXSlider.IndicatorPosition.LEFT);
        mut_prob.addEventHandler(javafx.scene.input.MouseEvent.DRAG_DETECTED, (e) -> {
            mutation_probability = (float) mut_prob.getValue() / 100f;
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.mutationProbability = mutation_probability;
            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        mut_prob.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            mutation_probability = (float) mut_prob.getValue() / 100f;
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.mutationProbability = mutation_probability;
            dPanel.g = g;

            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        mut_prob.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, (e) -> {
            mutation_probability = (float) mut_prob.getValue() / 100f;
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.mutationProbability = mutation_probability;
            dPanel.g = g;

            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        mut_prob.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            mutation_probability = (float) mut_prob.getValue() / 100f;
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            g.mutationProbability = mutation_probability;
            dPanel.g = g;

            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });
        mut.getChildren().addAll(mut_prob_label, mut_prob);

        JFXCheckBox maxim = new JFXCheckBox("MAXIMIZE");
        maxim.setFont(javafx.scene.text.Font.font("Aliquam", 15));
        maxim.setCheckedColor(rgb(33, 127, 188));
        VBox max = new VBox();
        max.setAlignment(Pos.CENTER_LEFT);
        max.getChildren().add(maxim);
        maxim.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                (e) -> {
                    if (maxim.isSelected()) {
                        dPanel.PAUSED = true;
                        epoch = 0;
                        maximize = true;
                        g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
                        dPanel.l_c.loss_history.clear();
                        dPanel.g = g;
                        g.step();
                        fitness = new LossChart();
                        dPanel.repaint();
                    } else {
                        dPanel.PAUSED = true;
                        epoch = 0;
                       g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
                        dPanel.g = g;
                        g.maximize = false;
                        g.p.maximum = false;
                        g.step();
                        fitness = new LossChart();
                        dPanel.repaint();
                    }
                });

        settingsDrawerPane.getChildren().addAll(buttonPane2, functionPane, mut, pop, CrossoverPane, max, new VBox());
        settingsDrawerPane.setAlignment(Pos.CENTER);
        settingsDrawerPane.setStyle("-fx-background-color:rgb(255,255,255)");
        settingsDrawer.setDefaultDrawerSize(150);
        settingsDrawer.setPrefWidth(1000);
        settingsDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        settingsDrawer.setSidePane(settingsDrawerPane);
        settingsDrawer.setOverLayVisible(true);
        settingsDrawer.setResizableOnDrag(false);

        JFXDrawersStack drawersStack = new JFXDrawersStack();
        drawersStack.setContent(content);
        drawersStack.toggle(settingsDrawer, true);

        run.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            run.setGraphic(run_icon);
            if (dPanel.PAUSED) {
                run.setGraphic(pause_icon);
                dPanel.PAUSED = !dPanel.PAUSED;
                dPanel.repaint();
            } else {
                run.setGraphic(run_icon);
                dPanel.PAUSED = !dPanel.PAUSED;
            }
        });

        reset.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            dPanel.PAUSED = true;
            epoch = 0;
            g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
            dPanel.l_c.loss_history.clear();
            dPanel.g = g;

            g.step();
            fitness = new LossChart();
            dPanel.repaint();
        });

        step.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            dPanel.PAUSED = true;
            g.step();
            dPanel.g = g;
            epoch++;
            // fitness.update_loss_data((float) g.p.getFittest(g.target_function).fit_val);
            dPanel.repaint();
        });

        root.getChildren().add(drawersStack);
        return (scene);
    }

    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);

    }

    private static void initAndShowGUI() {
        JFrame dFrame = new JFrame("MACHINE LEARNING PLAYGROUND");
        dFrame.setLayout(null);
        dPanel = new GAVis();
        dPanel.setSize(800, 700);
        dPanel.setLocation(300, 0);
        dPanel.setDoubleBuffered(true);
        g = new GA(population_size, mutation_probability, crossover_prob, selection_percentage, minX, maxX, minZ, maxZ, target_function, maximize);
        g.step();
        dPanel.g = g;
        final JFXPanel fxPanel = new JFXPanel();
        fxPanel.setLocation(0, 0);
        fxPanel.setSize(300, 800);

        dFrame.setLocation(50, 90);
        dFrame.setSize(1100, 700);
        dFrame.add(dPanel);
        dFrame.add(fxPanel);

        dFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dFrame.setVisible(true);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    public static void main(String[] args) {
        initAndShowGUI();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int mx = me.getX();
        int my = me.getY();
    }

    @Override
    public void mousePressed(MouseEvent me) {
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

}
