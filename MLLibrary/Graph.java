import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph extends JPanel {
    
    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private static final long serialVersionUID = 1L;
    private int labelPadding = 40;
    private Color lineColor = new Color(255, 255, 254);

    // TODO: Add point colors for each type of data point
    private Color tpColor = new Color(0, 0, 255);
    private Color fpColor = new Color(0, 255,255);
    private Color tnColor = new Color(255, 255, 0);
    private Color fnColor = new Color(255, 0, 0);

    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    // TODO: Change point width as needed
    private static int pointWidth = 10;

    // Number of grids and the padding width
    private int numXGridLines = 6;
    private int numYGridLines = 6;
    private int padding = 40;

    private List<DataPoint> data;
    
    public double truePositive = 0; 
    public double falsePositive = 0;
    public double trueNegative = 0;
    public double falseNegative = 0;

    private static KNNModel model;
    
    JLabel accLabel = new JLabel();
    JLabel preLabel = new JLabel();

    //SLIDER PORTION
    JSlider slider = new JSlider (JSlider.HORIZONTAL, MIN, MAX, INIT);
   
    public Slider(){
        private static final long serialVersionUID = 1L;
        static final int MIN =2;
        static final int MAX = 25;
        static final int INIT = 5;

        setLayout(new BoxLayout (this, BoxLayout.PAGE_AXIS));

        JLabel sliderLabel = new JLabel ("Choose the majority value", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font ("Times New Roman", Font.ITALIC,20);
        slider.setFont(font);
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton test= new JButton ("Run Test");
        test.addActionListener(new ActionListener()){
            @Override
            public void actionPerformed (Action event){
                int value = slider.getValue();
                sliderLabel.setText ("Value is:" + ((value*2)-1));
            }
            public void stateChanged (ChangedEvent e){
                JSlider source = (JSlider)e.getSource();
                int value = source.getValue();
                sliderLabel.setText("Value is:" + ((value*2)-1));

            }
        }
        add(sliderLabel);
        add(slider);
        add(button);
    }

        
    /**
     * Constructor method
     */
    public Graph(List<DataPoint> testData, List<DataPoint> trainData) {
        this.data = testData;
        model = new KNNModel(4);
        model.train((ArrayList<DataPoint>) trainData);
        this.setLayout(new FlowLayout());
        this.add(accLabel);
        this.add(preLabel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double minF1 = getMinF1Data();
        double maxF1 = getMaxF1Data();
        double minF2 = getMinF2Data();
        double maxF2 = getMaxF2Data();

        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - 
                labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLUE);

        double yGridRatio = (maxF2 - minF2) / numYGridLines;
        for (int i = 0; i < numYGridLines + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
                    labelPadding)) / numYGridLines + padding + labelPadding);
            int y1 = y0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = String.format("%.2f", (minF2 + (i * yGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        double xGridRatio = (maxF1 - minF1) / numXGridLines;
        for (int i = 0; i < numXGridLines + 1; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / (numXGridLines) + padding + labelPadding;
            int x1 = x0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = String.format("%.2f", (minF1 + (i * xGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw the main axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() -
                padding, getHeight() - padding - labelPadding);

        // Draw the points
        paintPoints(g2, minF1, maxF1, minF2, maxF2);
    }

    private void paintPoints(Graphics2D g2, double minF1, double maxF1, double minF2, double maxF2) {
        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        double xScale = ((double) getWidth() - (3 * padding) - labelPadding) /(maxF1 - minF1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxF2 - minF2);
        g2.setStroke(oldStroke);
        for (int i = 0; i < data.size(); i++) {
            int x1 = (int) ((data.get(i).getF1() - minF1) * xScale + padding + labelPadding);
            int y1 = (int) ((maxF2 - data.get(i).getF2()) * yScale + padding);
            int x = x1 - pointWidth / 2;
            int y = y1 - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;

            DataPoint datapoint = data.get(i);
            String result = model.test(datapoint);
            if (result.equals("1") && datapoint.getLabel().equals("1")) {
                g2.setColor(tpColor);
                truePositive++;
            }
            if (result.equals("1") && datapoint.getLabel().equals("0")) {
                g2.setColor(fpColor);
                falsePositive ++; 
            }
            if (result.equals("0") && datapoint.getLabel().equals("1")) {
                g2.setColor(tnColor);
                falseNegative++;
            }
            if (result.equals("0") && datapoint.getLabel().equals("0")) {
                g2.setColor(fnColor);
                trueNegative++;
            }
            g2.fillOval(x, y, ovalW, ovalH);
        }
        Double accuracy = (truePositive + trueNegative) / 
            (truePositive + trueNegative + falsePositive + falseNegative);
        Double precision =  truePositive / (truePositive + falseNegative);
        accLabel.setText("Accuracy:" + accuracy);
        preLabel.setText("Precision:" + precision);
    }

    /*
     * @Return the min values
     */
    private double getMinF1Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF1());
        }
        return minData;
    }

    private double getMinF2Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF2());
        }
        return minData;
    }


    /*
     * @Return the max values;
     */
    private double getMaxF1Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF1());
        }
        return maxData;
    }

    private double getMaxF2Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF2());
        }
        return maxData;
    }

    /* Mutator */
    public void setData(List<DataPoint> data) {
        this.data = data;
        invalidate();
        this.repaint();
    }

    /* Accessor */
    public List<DataPoint> getData() {
        return data;
    }
    
    
    /*  Run createAndShowGui in the main method, where we create the frame too and pack it in the panel*/
    private static void createAndShowGui(List<DataPoint> testData, List<DataPoint> trainData) {

        /* Main panel */
        Graph mainPanel = new Graph(testData, trainData);

        // Feel free to change the size of the panel
        mainPanel.setPreferredSize(new Dimension(500, 300));

        /* creating the frame */
        JFrame frame = new JFrame("CS 112 Lab Part 3");
        mainPanel.setPreferredSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
      
    /* The main method runs createAndShowGui*/
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            List<DataPoint> testData = new ArrayList<>();
            List<DataPoint> trainData = new ArrayList<>();
            Random random = new Random();
            try (Scanner scanner = new Scanner(new File("titanic.csv"))) {
                while (scanner.hasNextLine()) {
                    List<String> records = getRecordFromLine(scanner.nextLine());
                    String label = records.get(records.size() - 6);
                    Double fare;
                    Double age;
                    try {
                        fare = Double.valueOf(records.get(records.size() - 1));
                        age = Double.valueOf(records.get(records.size() - 2));
                    }
                    catch(NumberFormatException e) {
                        continue;
                    }       
                    // 90% of the data is reserved for training
                    if (random.nextDouble() < 0.9) {
                        DataPoint dp = new DataPoint(age, fare, label, "train");
                        trainData.add(dp);
                    } else {
                        DataPoint dp = new DataPoint(age, fare, label, "test");
                        testData.add(dp);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                return;
            }
            createAndShowGui(testData, trainData);
         }
      });
    }
}

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph extends JPanel {
    
    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private static final long serialVersionUID = 1L;
    private int labelPadding = 40;
    private Color lineColor = new Color(255, 255, 254);

    // TODO: Add point colors for each type of data point
    private Color tpColor = new Color(0, 0, 255);
    private Color fpColor = new Color(0, 255,255);
    private Color tnColor = new Color(255, 255, 0);
    private Color fnColor = new Color(255, 0, 0);

    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    // TODO: Change point width as needed
    private static int pointWidth = 10;

    // Number of grids and the padding width
    private int numXGridLines = 6;
    private int numYGridLines = 6;
    private int padding = 40;

    private List<DataPoint> data;
    
    public double truePositive = 0; 
    public double falsePositive = 0;
    public double trueNegative = 0;
    public double falseNegative = 0;

    private static KNNModel model;
    
    JLabel accLabel = new JLabel();
    JLabel preLabel = new JLabel();

    //SLIDER PORTION
    JSlider slider = new JSlider (JSlider.HORIZONTAL, MIN, MAX, INIT);
   
    public Slider(){
        private static final long serialVersionUID = 1L;
        static final int MIN =2;
        static final int MAX = 25;
        static final int INIT = 5;

        setLayout(new BoxLayout (this, BoxLayout.PAGE_AXIS));

        JLabel sliderLabel = new JLabel ("Choose the majority value", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font ("Times New Roman", Font.ITALIC,20);
        slider.setFont(font);
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton test= new JButton ("Run Test");
        test.addActionListener(new ActionListener()){
            @Override
            public void actionPerformed (Action event){
                int value = slider.getValue();
                sliderLabel.setText ("Value is:" + ((value*2)-1));
            }
            public void stateChanged (ChangedEvent e){
                JSlider source = (JSlider)e.getSource();
                int value = source.getValue();
                sliderLabel.setText("Value is:" + ((value*2)-1));

            }
        }
        add(sliderLabel);
        add(slider);
        add(button);
    }

        
    /**
     * Constructor method
     */
    public Graph(List<DataPoint> testData, List<DataPoint> trainData) {
        this.data = testData;
        model = new KNNModel(4);
        model.train((ArrayList<DataPoint>) trainData);
        this.setLayout(new FlowLayout());
        this.add(accLabel);
        this.add(preLabel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double minF1 = getMinF1Data();
        double maxF1 = getMaxF1Data();
        double minF2 = getMinF2Data();
        double maxF2 = getMaxF2Data();

        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - 
                labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLUE);

        double yGridRatio = (maxF2 - minF2) / numYGridLines;
        for (int i = 0; i < numYGridLines + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
                    labelPadding)) / numYGridLines + padding + labelPadding);
            int y1 = y0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = String.format("%.2f", (minF2 + (i * yGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        double xGridRatio = (maxF1 - minF1) / numXGridLines;
        for (int i = 0; i < numXGridLines + 1; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / (numXGridLines) + padding + labelPadding;
            int x1 = x0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = String.format("%.2f", (minF1 + (i * xGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw the main axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() -
                padding, getHeight() - padding - labelPadding);

        // Draw the points
        paintPoints(g2, minF1, maxF1, minF2, maxF2);
    }

    private void paintPoints(Graphics2D g2, double minF1, double maxF1, double minF2, double maxF2) {
        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        double xScale = ((double) getWidth() - (3 * padding) - labelPadding) /(maxF1 - minF1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxF2 - minF2);
        g2.setStroke(oldStroke);
        for (int i = 0; i < data.size(); i++) {
            int x1 = (int) ((data.get(i).getF1() - minF1) * xScale + padding + labelPadding);
            int y1 = (int) ((maxF2 - data.get(i).getF2()) * yScale + padding);
            int x = x1 - pointWidth / 2;
            int y = y1 - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;

            DataPoint datapoint = data.get(i);
            String result = model.test(datapoint);
            if (result.equals("1") && datapoint.getLabel().equals("1")) {
                g2.setColor(tpColor);
                truePositive++;
            }
            if (result.equals("1") && datapoint.getLabel().equals("0")) {
                g2.setColor(fpColor);
                falsePositive ++; 
            }
            if (result.equals("0") && datapoint.getLabel().equals("1")) {
                g2.setColor(tnColor);
                falseNegative++;
            }
            if (result.equals("0") && datapoint.getLabel().equals("0")) {
                g2.setColor(fnColor);
                trueNegative++;
            }
            g2.fillOval(x, y, ovalW, ovalH);
        }
        Double accuracy = (truePositive + trueNegative) / 
            (truePositive + trueNegative + falsePositive + falseNegative);
        Double precision =  truePositive / (truePositive + falseNegative);
        accLabel.setText("Accuracy:" + accuracy);
        preLabel.setText("Precision:" + precision);
    }

    /*
     * @Return the min values
     */
    private double getMinF1Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF1());
        }
        return minData;
    }

    private double getMinF2Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF2());
        }
        return minData;
    }


    /*
     * @Return the max values;
     */
    private double getMaxF1Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF1());
        }
        return maxData;
    }

    private double getMaxF2Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF2());
        }
        return maxData;
    }

    /* Mutator */
    public void setData(List<DataPoint> data) {
        this.data = data;
        invalidate();
        this.repaint();
    }

    /* Accessor */
    public List<DataPoint> getData() {
        return data;
    }
    
    
    /*  Run createAndShowGui in the main method, where we create the frame too and pack it in the panel*/
    private static void createAndShowGui(List<DataPoint> testData, List<DataPoint> trainData) {

        /* Main panel */
        Graph mainPanel = new Graph(testData, trainData);

        // Feel free to change the size of the panel
        mainPanel.setPreferredSize(new Dimension(500, 300));

        /* creating the frame */
        JFrame frame = new JFrame("CS 112 Lab Part 3");
        mainPanel.setPreferredSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
      
    /* The main method runs createAndShowGui*/
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            List<DataPoint> testData = new ArrayList<>();
            List<DataPoint> trainData = new ArrayList<>();
            Random random = new Random();
            try (Scanner scanner = new Scanner(new File("titanic.csv"))) {
                while (scanner.hasNextLine()) {
                    List<String> records = getRecordFromLine(scanner.nextLine());
                    String label = records.get(records.size() - 6);
                    Double fare;
                    Double age;
                    try {
                        fare = Double.valueOf(records.get(records.size() - 1));
                        age = Double.valueOf(records.get(records.size() - 2));
                    }
                    catch(NumberFormatException e) {
                        continue;
                    }       
                    // 90% of the data is reserved for training
                    if (random.nextDouble() < 0.9) {
                        DataPoint dp = new DataPoint(age, fare, label, "train");
                        trainData.add(dp);
                    } else {
                        DataPoint dp = new DataPoint(age, fare, label, "test");
                        testData.add(dp);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                return;
            }
            createAndShowGui(testData, trainData);
         }
      });
    }
}
import java.util.Scimport java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph extends JPanel {
    
    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private static final long serialVersionUID = 1L;
    private int labelPadding = 40;
    private Color lineColor = new Color(255, 255, 254);

    // TODO: Add point colors for each type of data point
    private Color tpColor = new Color(0, 0, 255);
    private Color fpColor = new Color(0, 255,255);
    private Color tnColor = new Color(255, 255, 0);
    private Color fnColor = new Color(255, 0, 0);

    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    // TODO: Change point width as needed
    private static int pointWidth = 10;

    // Number of grids and the padding width
    private int numXGridLines = 6;
    private int numYGridLines = 6;
    private int padding = 40;

    private List<DataPoint> data;
    
    public double truePositive = 0; 
    public double falsePositive = 0;
    public double trueNegative = 0;
    public double falseNegative = 0;

    private static KNNModel model;
    
    JLabel accLabel = new JLabel();
    JLabel preLabel = new JLabel();
        
    /**
     * Constructor method
     */
    public Graph(List<DataPoint> testData, List<DataPoint> trainData) {
        this.data = testData;
        model = new KNNModel(4);
        model.train((ArrayList<DataPoint>) trainData);
        this.setLayout(new FlowLayout());
        this.add(accLabel);
        this.add(preLabel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double minF1 = getMinF1Data();
        double maxF1 = getMaxF1Data();
        double minF2 = getMinF2Data();
        double maxF2 = getMaxF2Data();

        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - 
                labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLUE);

        double yGridRatio = (maxF2 - minF2) / numYGridLines;
        for (int i = 0; i < numYGridLines + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
                    labelPadding)) / numYGridLines + padding + labelPadding);
            int y1 = y0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = String.format("%.2f", (minF2 + (i * yGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        double xGridRatio = (maxF1 - minF1) / numXGridLines;
        for (int i = 0; i < numXGridLines + 1; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / (numXGridLines) + padding + labelPadding;
            int x1 = x0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = String.format("%.2f", (minF1 + (i * xGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw the main axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() -
                padding, getHeight() - padding - labelPadding);

        // Draw the points
        paintPoints(g2, minF1, maxF1, minF2, maxF2);
    }

    private void paintPoints(Graphics2D g2, double minF1, double maxF1, double minF2, double maxF2) {
        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        double xScale = ((double) getWidth() - (3 * padding) - labelPadding) /(maxF1 - minF1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxF2 - minF2);
        g2.setStroke(oldStroke);
        for (int i = 0; i < data.size(); i++) {
            int x1 = (int) ((data.get(i).getF1() - minF1) * xScale + padding + labelPadding);
            int y1 = (int) ((maxF2 - data.get(i).getF2()) * yScale + padding);
            int x = x1 - pointWidth / 2;
            int y = y1 - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;

            DataPoint datapoint = data.get(i);
            String result = model.test(datapoint);
            if (result.equals("1") && datapoint.getLabel().equals("1")) {
                g2.setColor(tpColor);
                truePositive++;
            }
            if (result.equals("1") && datapoint.getLabel().equals("0")) {
                g2.setColor(fpColor);
                falsePositive ++; 
            }
            if (result.equals("0") && datapoint.getLabel().equals("1")) {
                g2.setColor(tnColor);
                falseNegative++;
            }
            if (result.equals("0") && datapoint.getLabel().equals("0")) {
                g2.setColor(fnColor);
                trueNegative++;
            }
            g2.fillOval(x, y, ovalW, ovalH);
        }
        Double accuracy = (truePositive + trueNegative) / 
            (truePositive + trueNegative + falsePositive + falseNegative);
        Double precision =  truePositive / (truePositive + falseNegative);
        accLabel.setText("Accuracy:" + accuracy);
        preLabel.setText("Precision:" + precision);
    }

    /*
     * @Return the min values
     */
    private double getMinF1Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF1());
        }
        return minData;
    }

    private double getMinF2Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF2());
        }
        return minData;
    }


    /*
     * @Return the max values;
     */
    private double getMaxF1Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF1());
        }
        return maxData;
    }

    private double getMaxF2Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF2());
        }
        return maxData;
    }

    /* Mutator */
    public void setData(List<DataPoint> data) {
        this.data = data;
        invalidate();
        this.repaint();
    }

    /* Accessor */
    public List<DataPoint> getData() {
        return data;
    }
    
    
    /*  Run createAndShowGui in the main method, where we create the frame too and pack it in the panel*/
    private static void createAndShowGui(List<DataPoint> testData, List<DataPoint> trainData) {

        /* Main panel */
        Graph mainPanel = new Graph(testData, trainData);

        // Feel free to change the size of the panel
        mainPanel.setPreferredSize(new Dimension(500, 300));

        /* creating the frame */
        JFrame frame = new JFrame("CS 112 Lab Part 3");
        mainPanel.setPreferredSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
      
    /* The main method runs createAndShowGui*/
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            List<DataPoint> testData = new ArrayList<>();
            List<DataPoint> trainData = new ArrayList<>();
            Random random = new Random();
            try (Scanner scanner = new Scanner(new File("titanic.csv"))) {
                while (scanner.hasNextLine()) {
                    List<String> records = getRecordFromLine(scanner.nextLine());
                    String label = records.get(records.size() - 6);
                    Double fare;
                    Double age;
                    try {
                        fare = Double.valueOf(records.get(records.size() - 1));
                        age = Double.valueOf(records.get(records.size() - 2));
                    }
                    catch(NumberFormatException e) {
                        continue;
                    }       
                    // 90% of the data is reserved for training
                    if (random.nextDouble() < 0.9) {
                        DataPoint dp = new DataPoint(age, fare, label, "train");
                        trainData.add(dp);
                    } else {
                        DataPoint dp = new DataPoint(age, fare, label, "test");
                        testData.add(dp);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                return;
            }
            createAndShowGui(testData, trainData);
         }
      });
    }
}
anner;

public class Graph extends JPanel{
	private static List<String> getFrom (String line){
		List<String> values = new ArrayList<String>();
		try (Scanner scan= new Scanner (line)){
			scan.useDelimiter(",");
			while(scan.hasNext());
		}
	return values;
	}
	private static final long serialVersionUID = 1L;
    private int labelPadding = 40;
    private Color lineColor = new Color(255, 255, 254);

    // TODO: Add point colors for each type of data point
    private Color pointColor = new Color(255, 0, 255);
    private Color pointColor = new Color(0, 0, 255);
    private Color pointColor = new Color(0, 255, 255);
    private Color pointColor = new Color(255, 255, 0);
    private Color pointColor = new Color(255, 0, 0);
    
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static final int DataPoint = 0;

    // TODO: Change point width as needed
    private static int pointWidth = 15;

    // Number of grids and the padding width
    private int numXGridLines = 6;
    private int numYGridLines = 6;
    private int padding = 40;

    private List<DataPoint> data;
    
    private var truePositive=0;
    private var trueNegative=0;
    private var FalsePositive=0;
    private var FalseNegative=0;import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph extends JPanel {
    
    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private static final long serialVersionUID = 1L;
    private int labelPadding = 40;
    private Color lineColor = new Color(255, 255, 254);

    // TODO: Add point colors for each type of data point
    private Color tpColor = new Color(0, 0, 255);
    private Color fpColor = new Color(0, 255,255);
    private Color tnColor = new Color(255, 255, 0);
    private Color fnColor = new Color(255, 0, 0);

    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    // TODO: Change point width as needed
    private static int pointWidth = 10;

    // Number of grids and the padding width
    private int numXGridLines = 6;
    private int numYGridLines = 6;
    private int padding = 40;

    private List<DataPoint> data;
    
    public double truePositive = 0; 
    public double falsePositive = 0;
    public double trueNegative = 0;
    public double falseNegative = 0;

    private static KNNModel model;
    
    JLabel accLabel = new JLabel();
    JLabel preLabel = new JLabel();
        
    /**
     * Constructor method
     */
    public Graph(List<DataPoint> testData, List<DataPoint> trainData) {
        this.data = testData;
        model = new KNNModel(4);
        model.train((ArrayList<DataPoint>) trainData);
        this.setLayout(new FlowLayout());
        this.add(accLabel);
        this.add(preLabel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double minF1 = getMinF1Data();
        double maxF1 = getMaxF1Data();
        double minF2 = getMinF2Data();
        double maxF2 = getMaxF2Data();

        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - 
                labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLUE);

        double yGridRatio = (maxF2 - minF2) / numYGridLines;
        for (int i = 0; i < numYGridLines + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
                    labelPadding)) / numYGridLines + padding + labelPadding);
            int y1 = y0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = String.format("%.2f", (minF2 + (i * yGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        double xGridRatio = (maxF1 - minF1) / numXGridLines;
        for (int i = 0; i < numXGridLines + 1; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / (numXGridLines) + padding + labelPadding;
            int x1 = x0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = String.format("%.2f", (minF1 + (i * xGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw the main axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() -
                padding, getHeight() - padding - labelPadding);

        // Draw the points
        paintPoints(g2, minF1, maxF1, minF2, maxF2);
    }

    private void paintPoints(Graphics2D g2, double minF1, double maxF1, double minF2, double maxF2) {
        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        double xScale = ((double) getWidth() - (3 * padding) - labelPadding) /(maxF1 - minF1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxF2 - minF2);
        g2.setStroke(oldStroke);
        for (int i = 0; i < data.size(); i++) {
            int x1 = (int) ((data.get(i).getF1() - minF1) * xScale + padding + labelPadding);
            int y1 = (int) ((maxF2 - data.get(i).getF2()) * yScale + padding);
            int x = x1 - pointWidth / 2;
            int y = y1 - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;

            DataPoint datapoint = data.get(i);
            String result = model.test(datapoint);
            if (result.equals("1") && datapoint.getLabel().equals("1")) {
                g2.setColor(tpColor);
                truePositive++;
            }
            if (result.equals("1") && datapoint.getLabel().equals("0")) {
                g2.setColor(fpColor);
                falsePositive ++; 
            }
            if (result.equals("0") && datapoint.getLabel().equals("1")) {
                g2.setColor(tnColor);
                falseNegative++;
            }
            if (result.equals("0") && datapoint.getLabel().equals("0")) {
                g2.setColor(fnColor);
                trueNegative++;
            }
            g2.fillOval(x, y, ovalW, ovalH);
        }
        Double accuracy = (truePositive + trueNegative) / 
            (truePositive + trueNegative + falsePositive + falseNegative);
        Double precision =  truePositive / (truePositive + falseNegative);
        accLabel.setText("Accuracy:" + accuracy);
        preLabel.setText("Precision:" + precision);
    }

    /*
     * @Return the min values
     */
    private double getMinF1Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF1());
        }
        return minData;
    }

    private double getMinF2Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF2());
        }
        return minData;
    }


    /*
     * @Return the max values;
     */
    private double getMaxF1Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF1());
        }
        return maxData;
    }

    private double getMaxF2Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF2());
        }
        return maxData;
    }

    /* Mutator */
    public void setData(List<DataPoint> data) {
        this.data = data;
        invalidate();
        this.repaint();
    }

    /* Accessor */
    public List<DataPoint> getData() {
        return data;
    }
    
    
    /*  Run createAndShowGui in the main method, where we create the frame too and pack it in the panel*/
    private static void createAndShowGui(List<DataPoint> testData, List<DataPoint> trainData) {

        /* Main panel */
        Graph mainPanel = new Graph(testData, trainData);

        // Feel free to change the size of the panel
        mainPanel.setPreferredSize(new Dimension(500, 300));

        /* creating the frame */
        JFrame frame = new JFrame("CS 112 Lab Part 3");
        mainPanel.setPreferredSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
      
    /* The main method runs createAndShowGui*/
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            List<DataPoint> testData = new ArrayList<>();
            List<DataPoint> trainData = new ArrayList<>();
            Random random = new Random();
            try (Scanner scanner = new Scanner(new File("titanic.csv"))) {
                while (scanner.hasNextLine()) {
                    List<String> records = getRecordFromLine(scanner.nextLine());
                    String label = records.get(records.size() - 6);
                    Double fare;
                    Double age;
                    try {
                        fare = Double.valueOf(records.get(records.size() - 1));
                        age = Double.valueOf(records.get(records.size() - 2));
                    }
                    catch(NumberFormatException e) {
                        continue;
                    }       
                    // 90% of the data is reserved for training
                    if (random.nextDouble() < 0.9) {
                        DataPoint dp = new DataPoint(age, fare, label, "train");
                        trainData.add(dp);
                    } else {
                        DataPoint dp = new DataPoint(age, fare, label, "test");
                        testData.add(dp);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                return;
            }
            createAndShowGui(testData, trainData);
         }
      });
    }
}


    // TODO: Add a private KNNModel variable
   private String KNNModel;
	/**
	 * Constructor method
	 */
    public Graph(List<DataPoint> testData, List<DataPoint> trainData) {
       this.data = testData;
       KNNModel=new KNNModel();
       KNNModel.train((arrayList(<DataPoint>) trainData);
        // TODO: instantiate a KNNModel variable
        // TODO: Run train with the trainData
    }

    private int arrayList() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double minF1 = getMinF1Data();
        double maxF1 = getMaxF1Data();
        double minF2 = getMinF2Data();
        double maxF2 = getMaxF2Data();

        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - 
        		labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLUE);

        double yGridRatio = (maxF2 - minF2) / numYGridLines;
        for (int i = 0; i < numYGridLines + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
            		labelPadding)) / numYGridLines + padding + labelPadding);
            int y1 = y0;
            if (data.size()        g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
               g2.setColor(Color.BLACK);
                String yLabel = String.format("%.2f", (minF2 + (i * yGridRatio)));
                FontMetricsmetrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
           }
            g2.drawLine(x0, y0, x1, y1);
        }

        double xGridRatio = (maxF1 - minF1) / numXGridLines;
        for (int i = 0; i < numXGridLines + 1; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
           int x0 = i * (getWidth() - padding * 2 - labelPadding) / (numXGridLines) + padding + labelPadding;
            int x1 = x0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = String.format("%.2f", (minF1 + (i * xGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw the main axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() -
        		padding, getHeight() - padding - labelPadding
        // Draw the point
        paintPoints(g2, miF1, maxF1, minF2, maxF2);
    }

    private void paintPoints(Graphics2D g2, double minF1, double maxF1, double minF2, double maxF2) {
        Stroke oldStroke = g2.getStroke();
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Graph extends JPanel {
    
    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private static final long serialVersionUID = 1L;
    private int labelPadding = 40;
    private Color lineColor = new Color(255, 255, 254);

    // TODO: Add point colors for each type of data point
    private Color tpColor = new Color(0, 0, 255);
    private Color fpColor = new Color(0, 255,255);
    private Color tnColor = new Color(255, 255, 0);
    private Color fnColor = new Color(255, 0, 0);

    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    // TODO: Change point width as needed
    private static int pointWidth = 10;

    // Number of grids and the padding width
    private int numXGridLines = 6;
    private int numYGridLines = 6;
    private int padding = 40;

    private List<DataPoint> data;
    
    public double truePositive = 0; 
    public double falsePositive = 0;
    public double trueNegative = 0;
    public double falseNegative = 0;

    private static KNNModel model;
    
    JLabel accLabel = new JLabel();
    JLabel preLabel = new JLabel();

    //SLIDER PORTION
    JSlider slider = new JSlider (JSlider.HORIZONTAL, MIN, MAX, INIT);
   
    public Slider(){
        private static final long serialVersionUID = 1L;
        static final int MIN =2;
        static final int MAX = 25;
        static final int INIT = 5;

        setLayout(new BoxLayout (this, BoxLayout.PAGE_AXIS));

        JLabel sliderLabel = new JLabel ("Choose the majority value", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font ("Times New Roman", Font.ITALIC,20);
        slider.setFont(font);
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton test= new JButton ("Run Test");
        test.addActionListener(new ActionListener()){
            @Override
            public void actionPerformed (Action event){
                int value = slider.getValue();
                sliderLabel.setText ("Value is:" + ((value*2)-1));
            }
            public void stateChanged (ChangedEvent e){
                JSlider source = (JSlider)e.getSource();
                int value = source.getValue();
                sliderLabel.setText("Value is:" + ((value*2)-1));

            }
        }
        add(sliderLabel);
        add(slider);
        add(button);
    }

        
    /**
     * Constructor method
     */
    public Graph(List<DataPoint> testData, List<DataPoint> trainData) {
        this.data = testData;
        model = new KNNModel(4);
        model.train((ArrayList<DataPoint>) trainData);
        this.setLayout(new FlowLayout());
        this.add(accLabel);
        this.add(preLabel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double minF1 = getMinF1Data();
        double maxF1 = getMaxF1Data();
        double minF2 = getMinF2Data();
        double maxF2 = getMaxF2Data();

        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - 
                labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLUE);

        double yGridRatio = (maxF2 - minF2) / numYGridLines;
        for (int i = 0; i < numYGridLines + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 -
                    labelPadding)) / numYGridLines + padding + labelPadding);
            int y1 = y0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = String.format("%.2f", (minF2 + (i * yGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 6, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        double xGridRatio = (maxF1 - minF1) / numXGridLines;
        for (int i = 0; i < numXGridLines + 1; i++) {
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 - pointWidth;
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / (numXGridLines) + padding + labelPadding;
            int x1 = x0;
            if (data.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                g2.setColor(Color.BLACK);
                String xLabel = String.format("%.2f", (minF1 + (i * xGridRatio)));
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // Draw the main axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() -
                padding, getHeight() - padding - labelPadding);

        // Draw the points
        paintPoints(g2, minF1, maxF1, minF2, maxF2);
    }

    private void paintPoints(Graphics2D g2, double minF1, double maxF1, double minF2, double maxF2) {
        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        double xScale = ((double) getWidth() - (3 * padding) - labelPadding) /(maxF1 - minF1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxF2 - minF2);
        g2.setStroke(oldStroke);
        for (int i = 0; i < data.size(); i++) {
            int x1 = (int) ((data.get(i).getF1() - minF1) * xScale + padding + labelPadding);
            int y1 = (int) ((maxF2 - data.get(i).getF2()) * yScale + padding);
            int x = x1 - pointWidth / 2;
            int y = y1 - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;

            DataPoint datapoint = data.get(i);
            String result = model.test(datapoint);
            if (result.equals("1") && datapoint.getLabel().equals("1")) {
                g2.setColor(tpColor);
                truePositive++;
            }
            if (result.equals("1") && datapoint.getLabel().equals("0")) {
                g2.setColor(fpColor);
                falsePositive ++; 
            }
            if (result.equals("0") && datapoint.getLabel().equals("1")) {
                g2.setColor(tnColor);
                falseNegative++;
            }
            if (result.equals("0") && datapoint.getLabel().equals("0")) {
                g2.setColor(fnColor);
                trueNegative++;
            }
            g2.fillOval(x, y, ovalW, ovalH);
        }
        Double accuracy = (truePositive + trueNegative) / 
            (truePositive + trueNegative + falsePositive + falseNegative);
        Double precision =  truePositive / (truePositive + falseNegative);
        accLabel.setText("Accuracy:" + accuracy);
        preLabel.setText("Precision:" + precision);
    }

    /*
     * @Return the min values
     */
    private double getMinF1Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF1());
        }
        return minData;
    }

    private double getMinF2Data() {
        double minData = Double.MAX_VALUE;
        for (DataPoint pt : this.data) {
            minData = Math.min(minData, pt.getF2());
        }
        return minData;
    }


    /*
     * @Return the max values;
     */
    private double getMaxF1Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF1());
        }
        return maxData;
    }

    private double getMaxF2Data() {
        double maxData = Double.MIN_VALUE;
        for (DataPoint pt : this.data) {
            maxData = Math.max(maxData, pt.getF2());
        }
        return maxData;
    }

    /* Mutator */
    public void setData(List<DataPoint> data) {
        this.data = data;
        invalidate();
        this.repaint();
    }

    /* Accessor */
    public List<DataPoint> getData() {
        return data;
    }
    
    
    /*  Run createAndShowGui in the main method, where we create the frame too and pack it in the panel*/
    private static void createAndShowGui(List<DataPoint> testData, List<DataPoint> trainData) {

        /* Main panel */
        Graph mainPanel = new Graph(testData, trainData);

        // Feel free to change the size of the panel
        mainPanel.setPreferredSize(new Dimension(500, 300));

        /* creating the frame */
        JFrame frame = new JFrame("CS 112 Lab Part 3");
        mainPanel.setPreferredSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
      
    /* The main method runs createAndShowGui*/
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            List<DataPoint> testData = new ArrayList<>();
            List<DataPoint> trainData = new ArrayList<>();
            Random random = new Random();
            try (Scanner scanner = new Scanner(new File("titanic.csv"))) {
                while (scanner.hasNextLine()) {
                    List<String> records = getRecordFromLine(scanner.nextLine());
                    String label = records.get(records.size() - 6);
                    Double fare;
                    Double age;
                    try {
                        fare = Double.valueOf(records.get(records.size() - 1));
                        age = Double.valueOf(records.get(records.size() - 2));
                    }
                    catch(NumberFormatException e) {
                        continue;
                    }       
                    // 90% of the data is reserved for training
                    if (random.nextDouble() < 0.9) {
                        DataPoint dp = new DataPoint(age, fare, label, "train");
                        trainData.add(dp);
                    } else {
                        DataPoint dp = new DataPoint(age, fare, label, "test");
                        testData.add(dp);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                return;
            }
            createAndShowGui(testData, trainData);
         }
      });
    }
}
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE)
        double xScale = (double) getWidth() - (3 * padding) - labelPadding) /(maxF1 - minF1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (maxF2 - minF2);
        g2.setStroke(oldStroke);
        for (int i = 0; i < data.size(); i++) {
            int x1 = (int) ((data.get(i).getF1() - minF1) * xScale + padding + labelPadding);
            int y1 = (int) ((maxF2 - data.get(i).getF2()) * yScale + padding);
            int x = x1 - pointWidth / 2;
            int y = y1 - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH =pointWidth;

            DataPoint datapoint = data.get(i);
            String result = KNNModel.test(datapoint);
            
            if (result.equals("1") && datapoint.getLabel().equals("0")) {
            	poiateAndShowGui(List<DataPoint> testData, List<DataPoint> trainD
