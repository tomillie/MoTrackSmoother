package cz.muni.fi.motracksmoother.gui;

import cz.muni.fi.motracksmoother.misc.Misc;
import com.sun.j3d.utils.universe.SimpleUniverse;
import cz.muni.fi.motracksmoother.misc.JointType;
import cz.muni.fi.motracksmoother.Skeleton;
import cz.muni.fi.motracksmoother.SkeletonManagerImpl;
import cz.muni.fi.motracksmoother.SmootherManagerImpl;
import cz.muni.fi.motracksmoother.misc.InvalidFileSyntaxException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static javax.swing.ScrollPaneConstants.*;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.PlainDocument;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Main window of application.
 * 
 * @author Tomas Smetanka
 * @version 1.1
 * @since 1.0
 */
public class MoTrackSmoother extends javax.swing.JFrame {

    private Skeleton mainSkeleton = new Skeleton();
    // class variables
    private Misc misc = new Misc();
    private SmootherManagerImpl smootherManager = new SmootherManagerImpl();
    private SkeletonManagerImpl skeletonManager = new SkeletonManagerImpl();
    // misc
    private boolean validFile = true;
    JFileChooser chooser = new JFileChooser();
    // motion player variables
    private int numberOfFrames = 0;
    private int currentFrame = 0;
    private Integer lastFrame = null;
    private int startFrameToCut = 0;
    private int endFrameToCut = 0;
    private int endFrameToPlay = 0;
    private boolean isFiltered = false;
    private boolean isPlaying = false;
    private boolean wasPlayedOriginal = true;
    // java3d variables
    private Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    private SimpleUniverse universe = new SimpleUniverse(canvas);
    private ArrayList<BranchGroup> skeletonsInAllFrames = new ArrayList<BranchGroup>();
    private ArrayList<BranchGroup> skeletonsInAllFramesCleaned = new ArrayList<BranchGroup>();
    // basic renderer used in charts
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    // datasets for all charts
    private XYSeriesCollection datasetHead = new XYSeriesCollection();
    private XYSeriesCollection datasetShoulderCenter = new XYSeriesCollection();
    private XYSeriesCollection datasetShoulderRight = new XYSeriesCollection();
    private XYSeriesCollection datasetShoulderLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetElbowRight = new XYSeriesCollection();
    private XYSeriesCollection datasetElbowLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetWristRight = new XYSeriesCollection();
    private XYSeriesCollection datasetWristLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetHandRight = new XYSeriesCollection();
    private XYSeriesCollection datasetHandLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetSpine = new XYSeriesCollection();
    private XYSeriesCollection datasetHipCenter = new XYSeriesCollection();
    private XYSeriesCollection datasetHipRight = new XYSeriesCollection();
    private XYSeriesCollection datasetHipLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetKneeRight = new XYSeriesCollection();
    private XYSeriesCollection datasetKneeLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetAnkleRight = new XYSeriesCollection();
    private XYSeriesCollection datasetAnkleLeft = new XYSeriesCollection();
    private XYSeriesCollection datasetFootRight = new XYSeriesCollection();
    private XYSeriesCollection datasetFootLeft = new XYSeriesCollection();
    // charts for all joint types
    JFreeChart chartHead = ChartFactory.createXYLineChart("Head joint positions", "", "", datasetHead, PlotOrientation.VERTICAL, true, false, false);
    JFreeChart chartShoulderCenter = ChartFactory.createXYLineChart("Center shoulder joint positions", "", "", datasetShoulderCenter, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartShoulderRight = ChartFactory.createXYLineChart("Right shoulder joint positions", "", "", datasetShoulderRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartShoulderLeft = ChartFactory.createXYLineChart("Left shoulder joint positions", "", "", datasetShoulderLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartElbowRight = ChartFactory.createXYLineChart("Right elbow joint positions", "", "", datasetElbowRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartElbowLeft = ChartFactory.createXYLineChart("Left elbow joint positions", "", "", datasetElbowLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartWristRight = ChartFactory.createXYLineChart("Right wrist joint positions", "", "", datasetWristRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartWristLeft = ChartFactory.createXYLineChart("Left wrist joint positions", "", "", datasetWristLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartHandRight = ChartFactory.createXYLineChart("Right hand joint positions", "", "", datasetHandRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartHandLeft = ChartFactory.createXYLineChart("Left hand joint positions", "", "", datasetHandLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartSpine = ChartFactory.createXYLineChart("Spine joint positions", "", "", datasetSpine, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartHipCenter = ChartFactory.createXYLineChart("Center hip joint positions", "", "", datasetHipCenter, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartHipRight = ChartFactory.createXYLineChart("Right hip joint positions", "", "", datasetHipRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartHipLeft = ChartFactory.createXYLineChart("Left hip joint positions", "", "", datasetHipLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartKneeRight = ChartFactory.createXYLineChart("Right knee joint positions", "", "", datasetKneeRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartKneeLeft = ChartFactory.createXYLineChart("Left knee joint positions", "", "", datasetKneeLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartAnkleRight = ChartFactory.createXYLineChart("Right ankle joint positions", "", "", datasetAnkleRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartAnkleLeft = ChartFactory.createXYLineChart("Left ankle joint positions", "", "", datasetAnkleLeft, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartFootRight = ChartFactory.createXYLineChart("Right foot joint positions", "", "", datasetFootRight, PlotOrientation.VERTICAL, false, false, false);
    JFreeChart chartFootLeft = ChartFactory.createXYLineChart("Left foot joint positions", "", "", datasetFootLeft, PlotOrientation.VERTICAL, false, false, false);
    ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();

    public MoTrackSmoother() {

        initComponents();
        setLocationRelativeTo(null);
        graphsScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        // provides that all tooltips are shown immediately after cursor moves over the component
        ToolTipManager.sharedInstance().setInitialDelay(0);
        // sets tooltip time to dismiss to 1 minute
        ToolTipManager.sharedInstance().setDismissDelay(60000);
        // adds content filter to noiseField
        PlainDocument doc = (PlainDocument) noiseField.getDocument();
        doc.setDocumentFilter(new NoiseFieldFilter());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        graphsScrollPane = new javax.swing.JScrollPane();
        graphsScrollPanel = new javax.swing.JPanel();
        graphHead = new javax.swing.JPanel();
        graphShoulderCenter = new javax.swing.JPanel();
        graphShoulderRight = new javax.swing.JPanel();
        graphShoulderLeft = new javax.swing.JPanel();
        graphElbowRight = new javax.swing.JPanel();
        graphElbowLeft = new javax.swing.JPanel();
        graphWristRight = new javax.swing.JPanel();
        graphWristLeft = new javax.swing.JPanel();
        graphHandRight = new javax.swing.JPanel();
        graphHandLeft = new javax.swing.JPanel();
        graphSpine = new javax.swing.JPanel();
        graphHipCenter = new javax.swing.JPanel();
        graphHipRight = new javax.swing.JPanel();
        graphHipLeft = new javax.swing.JPanel();
        graphKneeRight = new javax.swing.JPanel();
        graphKneeLeft = new javax.swing.JPanel();
        graphAnkleRight = new javax.swing.JPanel();
        graphAnkleLeft = new javax.swing.JPanel();
        graphFootRight = new javax.swing.JPanel();
        graphFootLeft = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        playerPanel = new javax.swing.JPanel();
        playRadioButton1 = new javax.swing.JRadioButton();
        playRadioButton2 = new javax.swing.JRadioButton();
        playButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        playerSlider = new javax.swing.JSlider();
        importButton = new javax.swing.JButton();
        filterButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        hintLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        currentFrameLabel = new javax.swing.JLabel();
        lastFrameLabel = new javax.swing.JLabel();
        setStartButton = new javax.swing.JButton();
        setEndButton = new javax.swing.JButton();
        playRadioButton3 = new javax.swing.JRadioButton();
        startLabel = new javax.swing.JLabel();
        fromToLabel = new javax.swing.JLabel();
        endLabel = new javax.swing.JLabel();
        playRadioButton4 = new javax.swing.JRadioButton();
        noiseLabel = new javax.swing.JLabel();
        noiseField = new javax.swing.JTextField();
        firstFrameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MoTrack Smoother");
        setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("motrack-icon.png")));
        setName("mainFrame"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        graphsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        graphsScrollPane.setEnabled(false);
        graphsScrollPane.setPreferredSize(new java.awt.Dimension(563, 4173));

        graphsScrollPanel.setBackground(new java.awt.Color(255, 255, 255));
        graphsScrollPanel.setEnabled(false);
        graphsScrollPanel.setPreferredSize(new java.awt.Dimension(563, 4177));

        javax.swing.GroupLayout graphHeadLayout = new javax.swing.GroupLayout(graphHead);
        graphHead.setLayout(graphHeadLayout);
        graphHeadLayout.setHorizontalGroup(
            graphHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphHeadLayout.setVerticalGroup(
            graphHeadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphShoulderCenterLayout = new javax.swing.GroupLayout(graphShoulderCenter);
        graphShoulderCenter.setLayout(graphShoulderCenterLayout);
        graphShoulderCenterLayout.setHorizontalGroup(
            graphShoulderCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphShoulderCenterLayout.setVerticalGroup(
            graphShoulderCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphShoulderRightLayout = new javax.swing.GroupLayout(graphShoulderRight);
        graphShoulderRight.setLayout(graphShoulderRightLayout);
        graphShoulderRightLayout.setHorizontalGroup(
            graphShoulderRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 514, Short.MAX_VALUE)
        );
        graphShoulderRightLayout.setVerticalGroup(
            graphShoulderRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 206, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphShoulderLeftLayout = new javax.swing.GroupLayout(graphShoulderLeft);
        graphShoulderLeft.setLayout(graphShoulderLeftLayout);
        graphShoulderLeftLayout.setHorizontalGroup(
            graphShoulderLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphShoulderLeftLayout.setVerticalGroup(
            graphShoulderLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphElbowRightLayout = new javax.swing.GroupLayout(graphElbowRight);
        graphElbowRight.setLayout(graphElbowRightLayout);
        graphElbowRightLayout.setHorizontalGroup(
            graphElbowRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphElbowRightLayout.setVerticalGroup(
            graphElbowRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphElbowLeftLayout = new javax.swing.GroupLayout(graphElbowLeft);
        graphElbowLeft.setLayout(graphElbowLeftLayout);
        graphElbowLeftLayout.setHorizontalGroup(
            graphElbowLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphElbowLeftLayout.setVerticalGroup(
            graphElbowLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphWristRightLayout = new javax.swing.GroupLayout(graphWristRight);
        graphWristRight.setLayout(graphWristRightLayout);
        graphWristRightLayout.setHorizontalGroup(
            graphWristRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphWristRightLayout.setVerticalGroup(
            graphWristRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphWristLeftLayout = new javax.swing.GroupLayout(graphWristLeft);
        graphWristLeft.setLayout(graphWristLeftLayout);
        graphWristLeftLayout.setHorizontalGroup(
            graphWristLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphWristLeftLayout.setVerticalGroup(
            graphWristLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphHandRightLayout = new javax.swing.GroupLayout(graphHandRight);
        graphHandRight.setLayout(graphHandRightLayout);
        graphHandRightLayout.setHorizontalGroup(
            graphHandRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphHandRightLayout.setVerticalGroup(
            graphHandRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphHandLeftLayout = new javax.swing.GroupLayout(graphHandLeft);
        graphHandLeft.setLayout(graphHandLeftLayout);
        graphHandLeftLayout.setHorizontalGroup(
            graphHandLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphHandLeftLayout.setVerticalGroup(
            graphHandLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphSpineLayout = new javax.swing.GroupLayout(graphSpine);
        graphSpine.setLayout(graphSpineLayout);
        graphSpineLayout.setHorizontalGroup(
            graphSpineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphSpineLayout.setVerticalGroup(
            graphSpineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphHipCenterLayout = new javax.swing.GroupLayout(graphHipCenter);
        graphHipCenter.setLayout(graphHipCenterLayout);
        graphHipCenterLayout.setHorizontalGroup(
            graphHipCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphHipCenterLayout.setVerticalGroup(
            graphHipCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphHipRightLayout = new javax.swing.GroupLayout(graphHipRight);
        graphHipRight.setLayout(graphHipRightLayout);
        graphHipRightLayout.setHorizontalGroup(
            graphHipRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphHipRightLayout.setVerticalGroup(
            graphHipRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphHipLeftLayout = new javax.swing.GroupLayout(graphHipLeft);
        graphHipLeft.setLayout(graphHipLeftLayout);
        graphHipLeftLayout.setHorizontalGroup(
            graphHipLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphHipLeftLayout.setVerticalGroup(
            graphHipLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphKneeRightLayout = new javax.swing.GroupLayout(graphKneeRight);
        graphKneeRight.setLayout(graphKneeRightLayout);
        graphKneeRightLayout.setHorizontalGroup(
            graphKneeRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphKneeRightLayout.setVerticalGroup(
            graphKneeRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphKneeLeftLayout = new javax.swing.GroupLayout(graphKneeLeft);
        graphKneeLeft.setLayout(graphKneeLeftLayout);
        graphKneeLeftLayout.setHorizontalGroup(
            graphKneeLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphKneeLeftLayout.setVerticalGroup(
            graphKneeLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphAnkleRightLayout = new javax.swing.GroupLayout(graphAnkleRight);
        graphAnkleRight.setLayout(graphAnkleRightLayout);
        graphAnkleRightLayout.setHorizontalGroup(
            graphAnkleRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphAnkleRightLayout.setVerticalGroup(
            graphAnkleRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphAnkleLeftLayout = new javax.swing.GroupLayout(graphAnkleLeft);
        graphAnkleLeft.setLayout(graphAnkleLeftLayout);
        graphAnkleLeftLayout.setHorizontalGroup(
            graphAnkleLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 514, Short.MAX_VALUE)
        );
        graphAnkleLeftLayout.setVerticalGroup(
            graphAnkleLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphFootRightLayout = new javax.swing.GroupLayout(graphFootRight);
        graphFootRight.setLayout(graphFootRightLayout);
        graphFootRightLayout.setHorizontalGroup(
            graphFootRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphFootRightLayout.setVerticalGroup(
            graphFootRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphFootLeftLayout = new javax.swing.GroupLayout(graphFootLeft);
        graphFootLeft.setLayout(graphFootLeftLayout);
        graphFootLeftLayout.setHorizontalGroup(
            graphFootLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graphFootLeftLayout.setVerticalGroup(
            graphFootLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout graphsScrollPanelLayout = new javax.swing.GroupLayout(graphsScrollPanel);
        graphsScrollPanel.setLayout(graphsScrollPanelLayout);
        graphsScrollPanelLayout.setHorizontalGroup(
            graphsScrollPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphsScrollPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(graphsScrollPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(graphAnkleLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphAnkleRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphKneeLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphKneeRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphHipLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphHipRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphHipCenter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphSpine, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphHandLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphHandRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphWristLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphWristRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphElbowLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphElbowRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphShoulderLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphShoulderRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphHead, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphFootRight, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphShoulderCenter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphFootLeft, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        graphsScrollPanelLayout.setVerticalGroup(
            graphsScrollPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphsScrollPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(graphHead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphShoulderCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphShoulderRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphShoulderLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphElbowRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphElbowLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphWristRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphWristLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphHandRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphHandLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphSpine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphHipCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphHipRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphHipLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphKneeRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphKneeLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphAnkleRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphAnkleLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphFootRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphFootLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        graphHead.getAccessibleContext().setAccessibleParent(graphsScrollPane);

        graphsScrollPane.setViewportView(graphsScrollPanel);

        jLabel1.setText("Export & Play:");

        playerPanel.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout playerPanelLayout = new javax.swing.GroupLayout(playerPanel);
        playerPanel.setLayout(playerPanelLayout);
        playerPanelLayout.setHorizontalGroup(
            playerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        playerPanelLayout.setVerticalGroup(
            playerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
        );

        buttonGroup1.add(playRadioButton1);
        playRadioButton1.setSelected(true);
        playRadioButton1.setText("Original motion");
        playRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playRadioButton1StateChanged(evt);
            }
        });

        buttonGroup1.add(playRadioButton2);
        playRadioButton2.setText("Filtered motion");
        playRadioButton2.setEnabled(false);
        playRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playRadioButton2StateChanged(evt);
            }
        });

        playButton.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        playButton.setText("►");
        playButton.setEnabled(false);
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playButtonMouseClicked(evt);
            }
        });

        stopButton.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        stopButton.setText("■");
        stopButton.setEnabled(false);
        stopButton.setMaximumSize(new java.awt.Dimension(49, 33));
        stopButton.setMinimumSize(new java.awt.Dimension(49, 33));
        stopButton.setPreferredSize(new java.awt.Dimension(49, 33));
        stopButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stopButtonMouseClicked(evt);
            }
        });

        playerSlider.setValue(0);
        playerSlider.setEnabled(false);
        playerSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                playerSliderMouseReleased(evt);
            }
        });
        playerSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playerSliderStateChanged(evt);
            }
        });

        importButton.setText("IMPORT");
        importButton.setToolTipText("Import file with joints positions");
        importButton.setName(""); // NOI18N
        importButton.setPreferredSize(new java.awt.Dimension(110, 35));
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importHandler(evt);
            }
        });

        filterButton.setText("FILTER");
        filterButton.setToolTipText("Suppress estimation errors and acquire smooth motion capture data");
        filterButton.setEnabled(false);
        filterButton.setPreferredSize(new java.awt.Dimension(110, 35));
        filterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                filterButtonMouseClicked(evt);
            }
        });

        exportButton.setText("EXPORT");
        exportButton.setToolTipText("Export file with suppressed motion capture data");
        exportButton.setEnabled(false);
        exportButton.setPreferredSize(new java.awt.Dimension(110, 35));
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportHandler(evt);
            }
        });

        hintLabel.setText("Choose and import file to process");

        progressBar.setIndeterminate(true);

        currentFrameLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        currentFrameLabel.setText(String.valueOf(currentFrame));

        lastFrameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lastFrameLabel.setText(String.valueOf(numberOfFrames));

        setStartButton.setText("SET START");
        setStartButton.setEnabled(false);
        setStartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setStartButtonMouseClicked(evt);
            }
        });

        setEndButton.setText("SET END");
        setEndButton.setEnabled(false);
        setEndButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setEndButtonMouseClicked(evt);
            }
        });

        buttonGroup1.add(playRadioButton3);
        playRadioButton3.setText("Selected submotion");
        playRadioButton3.setEnabled(false);
        playRadioButton3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playRadioButton3StateChanged(evt);
            }
        });

        startLabel.setText(String.valueOf(startFrameToCut));
        startLabel.setEnabled(false);

        fromToLabel.setText("-");
        fromToLabel.setEnabled(false);

        endLabel.setText(String.valueOf(endFrameToCut));
        endLabel.setEnabled(false);

        buttonGroup1.add(playRadioButton4);
        playRadioButton4.setText("Filtered submotion");
        playRadioButton4.setEnabled(false);
        playRadioButton4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                playRadioButton4StateChanged(evt);
            }
        });

        noiseLabel.setText("Measurement noise: ");
        noiseLabel.setToolTipText("Number between 0 and 1, representing noise or discrepancy captured during motion recording. The value is used as measurement noise covariance and is assumed as a constant.");
        noiseLabel.setEnabled(false);

        noiseField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        noiseField.setText("0.001");
        noiseField.setEnabled(false);
        noiseField.setPreferredSize(new java.awt.Dimension(59, 35));

        firstFrameLabel.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(graphsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(noiseLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(noiseField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(filterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(hintLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(exportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(playRadioButton2)
                                    .addComponent(playRadioButton1)
                                    .addComponent(playRadioButton3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(playButton)
                                .addGap(18, 18, 18)
                                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(playerSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(firstFrameLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lastFrameLabel))
                                    .addComponent(currentFrameLabel)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(setStartButton)
                                .addGap(18, 18, 18)
                                .addComponent(setEndButton)
                                .addGap(18, 18, 18)
                                .addComponent(startLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fromToLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(endLabel))))
                    .addComponent(playerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(graphsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(playerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(noiseField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(filterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(noiseLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hintLabel)
                            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(playButton))
                                .addGap(36, 36, 36))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(playRadioButton1)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playRadioButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playRadioButton3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(firstFrameLabel)
                                    .addComponent(lastFrameLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(currentFrameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(setEndButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(startLabel)
                                .addComponent(fromToLabel)
                                .addComponent(endLabel))
                            .addComponent(setStartButton)
                            .addComponent(playRadioButton4))
                        .addGap(23, 23, 23))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handles all actions related to file import.
     * 
     * @param evt 
     * @version 1.0
     * @since 1.0
     */
    private void importHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importHandler

        int code = chooser.showOpenDialog(null);

        // checks if some file was choosed
        if (code == JFileChooser.APPROVE_OPTION) {
            final File importedFile = chooser.getSelectedFile();

            String filename = importedFile.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            extension = extension.toLowerCase();

            // checks if the extension of the file is correct
            if (extension.equals("json") || extension.equals("xml") || extension.equals("csv")) {

                graphsScrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
                progressBar.setVisible(true);
                hintLabel.setText("Parsing . . .");

                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        
                        // cleares all attributes
                        misc.clearAttributes(mainSkeleton);

                        // parsing from file
                        try {
                            skeletonManager.getPositionsFromFile(mainSkeleton, importedFile);
                            validFile = true;
                        } catch (InvalidFileSyntaxException ex) {
                            validFile = false;
                            return null;
                        }

                        // gets number of frames
                        numberOfFrames = mainSkeleton.getxPositions().get(JointType.HEAD).size();

                        // removes all current XYSeries in datasets
                        removeAllSeriesFromDatasets();

                        hintLabel.setText("Drawing . . .");

                        // START OF CREATING CHARTS
                        // gets X/Y/Z-axis 
                        getAxis(mainSkeleton.getxPositions(), "X");
                        getAxis(mainSkeleton.getyPositions(), "Y");
                        getAxis(mainSkeleton.getzPositions(), "Z");

                        // Add all existing charts into ArrayList to loop easier
                        addAllChartsIntoArray(charts);

                        // sets paint color for each series
                        setColoursForAxes();

                        // removes dots in chart
                        renderer.setBaseShapesVisible(false);

                        for (JFreeChart chart : charts) {

                            XYPlot plot = chart.getXYPlot();
                            plot.setRenderer(renderer);
                            plot.setBackgroundPaint(Color.WHITE);
                            JPanel jPanelTemp = new JPanel();

                            // inserts chart into correct JPanel
                            if (chart == chartHead) {
                                jPanelTemp = graphHead;
                            } else if (chart == chartShoulderCenter) {
                                jPanelTemp = graphShoulderCenter;
                            } else if (chart == chartShoulderRight) {
                                jPanelTemp = graphShoulderRight;
                            } else if (chart == chartShoulderLeft) {
                                jPanelTemp = graphShoulderLeft;
                            } else if (chart == chartElbowRight) {
                                jPanelTemp = graphElbowRight;
                            } else if (chart == chartElbowLeft) {
                                jPanelTemp = graphElbowLeft;
                            } else if (chart == chartWristRight) {
                                jPanelTemp = graphWristRight;
                            } else if (chart == chartWristLeft) {
                                jPanelTemp = graphWristLeft;
                            } else if (chart == chartHandRight) {
                                jPanelTemp = graphHandRight;
                            } else if (chart == chartHandLeft) {
                                jPanelTemp = graphHandLeft;
                            } else if (chart == chartSpine) {
                                jPanelTemp = graphSpine;
                            } else if (chart == chartHipCenter) {
                                jPanelTemp = graphHipCenter;
                            } else if (chart == chartHipRight) {
                                jPanelTemp = graphHipRight;
                            } else if (chart == chartHipLeft) {
                                jPanelTemp = graphHipLeft;
                            } else if (chart == chartKneeRight) {
                                jPanelTemp = graphKneeRight;
                            } else if (chart == chartKneeLeft) {
                                jPanelTemp = graphKneeLeft;
                            } else if (chart == chartAnkleRight) {
                                jPanelTemp = graphAnkleRight;
                            } else if (chart == chartAnkleLeft) {
                                jPanelTemp = graphAnkleLeft;
                            } else if (chart == chartFootRight) {
                                jPanelTemp = graphFootRight;
                            } else if (chart == chartFootLeft) {
                                jPanelTemp = graphFootLeft;
                            }

                            jPanelTemp.setLayout(new java.awt.BorderLayout());
                            jPanelTemp.add(new ChartPanel(chart, 513, 200, 0, 0, 513, 200, true, true, true, true, true, true), BorderLayout.CENTER);
                            jPanelTemp.validate();

                        }
                        // END OF CREATING CHARTS

                        // creates skeleton in all frames
                        createSkeletonInAllFrames(false);

                        // draw skeleton in first frame
                        drawSkeleton();

                        return null;
                    }

                    @Override
                    protected void done() {
                        if (validFile) {
                            isFiltered = false;
                            setColoursForAxes();

                            playRadioButton2.setEnabled(false);
                            playRadioButton3.setEnabled(false);
                            playRadioButton4.setEnabled(false);
                            playRadioButton1.setSelected(true);

                            progressBar.setVisible(false);
                            playButton.setEnabled(true);
                            playerSlider.setEnabled(true);
                            playerSlider.setMinimum(0);
                            playerSlider.setMaximum(numberOfFrames);
                            lastFrameLabel.setText(String.valueOf(numberOfFrames));
                            currentFrame = 0;
                            setStartButton.setEnabled(true);
                            setEndButton.setEnabled(true);
                            filterButton.setEnabled(true);
                            startFrameToCut = 0;
                            endFrameToCut = 0;
                            endFrameToPlay = numberOfFrames;
                            startLabel.setText("0");
                            endLabel.setText("0");
                            firstFrameLabel.setText("0");
                            lastFrameLabel.setText(String.valueOf(numberOfFrames));

                            noiseLabel.setEnabled(true);
                            noiseField.setEnabled(true);

                            exportButton.setEnabled(true);

                            hintLabel.setText("File " + mainSkeleton.getName() + " has been successfully imported");
                        } else {
                            progressBar.setVisible(false);

                            hintLabel.setForeground(new Color(182, 17, 17));
                            hintLabel.setText("The imported file has invalid structure");
                            hintLabel.setForeground(Color.BLACK);
                        }
                    }
                };
                swingWorker.execute();
            } else {
                hintLabel.setForeground(new Color(182, 17, 17));
                hintLabel.setText("The imported file must be one of these types: .JSON, .XML, .CSV");
                hintLabel.setForeground(Color.BLACK);
            }

        }

    }//GEN-LAST:event_importHandler

    /**
     * Handles all actions related to motion player.
     * 
     * @param evt 
     * @version 1.0
     * @since 1.0
     */
    private void playButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playButtonMouseClicked

        if (playButton.isEnabled()) {

            if (isPlaying) {
                stopPlayer();
            } else {
                playPlayer();
            }

            SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    while (currentFrame < endFrameToPlay) {

                        if (isPlaying) {
                            currentFrame++;
                            playerSlider.setValue(currentFrame);
                            drawSkeleton();
                        } else {
                            break;
                        }

                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MoTrackSmoother.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                    return null;
                }

                protected void done() {
                    stopPlayer();
                }
            };
            swingWorker.execute();

        }

    }//GEN-LAST:event_playButtonMouseClicked

    private void stopPlayer() {

        playButton.setFont(new java.awt.Font("SansSerif", 0, 18));
        playButton.setText("►");
        isPlaying = false;
        importButton.setEnabled(true);
        exportButton.setEnabled(true);
        filterButton.setEnabled(true);
        noiseLabel.setEnabled(true);
        noiseField.setEnabled(true);

        if (currentFrame == endFrameToPlay) {

            // checks if submotion is selected and sets correct start frame
            if (playRadioButton3.isSelected() || playRadioButton4.isSelected()) {
                currentFrame = startFrameToCut;
                playerSlider.setValue(startFrameToCut);
            } else {
                currentFrame = 0;
                playerSlider.setValue(0);
            }

            drawSkeleton();
        }

        if (currentFrame == 0) {
            stopButton.setEnabled(false);
        }

    }

    private void playPlayer() {

        playButton.setFont(new java.awt.Font("SansSerif", 0, 17));
        playButton.setText(" ll ");
        playButton.setSize(new java.awt.Dimension(49, 33));
        stopButton.setEnabled(true);
        importButton.setEnabled(false);
        exportButton.setEnabled(false);
        filterButton.setEnabled(false);
        noiseLabel.setEnabled(false);
        noiseField.setEnabled(false);
        isPlaying = true;

    }

    private void playerSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playerSliderStateChanged

        currentFrameLabel.setText(String.valueOf(playerSlider.getValue()));

    }//GEN-LAST:event_playerSliderStateChanged

    private void stopButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stopButtonMouseClicked

        if (stopButton.isEnabled()) {
            isPlaying = false;
            if (playRadioButton1.isSelected() || playRadioButton2.isSelected()) {
                currentFrame = 0;
                playerSlider.setValue(0);
            } else {
                currentFrame = startFrameToCut;
                playerSlider.setValue(startFrameToCut);
            }
            stopButton.setEnabled(false);
            drawSkeleton();
            importButton.setEnabled(true);
            exportButton.setEnabled(true);
            filterButton.setEnabled(true);
            noiseLabel.setEnabled(true);
            noiseField.setEnabled(true);
        }

    }//GEN-LAST:event_stopButtonMouseClicked

    /**
     * Repaints skeleton in motion player if window is minimized.
     * 
     * @param evt 
     * @version 1.0
     * @since 1.0
     */
    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        canvas.repaint();
    }//GEN-LAST:event_formWindowActivated

    /**
     * Repaints skeleton in motion player if window is dragged out of the screen.
     * 
     * @param evt 
     * @version 1.0
     * @since 1.0
     */
    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        canvas.repaint();
    }//GEN-LAST:event_formWindowStateChanged

    private void playerSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerSliderMouseReleased

        if (playerSlider.isEnabled()) {
            currentFrame = playerSlider.getValue();
            drawSkeleton();
        }

    }//GEN-LAST:event_playerSliderMouseReleased

    private void setStartButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setStartButtonMouseClicked

        startFrameToCut = currentFrame;
        startLabel.setText(String.valueOf(currentFrame));
        if (startFrameToCut < endFrameToCut) {
            startLabel.setEnabled(true);
            fromToLabel.setEnabled(true);
            endLabel.setEnabled(true);
            playRadioButton3.setEnabled(true);
            if (isFiltered) {
                playRadioButton4.setEnabled(true);
            }
        } else {
            startLabel.setEnabled(false);
            fromToLabel.setEnabled(false);
            endLabel.setEnabled(false);
            playRadioButton3.setEnabled(false);
            playRadioButton4.setEnabled(false);
        }

    }//GEN-LAST:event_setStartButtonMouseClicked

    private void setEndButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_setEndButtonMouseClicked

        endFrameToCut = currentFrame;
        endLabel.setText(String.valueOf(currentFrame));
        if (startFrameToCut < endFrameToCut) {
            startLabel.setEnabled(true);
            fromToLabel.setEnabled(true);
            endLabel.setEnabled(true);
            playRadioButton3.setEnabled(true);
            if (isFiltered) {
                playRadioButton4.setEnabled(true);
            }
        } else {
            startLabel.setEnabled(false);
            fromToLabel.setEnabled(false);
            endLabel.setEnabled(false);
            playRadioButton3.setEnabled(false);
            playRadioButton4.setEnabled(false);
        }

    }//GEN-LAST:event_setEndButtonMouseClicked

    /**
     * Handles all actions related to filtering.
     * 
     * @param evt 
     * @version 1.0
     * @since 1.0
     */
    private void filterButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filterButtonMouseClicked

        if (filterButton.isEnabled()) {
            progressBar.setVisible(true);
            hintLabel.setText("Filtering . . .");

            if (isFiltered) {
                mainSkeleton.getxPositionsCleaned().clear();
                mainSkeleton.getyPositionsCleaned().clear();
                mainSkeleton.getzPositionsCleaned().clear();

                removeCleanedSeriesFromDatasets();
            }

            SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    double measurementNoiseCovariance = Double.parseDouble(noiseField.getText());
                    smootherManager.kalman(mainSkeleton, measurementNoiseCovariance);

                    createSkeletonInAllFrames(true);
                    drawSkeleton();

                    return null;
                }

                protected void done() {

                    isFiltered = true;
                    setColoursForAxes();

                    getAxis(mainSkeleton.getxPositionsCleaned(), "X filtered");
                    getAxis(mainSkeleton.getyPositionsCleaned(), "Y filtered");
                    getAxis(mainSkeleton.getzPositionsCleaned(), "Z filtered");

                    playRadioButton2.setEnabled(true);
                    if (playRadioButton3.isEnabled()) {
                        playRadioButton4.setEnabled(true);
                    }
                    exportButton.setEnabled(true);
                    progressBar.setVisible(false);
                    hintLabel.setText("All axes were successfully filtered using Kalman filtration.");
                }
            };
            swingWorker.execute();
        }

    }//GEN-LAST:event_filterButtonMouseClicked

    /**
     * Handles all actions related to file export.
     * 
     * @param evt 
     * @version 1.0
     * @since 1.0
     */
    private void exportHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportHandler

        chooser.setAcceptAllFileFilterUsed(false);
        String[] extensions = new String[]{".json", ".xml", ".csv"};
        FileFilter acceptedTypes = (FileFilter) new ExtensionFilter("JSON & XML & CSV", extensions);
        chooser.addChoosableFileFilter(acceptedTypes);
        chooser.setFileFilter(acceptedTypes);
        int code = chooser.showSaveDialog(null);

        if (code == JFileChooser.APPROVE_OPTION) {
            final File fileToSave = chooser.getSelectedFile();

            String filename = fileToSave.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            extension = extension.toLowerCase();

            // checks if the extension of the file is correct
            if (extension.equals("json") || extension.equals("xml") || extension.equals("csv")) {

                progressBar.setVisible(true);

                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {

                        hintLabel.setText("Creating a file . . .");

                        // writing to the file
                        if (playRadioButton1.isSelected()) {
                            skeletonManager.createOutput(mainSkeleton, fileToSave, false, 0, mainSkeleton.getFrames());
                        } else if (playRadioButton2.isSelected()) {
                            skeletonManager.createOutput(mainSkeleton, fileToSave, true, 0, mainSkeleton.getFrames());
                        } else if (playRadioButton3.isSelected()) {
                            skeletonManager.createOutput(mainSkeleton, fileToSave, false, startFrameToCut, endFrameToCut);
                        } else if (playRadioButton4.isSelected()) {
                            skeletonManager.createOutput(mainSkeleton, fileToSave, true, startFrameToCut, endFrameToCut);
                        }

                        return null;
                    }

                    @Override
                    protected void done() {
                        progressBar.setVisible(false);
                        hintLabel.setText("File " + fileToSave.getName() + " has been successfully created and exported");
                    }
                };
                swingWorker.execute();

            } else {
                hintLabel.setForeground(new Color(182, 17, 17));
                hintLabel.setText("The exported file must be one of these types: .JSON, .XML, .CSV");
                hintLabel.setForeground(Color.BLACK);
            }

        }

    }//GEN-LAST:event_exportHandler

    private void playRadioButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playRadioButton1StateChanged

        if (playRadioButton1.isSelected()) {
            playTypeChange(true, true);
        }

    }//GEN-LAST:event_playRadioButton1StateChanged

    private void playRadioButton2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playRadioButton2StateChanged

        if (playRadioButton2.isSelected()) {
            playTypeChange(false, true);
        }

    }//GEN-LAST:event_playRadioButton2StateChanged

    private void playRadioButton3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playRadioButton3StateChanged

        if (playRadioButton3.isSelected()) {
            playTypeChange(true, false);
        }

    }//GEN-LAST:event_playRadioButton3StateChanged

    private void playRadioButton4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playRadioButton4StateChanged

        if (playRadioButton4.isSelected()) {
            playTypeChange(false, false);
        }

    }//GEN-LAST:event_playRadioButton4StateChanged

    /**
     * Sets label values and radio button states related to motion player.
     * 
     * @param original is true if no filtered motion is selected
     * @param uncut is true if no submotion is selected
     * @version 1.0
     * @since 1.0
     */
    private void playTypeChange(boolean original, boolean uncut) {

        if (original && uncut) {

            playerSlider.setMinimum(0);
            playerSlider.setMaximum(numberOfFrames);
            endFrameToPlay = numberOfFrames;
            firstFrameLabel.setText("0");
            lastFrameLabel.setText(String.valueOf(numberOfFrames));

        } else if (!original && uncut) {

            playerSlider.setMinimum(0);
            playerSlider.setMaximum(numberOfFrames);
            endFrameToPlay = numberOfFrames;
            firstFrameLabel.setText("0");
            lastFrameLabel.setText(String.valueOf(numberOfFrames));

        } else if (original && !uncut) {

            playerSlider.setMinimum(startFrameToCut);
            playerSlider.setMaximum(endFrameToCut);
            playerSlider.setValue(startFrameToCut);
            currentFrame = startFrameToCut;
            endFrameToPlay = endFrameToCut;
            firstFrameLabel.setText(String.valueOf(startFrameToCut));
            lastFrameLabel.setText(String.valueOf(endFrameToCut));

        } else if (!original && !uncut) {

            playerSlider.setMinimum(startFrameToCut);
            playerSlider.setMaximum(endFrameToCut);
            playerSlider.setValue(startFrameToCut);
            currentFrame = startFrameToCut;
            endFrameToPlay = endFrameToCut;
            firstFrameLabel.setText(String.valueOf(startFrameToCut));
            lastFrameLabel.setText(String.valueOf(endFrameToCut));

        }

    }

    /**
     * Draws specified skeleton from skeletonInAllFrames ArrayList to motion player.
     * 
     * @version 1.0
     * @since 1.0
     */
    private void drawSkeleton() {

        // removes last drawn skeleton
        if (lastFrame != null) {
            if (wasPlayedOriginal) {
                universe.getLocale().removeBranchGraph(skeletonsInAllFrames.get(lastFrame));
            } else {
                universe.getLocale().removeBranchGraph(skeletonsInAllFramesCleaned.get(lastFrame));
            }
        }

        // avoids blinking
        if (lastFrame == null) {
            playerPanel.setLayout(new java.awt.BorderLayout());
            playerPanel.add(canvas);
            playerPanel.validate();
        }

        universe.getViewingPlatform().setNominalViewingTransform();
        // sets camera position
        Transform3D move = new Transform3D();
        move.setTranslation(new Vector3f(0.0f, 0.0f, 7.0f));
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(move);

        if (playRadioButton1.isSelected() || playRadioButton3.isSelected()) {
            wasPlayedOriginal = true;
            universe.addBranchGraph(skeletonsInAllFrames.get(currentFrame));
        } else {
            wasPlayedOriginal = false;
            universe.addBranchGraph(skeletonsInAllFramesCleaned.get(currentFrame));
        }

        lastFrame = currentFrame;

    }

    /**
     * Creates skeleton in every frame to play it as a motion.
     * 
     * Lines (pairs of points) to be created.
     * Joints have numbers allocated according to JoinType enumeration.
     * 
     * o_o       o       o_o        8 (right hand), 6 (right wrist), 0 (head), 7 (left wrist), 9 (left hand)
     *    \      |      / 
     *     o     |     o            4 (right elbow), 5 (left elbow)
     *      \    |    /
     *       o___o___o              2 (right shoulder), 1 (center shoulder), 3 (left shoulder)
     *           |
     *           |
     *           o                  10 (spine)
     *           |
     *           |
     *         __o__                11 (center hip)
     *        /     \
     *       o       o              12 (right hip), 13 (left hip)
     *      /         \
     *     |           |
     *     |           |
     *     o           o            14 (right knee), 15 (left knee)
     *     |           |
     *     |           |
     *  o__o           o__o         18 (right foot), 16 (right ankle), 17 (left ankle), 19 (left foot)
     * 
     * 
     * @param cleanedSkeleton is true if filtered motion is selected
     * @version 1.0
     * @since 1.0
     */
    private void createSkeletonInAllFrames(boolean cleanedSkeleton) {

        // removes last drawn skeleton
        if (lastFrame != null) {
            if (wasPlayedOriginal) {
                universe.getLocale().removeBranchGraph(skeletonsInAllFrames.get(lastFrame));
            } else {
                universe.getLocale().removeBranchGraph(skeletonsInAllFramesCleaned.get(lastFrame));
            }
        }

        if (playRadioButton1.isSelected() || playRadioButton3.isSelected()) {
            wasPlayedOriginal = true;
        } else {
            wasPlayedOriginal = false;
        }

        // removes old skeletons
        if (cleanedSkeleton) {
            skeletonsInAllFramesCleaned.clear();
        } else {
            skeletonsInAllFrames.clear();
        }

        // resets slider and frame counters
        lastFrame = null;
        currentFrame = 0;
        playerSlider.setValue(0);


        int j = 0;
        for (int i = 0; i < numberOfFrames; i++) {

            BranchGroup skeleton = new BranchGroup();
            LineArray line = new LineArray(38, LineArray.COORDINATES);
            Point3f[] points = new Point3f[20];

            j = 0;
            for (JointType jointType : JointType.values()) {

                ArrayList<Float> currentXPositions = new ArrayList<Float>();
                ArrayList<Float> currentYPositions = new ArrayList<Float>();
                ArrayList<Float> currentZPositions = new ArrayList<Float>();

                if (cleanedSkeleton) {
                    currentXPositions = (ArrayList<Float>) mainSkeleton.getxPositionsCleaned().get(jointType);
                    currentYPositions = (ArrayList<Float>) mainSkeleton.getyPositionsCleaned().get(jointType);
                    currentZPositions = (ArrayList<Float>) mainSkeleton.getzPositionsCleaned().get(jointType);
                } else {
                    currentXPositions = (ArrayList<Float>) mainSkeleton.getxPositions().get(jointType);
                    currentYPositions = (ArrayList<Float>) mainSkeleton.getyPositions().get(jointType);
                    currentZPositions = (ArrayList<Float>) mainSkeleton.getzPositions().get(jointType);
                }
                Float currentXPosition = (-1) * currentXPositions.get(i);
                Float currentYPosition = currentYPositions.get(i);
                Float currentZPosition = currentZPositions.get(i);

                points[j] = new Point3f(currentXPosition, currentYPosition, currentZPosition);

                j++;

            }

            line.setCoordinate(0, points[0]);
            line.setCoordinate(1, points[1]);
            line.setCoordinate(2, points[1]);
            line.setCoordinate(3, points[2]);
            line.setCoordinate(4, points[1]);
            line.setCoordinate(5, points[3]);
            line.setCoordinate(6, points[3]);
            line.setCoordinate(7, points[5]);
            line.setCoordinate(8, points[2]);
            line.setCoordinate(9, points[4]);
            line.setCoordinate(10, points[5]);
            line.setCoordinate(11, points[7]);
            line.setCoordinate(12, points[4]);
            line.setCoordinate(13, points[6]);
            line.setCoordinate(14, points[7]);
            line.setCoordinate(15, points[9]);
            line.setCoordinate(16, points[6]);
            line.setCoordinate(17, points[8]);
            line.setCoordinate(18, points[1]);
            line.setCoordinate(19, points[10]);
            line.setCoordinate(20, points[10]);
            line.setCoordinate(21, points[11]);
            line.setCoordinate(22, points[11]);
            line.setCoordinate(23, points[13]);
            line.setCoordinate(24, points[11]);
            line.setCoordinate(25, points[12]);
            line.setCoordinate(26, points[13]);
            line.setCoordinate(27, points[15]);
            line.setCoordinate(28, points[12]);
            line.setCoordinate(29, points[14]);
            line.setCoordinate(30, points[15]);
            line.setCoordinate(31, points[17]);
            line.setCoordinate(32, points[14]);
            line.setCoordinate(33, points[16]);
            line.setCoordinate(34, points[17]);
            line.setCoordinate(35, points[19]);
            line.setCoordinate(36, points[16]);
            line.setCoordinate(37, points[18]);
            Shape3D plShape = new Shape3D(line);
            skeleton.addChild(plShape);

            skeleton.setCapability(BranchGroup.ALLOW_DETACH);

            if (cleanedSkeleton) {
                skeletonsInAllFramesCleaned.add(skeleton);
            } else {
                skeletonsInAllFrames.add(skeleton);
            }
        }

    }

    /**
     * Gets X, Y or Z axis from a defined TreeMap.
     * 
     * @param positions is the TreeMap with axis to be parsed
     * @param seriesName is the name for XYSeries
     * @version 1.0
     * @since 1.0
     */
    private void getAxis(TreeMap<JointType, ArrayList<Float>> positions, String seriesName) {

        int i = 0;
        for (Map.Entry entry : positions.entrySet()) {

            XYSeries series = new XYSeries(seriesName);

            ArrayList<Float> posTemp = (ArrayList<Float>) entry.getValue();
            i = 1;

            for (Float pos : posTemp) {
                series.add(i, pos);
                i++;
            }

            insertIntoDataset((JointType) entry.getKey(), series);

        }

    }

    /**
     * Inserts series into datasets according to joint type.
     * 
     * @param key is a JointType defined in entry
     * @param series is XYSeries with data filled
     * @version 1.0
     * @since 1.0
     */
    private void insertIntoDataset(JointType key, XYSeries series) {

        if (key == JointType.HEAD) {
            datasetHead.addSeries(series);
        } else if (key == JointType.SHOULDERCENTER) {
            datasetShoulderCenter.addSeries(series);
        } else if (key == JointType.SHOULDERRIGHT) {
            datasetShoulderRight.addSeries(series);
        } else if (key == JointType.SHOULDERLEFT) {
            datasetShoulderLeft.addSeries(series);
        } else if (key == JointType.ELBOWRIGHT) {
            datasetElbowRight.addSeries(series);
        } else if (key == JointType.ELBOWLEFT) {
            datasetElbowLeft.addSeries(series);
        } else if (key == JointType.WRISTRIGHT) {
            datasetWristRight.addSeries(series);
        } else if (key == JointType.WRISTLEFT) {
            datasetWristLeft.addSeries(series);
        } else if (key == JointType.HANDRIGHT) {
            datasetHandRight.addSeries(series);
        } else if (key == JointType.HANDLEFT) {
            datasetHandLeft.addSeries(series);
        } else if (key == JointType.SPINE) {
            datasetSpine.addSeries(series);
        } else if (key == JointType.HIPCENTER) {
            datasetHipCenter.addSeries(series);
        } else if (key == JointType.HIPRIGHT) {
            datasetHipRight.addSeries(series);
        } else if (key == JointType.HIPLEFT) {
            datasetHipLeft.addSeries(series);
        } else if (key == JointType.KNEERIGHT) {
            datasetKneeRight.addSeries(series);
        } else if (key == JointType.KNEELEFT) {
            datasetKneeLeft.addSeries(series);
        } else if (key == JointType.ANKLERIGHT) {
            datasetAnkleRight.addSeries(series);
        } else if (key == JointType.ANKLELEFT) {
            datasetAnkleLeft.addSeries(series);
        } else if (key == JointType.FOOTRIGHT) {
            datasetFootRight.addSeries(series);
        } else if (key == JointType.FOOTLEFT) {
            datasetFootLeft.addSeries(series);
        }

    }

    /**
     * Adds all existing charts into ArrayList to loop easier.
     * 
     * @param charts is the ArrayList to use as a collection of charts
     * @version 1.0
     * @since 1.0
     */
    private void addAllChartsIntoArray(ArrayList<JFreeChart> charts) {

        charts.add(chartHead);
        charts.add(chartShoulderCenter);
        charts.add(chartShoulderRight);
        charts.add(chartShoulderLeft);
        charts.add(chartElbowRight);
        charts.add(chartElbowLeft);
        charts.add(chartWristRight);
        charts.add(chartWristLeft);
        charts.add(chartHandRight);
        charts.add(chartHandLeft);
        charts.add(chartSpine);
        charts.add(chartHipCenter);
        charts.add(chartHipRight);
        charts.add(chartHipLeft);
        charts.add(chartKneeRight);
        charts.add(chartKneeLeft);
        charts.add(chartAnkleRight);
        charts.add(chartAnkleLeft);
        charts.add(chartFootRight);
        charts.add(chartFootLeft);

    }

    /**
     * Removes all XYSeries from all defined datasets. 
     * Used before updating charts, e.g.: user has imported a new file.
     * 
     * @version 1.0
     * @since 1.0
     */
    private void removeAllSeriesFromDatasets() {

        datasetHead.removeAllSeries();
        datasetShoulderCenter.removeAllSeries();
        datasetShoulderRight.removeAllSeries();
        datasetShoulderLeft.removeAllSeries();
        datasetElbowRight.removeAllSeries();
        datasetElbowLeft.removeAllSeries();
        datasetWristRight.removeAllSeries();
        datasetWristLeft.removeAllSeries();
        datasetHandRight.removeAllSeries();
        datasetHandLeft.removeAllSeries();
        datasetSpine.removeAllSeries();
        datasetHipCenter.removeAllSeries();
        datasetHipRight.removeAllSeries();
        datasetHipLeft.removeAllSeries();
        datasetKneeRight.removeAllSeries();
        datasetKneeLeft.removeAllSeries();
        datasetAnkleRight.removeAllSeries();
        datasetAnkleLeft.removeAllSeries();
        datasetFootRight.removeAllSeries();
        datasetFootLeft.removeAllSeries();

    }

    /**
     * Removes cleaned XYSeries from all defined datasets. 
     * Used when multiple filtering is called.
     * 
     * @version 1.0
     * @since 1.0
     */
    private void removeCleanedSeriesFromDatasets() {

        for (int i = 5; i > 2; i--) {
            datasetHead.removeSeries(i);
            datasetShoulderCenter.removeSeries(i);
            datasetShoulderRight.removeSeries(i);
            datasetShoulderLeft.removeSeries(i);
            datasetElbowRight.removeSeries(i);
            datasetElbowLeft.removeSeries(i);
            datasetWristRight.removeSeries(i);
            datasetWristLeft.removeSeries(i);
            datasetHandRight.removeSeries(i);
            datasetHandLeft.removeSeries(i);
            datasetSpine.removeSeries(i);
            datasetHipCenter.removeSeries(i);
            datasetHipRight.removeSeries(i);
            datasetHipLeft.removeSeries(i);
            datasetKneeRight.removeSeries(i);
            datasetKneeLeft.removeSeries(i);
            datasetAnkleRight.removeSeries(i);
            datasetAnkleLeft.removeSeries(i);
            datasetFootRight.removeSeries(i);
            datasetFootLeft.removeSeries(i);
        }

    }

    /**
     * Sets colours for axes. It is called mostly, when the motion was filtered.
     * 
     * @version 1.0
     * @since 1.0
     */
    private void setColoursForAxes() {

        if (isFiltered) {
            renderer.setSeriesPaint(0, Color.GRAY);
            renderer.setSeriesPaint(1, Color.GRAY);
            renderer.setSeriesPaint(2, Color.GRAY);
            renderer.setSeriesPaint(3, Color.BLUE);
            renderer.setSeriesPaint(4, Color.GREEN);
            renderer.setSeriesPaint(5, Color.RED);
        } else {
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesPaint(1, Color.GREEN);
            renderer.setSeriesPaint(2, Color.RED);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try {
            UIManager.setLookAndFeel("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel");


        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MoTrackSmoother.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MoTrackSmoother.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MoTrackSmoother.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MoTrackSmoother.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MoTrackSmoother().setVisible(true);
            }
        });


    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel currentFrameLabel;
    private javax.swing.JLabel endLabel;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton filterButton;
    private javax.swing.JLabel firstFrameLabel;
    private javax.swing.JLabel fromToLabel;
    private javax.swing.JPanel graphAnkleLeft;
    private javax.swing.JPanel graphAnkleRight;
    private javax.swing.JPanel graphElbowLeft;
    private javax.swing.JPanel graphElbowRight;
    private javax.swing.JPanel graphFootLeft;
    private javax.swing.JPanel graphFootRight;
    private javax.swing.JPanel graphHandLeft;
    private javax.swing.JPanel graphHandRight;
    private javax.swing.JPanel graphHead;
    private javax.swing.JPanel graphHipCenter;
    private javax.swing.JPanel graphHipLeft;
    private javax.swing.JPanel graphHipRight;
    private javax.swing.JPanel graphKneeLeft;
    private javax.swing.JPanel graphKneeRight;
    private javax.swing.JPanel graphShoulderCenter;
    private javax.swing.JPanel graphShoulderLeft;
    private javax.swing.JPanel graphShoulderRight;
    private javax.swing.JPanel graphSpine;
    private javax.swing.JPanel graphWristLeft;
    private javax.swing.JPanel graphWristRight;
    private javax.swing.JScrollPane graphsScrollPane;
    private javax.swing.JPanel graphsScrollPanel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lastFrameLabel;
    private javax.swing.JTextField noiseField;
    private javax.swing.JLabel noiseLabel;
    private javax.swing.JButton playButton;
    private javax.swing.JRadioButton playRadioButton1;
    private javax.swing.JRadioButton playRadioButton2;
    private javax.swing.JRadioButton playRadioButton3;
    private javax.swing.JRadioButton playRadioButton4;
    private javax.swing.JPanel playerPanel;
    private javax.swing.JSlider playerSlider;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton setEndButton;
    private javax.swing.JButton setStartButton;
    private javax.swing.JLabel startLabel;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}