package assignment2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.table.TableColumn;

import org.jfree.ui.RefineryUtilities;

@SuppressWarnings("serial")
public class Gui extends JFrame {
	
	// screen dimensions
	double widthScreen;
	double heightScreen;
	// Declare objects that will appear on the interface
	// TextFields
	private JTextField tf1;
	private JPasswordField tf2;
	private JTextField tf1title;
	private JTextField tf2title;
	private JTextField Customtitle;
	private JTextField KMEANtitle;
	private JTextField Scaletitle;
	private JTextField Actiontitle;
	private JTextArea errorText;
	private JTextField numTempClusters;
	private JTextField numTempClustersValue;
	private JTextField KNNTitle;
	private JTextField maxIterTitle;
	private JTextField maxIterText;
	private JTextField neighborsNumTitle;
	private JTextField neighborsNumText;
	// buttons
	private JRadioButton defb;
	private JRadioButton cusb;
	private JRadioButton forgybutton;
	private JRadioButton RPMbutton;
	private JRadioButton topologybutton;
	private JRadioButton generalbutton;
	private ButtonGroup groupDC;
	private ButtonGroup groupF_RPM;
	private ButtonGroup grouplabel;
	private JButton buttCluster;
	private JButton buttPlot;
	private JButton buttKNN;
	private JButton exportButton;
	// checkBox
	private JCheckBox downScaleCB;
	// set boolean variable to allow the choice of custom options
	boolean	customOpt = false;
	// set default variables
	private String USER = "root";
	private String PASS = "root";
	private int tempClusters = 16; // number of temporary clusters
	private String KmeanMethod = "forgy"; // k-mean clustering method
	ArrayList<ArrayList<SystemState>> Clusters;
	private int maxIter = 100; // max number of iteration
	private int clusterNum = 4; // final number of clusters
	private int neighborNum = 6; // number of neighbors for KNN
	private boolean labelingGen = false; // labeling technique
	
	// ############################################################################################################
	public Gui(){
		
		// set interface title
		super("Project 2");
		// use a pre-determined layout, i.e. FlowLayout
		setLayout(new FlowLayout());
		
		// get screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		widthScreen = screenSize.getWidth();
		heightScreen = screenSize.getHeight();
		// set the dimension for the console considering different screen resolutions 
		int consoleHeight, consoleWidth;
		if(widthScreen>=1920){
			widthScreen  = 1920.0;
			heightScreen = 1080.0;
			consoleHeight = (int) (0.407*heightScreen);
			consoleWidth  = (int) (0.595* widthScreen);
		}
		else if(widthScreen==1366){
			consoleHeight = (int) (0.3*heightScreen);
			consoleWidth  = (int) (0.595* widthScreen);
		}
		else{
			consoleHeight = (int) (0.22*heightScreen);
			consoleWidth  = (int) (0.595* widthScreen);
		}
		
		// set the width of text fields
		int textWidth = (int) (widthScreen*0.6*0.12);
		
		// CREATE GRAPHICAL OBJECTS
		
		// #####
		// default/ custom radio buttons
		Customtitle = new JTextField("DEFAULT-CUSTOM OPTIONS", textWidth);
		Customtitle.setFont(new Font("Serif",Font.BOLD, 18));
		Customtitle.setHorizontalAlignment(JTextField.CENTER);
		Customtitle.setBackground(Color.GRAY);
		Customtitle.setEditable(false);
		add(Customtitle);
		defb = new JRadioButton("default options", true);
		defb.setFont(new Font("Serif",Font.BOLD, 18));
		// true is checked, false in unchecked
		cusb = new JRadioButton("custom options", false);
		cusb.setFont(new Font("Serif",Font.BOLD, 18));
		add(defb);
		add(cusb);
		// group the radio buttons together
		groupDC = new ButtonGroup();
		groupDC.add(defb);
		groupDC.add(cusb);
		// #####
		
		// Text fields (USER, PASSWORD)
		// user
		tf1title = new JTextField("MySQL USERNAME", textWidth);
		tf1title.setEditable(false);
		tf1title.setFont(new Font("Serif",Font.BOLD, 18));
		tf1title.setHorizontalAlignment(JTextField.CENTER);
		tf1title.setBackground(Color.GRAY);
		add(tf1title);
		tf1 = new JTextField("root", 15);
		tf1.setFont(new Font("Serif",Font.PLAIN, 18));
		tf1.setToolTipText("insert MySQL USERNAME and press Enter");
		// by default they are set not editable
		tf1.setEditable(customOpt);
		tf1.setEnabled(false);
		add(tf1);
		// password
		tf2title = new JTextField("MySQL PASSWORD", textWidth);
		tf2title.setFont(new Font("Serif",Font.BOLD, 18));
		tf2title.setHorizontalAlignment(JTextField.CENTER);
		tf2title.setBackground(Color.GRAY);
		tf2title.setEditable(false);
		add(tf2title);
		tf2 = new JPasswordField("root", 15);
		tf2.setFont(new Font("Serif",Font.PLAIN, 18));
		tf2.setEditable(customOpt);
		tf2.setToolTipText("insert MySQL PASSWORD and press Enter");
		tf2.setEnabled(false);
		add(tf2);
		// #####
		
		// random partition - forgy method radio buttons plus text title
		// title
		KMEANtitle = new JTextField("K-MEAN INITIALIZATION OPTIONS", textWidth);
		KMEANtitle.setFont(new Font("Serif",Font.BOLD, 18));
		KMEANtitle.setHorizontalAlignment(JTextField.CENTER);
		KMEANtitle.setBackground(Color.GRAY);
		KMEANtitle.setEditable(false);
		add(KMEANtitle);
		// forgy button
		forgybutton = new JRadioButton("forgy method", true);
		forgybutton.setFont(new Font("Serif",Font.BOLD, 18));
		forgybutton.setEnabled(false);
		// RPM button
		RPMbutton = new JRadioButton("random partition method", false);
		RPMbutton.setFont(new Font("Serif",Font.BOLD, 18));
		RPMbutton.setEnabled(false);
		add(forgybutton);
		add(RPMbutton);
		// group the radio buttons together
		groupF_RPM= new ButtonGroup();
		groupF_RPM.add(forgybutton);
		groupF_RPM.add(RPMbutton);
		// #####
		
		// max iteration section
		maxIterTitle = new JTextField("Maximum iteration",20);
		maxIterTitle.setBorder(null);
		maxIterTitle.setHorizontalAlignment(JTextField.CENTER);
		maxIterTitle.setFont(new Font("Serif",Font.BOLD, 18));
		maxIterTitle.setEditable(false);
		maxIterTitle.setEnabled(false);
		add(maxIterTitle);
		maxIterText = new JTextField("100", 5);
		maxIterText.setFont(new Font("Serif",Font.PLAIN, 18));
		maxIterText.setHorizontalAlignment(JTextField.CENTER);
		maxIterText.setEditable(false);
		maxIterText.setEnabled(false);
		maxIterText.setToolTipText("Set number of maximum iterations and press enter");
		add(maxIterText);
		// #####
		
		// up-scaling options
		// title
		Scaletitle = new JTextField("UP-SCALING OPTIONS", textWidth);
		Scaletitle.setFont(new Font("Serif",Font.BOLD, 18));
		Scaletitle.setHorizontalAlignment(JTextField.CENTER);
		Scaletitle.setBackground(Color.GRAY);
		Scaletitle.setEditable(false);
		add(Scaletitle);
		// down Scale check box
		downScaleCB = new JCheckBox("Apply up-scaling");
		downScaleCB.setFont(new Font("Serif",Font.BOLD, 18));
		downScaleCB.setSelected(false);
		downScaleCB.setEnabled(false);
		add(downScaleCB);
		// number of temporary clusters
		numTempClusters = new JTextField("Intermediate Clusters Number",20);
		numTempClusters.setBorder(null);
		numTempClusters.setHorizontalAlignment(JTextField.CENTER);
		numTempClusters.setFont(new Font("Serif",Font.BOLD, 18));
		numTempClusters.setEditable(false);
		numTempClusters.setEnabled(false);
		add(numTempClusters);
		numTempClustersValue = new JTextField("16", 5);
		numTempClustersValue.setFont(new Font("Serif",Font.PLAIN, 18));
		numTempClustersValue.setHorizontalAlignment(JTextField.CENTER);
		numTempClustersValue.setEditable(false);
		numTempClustersValue.setEnabled(false);
		numTempClustersValue.setToolTipText("Set number of temporary clusters and press enter");
		add(numTempClustersValue);
		// #####
		
		// buttons to execute K-mean, create and label clusters
		// title
		Actiontitle = new JTextField("K-MEAN SECTION", textWidth);
		Actiontitle.setFont(new Font("Serif",Font.BOLD, 18));
		Actiontitle.setHorizontalAlignment(JTextField.CENTER);
		Actiontitle.setBackground(Color.GRAY);
		Actiontitle.setEditable(false);
		add(Actiontitle);
		// clustering button
		buttCluster = new JButton("Create Clusters");
		buttCluster.setFont(new Font("Serif",Font.BOLD, 18));
		add(buttCluster);
		// plot button
		buttPlot = new JButton("Plot Clusters");
		buttPlot.setFont(new Font("Serif",Font.BOLD, 18));
		buttPlot.setEnabled(false);
		add(buttPlot);
		// export CSV button
		exportButton = new JButton("Export CSV");
		exportButton.setFont(new Font("Serif",Font.BOLD, 18));
		exportButton.setEnabled(false);
		add(exportButton);
		// create radio buttons to choose labeling method
		topologybutton = new JRadioButton("System specific labeling", true);
		topologybutton.setFont(new Font("Serif",Font.BOLD, 18));
		topologybutton.setEnabled(false);
		generalbutton = new JRadioButton("General labeling", false);
		generalbutton.setFont(new Font("Serif",Font.BOLD, 18));
		generalbutton.setEnabled(false);
		add(topologybutton);
		add(generalbutton);
		// group the radiobuttons together
		grouplabel = new ButtonGroup();
		grouplabel.add(topologybutton);
		grouplabel.add(generalbutton);
		// #####
		
		// create buttons to execute KNN
		// title
		KNNTitle = new JTextField("K-NN SECTION", textWidth);
		KNNTitle.setFont(new Font("Serif",Font.BOLD, 18));
		KNNTitle.setHorizontalAlignment(JTextField.CENTER);
		KNNTitle.setBackground(Color.GRAY);
		KNNTitle.setEditable(false);
		add(KNNTitle);
		// KNN button
		buttKNN = new JButton("Classify Test-set");
		buttKNN.setFont(new Font("Serif",Font.BOLD, 18));
		buttKNN.setEnabled(false);
		add(buttKNN);
		// number of temporary clusters
		neighborsNumTitle = new JTextField("Neighbors Number",15);
		neighborsNumTitle.setBorder(null);
		neighborsNumTitle.setHorizontalAlignment(JTextField.CENTER);
		neighborsNumTitle.setFont(new Font("Serif",Font.BOLD, 18));
		neighborsNumTitle.setEditable(false);
		neighborsNumTitle.setEnabled(false);
		add(neighborsNumTitle);
		neighborsNumText = new JTextField("6", 5);
		neighborsNumText.setFont(new Font("Serif",Font.PLAIN, 18));
		neighborsNumText.setHorizontalAlignment(JTextField.CENTER);
		neighborsNumText.setEditable(false);
		neighborsNumText.setEnabled(false);
		neighborsNumText.setToolTipText("Set number of neighbors to consider and press enter");
		add(neighborsNumText);
		// #####
		
		// create console to display outputs
		errorText = new JTextArea();
		// set text color to red
		errorText.setForeground(Color.RED);
		PrintStream printStream = new PrintStream(new CustomOutputStream(errorText));
		System.setOut(printStream);
		System.setErr(printStream);
		// add a scroll pane
		JScrollPane scrollPane1 = new JScrollPane(errorText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setPreferredSize(new Dimension(consoleWidth,consoleHeight));
		add(scrollPane1);
		// #####
		
		// ASSIGN EVENT HANDLERS
		
		// HANDLE ENTER PRESSURE ON TEXT FIELD
		// SQL user and password
		EnterHandler enterhandler = new EnterHandler();
		tf1.addActionListener(enterhandler);
		tf2.addActionListener(enterhandler);
		// number of temporary clusters
		EnterHandlerCluster enterHandlerCluster = new EnterHandlerCluster();
		numTempClustersValue.addActionListener(enterHandlerCluster);
		// handle max iter
		EnterHandleriter enterHandleriter = new EnterHandleriter();
		maxIterText.addActionListener(enterHandleriter);
		// handle neighbors number
		EnterHandlerNeighNum enterHandlerNeighNum = new EnterHandlerNeighNum();
		neighborsNumText.addActionListener(enterHandlerNeighNum);
		// #####
		
		// HANDLE RADIO BUTTONS
		// handle default-custom options with radio buttons
		ClickHandler clickhandler = new ClickHandler();
		defb.addItemListener(clickhandler);
		cusb.addItemListener(clickhandler);
		// check box
		downScaleCB.addActionListener(enterhandler);
		// handle initialization of kmean
		KmeanInit kmeanInit = new KmeanInit();
		forgybutton.addItemListener(kmeanInit);
		RPMbutton.addItemListener(kmeanInit);
		// handle labeling
		labelHandler clickhandler1 = new labelHandler();
		topologybutton.addItemListener(clickhandler1);
		generalbutton.addItemListener(clickhandler1);
		// #####
		
		// HANDLE BUTTON PUSHING
		// handle cluster creation button
		ButtonKmeanHandler buttonKmeanHandler = new ButtonKmeanHandler();
		buttCluster.addActionListener(buttonKmeanHandler);
		// handle plot button
		ButtonPlotHandler buttonPlotHandler = new ButtonPlotHandler();
		buttPlot.addActionListener(buttonPlotHandler);
		// handle CSV button
		ButtonCSVHandler buttonCSVHandler = new ButtonCSVHandler();
		exportButton.addActionListener(buttonCSVHandler);
		// handel KNN
		ButtonKNNHandler buttonKNNHandler = new ButtonKNNHandler();
		buttKNN.addActionListener(buttonKNNHandler);
		
	}
	// ############################################################################################################
	// class to plot the console output in the GUI
	public class CustomOutputStream extends OutputStream {
	    private JTextArea textArea;
	     
	    public CustomOutputStream(JTextArea textArea) {
	        this.textArea = textArea;
	    }
	     
	    @Override
	    public void write(int b) throws IOException {
	        // redirects data to the text area
	        textArea.append(String.valueOf((char)b));
	        // scrolls the text area to the end of data
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	}
	// ############################################################################################################
	// handle default-custom options with radio buttons
	private class ClickHandler implements ItemListener{
		public void itemStateChanged(ItemEvent event){
			// if default options are selected
			if(defb.isSelected() == true){
				// set the variable to default values
				USER = "root";
				PASS = "root";
				tempClusters = 16;
				KmeanMethod = "forgy";
				customOpt=false;
				tf1.setEditable(customOpt);
				tf2.setEditable(customOpt);
				tf1.setEnabled(customOpt);
				tf2.setEnabled(customOpt);
				RPMbutton.setEnabled(customOpt);
				forgybutton.setEnabled(customOpt);
				downScaleCB.setEnabled(customOpt);
				numTempClusters.setEnabled(customOpt);
				numTempClustersValue.setEnabled(customOpt);
				numTempClustersValue.setEnabled(customOpt);
				buttCluster.setEnabled(!customOpt);
				buttKNN.setEnabled(!customOpt);
				buttPlot.setEnabled(customOpt);
				exportButton.setEnabled(customOpt);
				maxIterText.setEditable(customOpt);
				maxIterText.setEnabled(customOpt);
				maxIterTitle.setEnabled(customOpt);
				neighborsNumTitle.setEnabled(customOpt);
				neighborsNumText.setEditable(customOpt);
				neighborsNumText.setEnabled(customOpt);
				buttKNN.setEnabled(customOpt);
				topologybutton.setEnabled(customOpt);
				generalbutton.setEnabled(customOpt);
				labelingGen = false;
			}
			// if custom options are selected
			else if(cusb.isSelected() == true){
				customOpt=true;
				tempClusters = 4;
				tf1.setEditable(customOpt);
				tf1.setEnabled(customOpt);
				buttCluster.setEnabled(!customOpt);
				buttKNN.setEnabled(!customOpt);
				buttPlot.setEnabled(!customOpt);
				neighborsNumTitle.setEnabled(!customOpt);
				neighborsNumText.setEditable(!customOpt);
				neighborsNumText.setEnabled(!customOpt);
				buttKNN.setEnabled(!customOpt);
				exportButton.setEnabled(!customOpt);
			}
		}
	}
	// ############################################################################################################
	// handle enter press on SQL text fields and check-box selection
	private class EnterHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			// when enter is pressed on USER field
			if(event.getSource()==tf1){
				USER = event.getActionCommand();
				JOptionPane.showMessageDialog(null, "USERNAME successfully inserted");
				tf2.setEditable(customOpt);
				tf2.setEnabled(customOpt);
			}
			// when enter is pressed on PASS field
			else if(event.getSource()==tf2){
				PASS = event.getActionCommand();
				JOptionPane.showMessageDialog(null, "PASSWORD successfully inserted");
				RPMbutton.setEnabled(customOpt);
				forgybutton.setEnabled(customOpt);
				downScaleCB.setEnabled(customOpt);
				buttCluster.setEnabled(customOpt);
				maxIterText.setEditable(customOpt);
				maxIterText.setEnabled(customOpt);
				maxIterTitle.setEnabled(customOpt);
				topologybutton.setEnabled(customOpt);
				generalbutton.setEnabled(customOpt);
			
			}
			// when down-scale check-box is checked
			else if(downScaleCB.isSelected()){
				numTempClusters.setEnabled(customOpt);
				numTempClustersValue.setEnabled(customOpt);
				numTempClustersValue.setEditable(customOpt);
				tempClusters = Integer.parseInt(numTempClustersValue.getText());
				
			}
			else if(!(downScaleCB.isSelected())){
				numTempClusters.setEnabled(!customOpt);
				numTempClustersValue.setEnabled(!customOpt);
				numTempClustersValue.setEditable(!customOpt);
				tempClusters = 4;
			}
		}
	}
	// ############################################################################################################
	// handle max iterations number insertion
	private class EnterHandleriter implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(event.getSource() == maxIterText){
				try{
					maxIter = Integer.parseInt(event.getActionCommand());
					JOptionPane.showMessageDialog(null, "Maximum iteration successfully inserted");
				}catch(NumberFormatException ex){ 
					// check if the inserted value is a number
					playSound();
					JOptionPane.showMessageDialog(null, "Maximum iteration has to be a number!");
					maxIterText.setText("100");
					maxIter = 100;
				}
			}
	
		}
	}
	// ############################################################################################################
	// handle temporary cluster's number insertion
	private class EnterHandlerCluster implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(event.getSource() == numTempClustersValue){
				try{
					tempClusters = Integer.parseInt(event.getActionCommand());
					if(tempClusters<4){
						playSound();
						tempClusters = 4;
						JOptionPane.showMessageDialog(null, "Temporary clusters number has to be greater or equal to 4!");
						numTempClustersValue.setText("4");
					}else if(tempClusters>100){
						playSound();
						tempClusters = 100;
						JOptionPane.showMessageDialog(null, "Temporary clusters cannot be greater than 100!");
						numTempClustersValue.setText("100");
					}
					else{
						JOptionPane.showMessageDialog(null, "Temporary clusters number successfully inserted");
					}
				}catch(NumberFormatException ex){ 
					playSound();
					JOptionPane.showMessageDialog(null, "Temporary clusters number has to be a number!");
					numTempClustersValue.setText("16");
					tempClusters = 16;
				}
			}
			
		}
	}
	// ############################################################################################################
	// handle k-mean initialization options
	private class KmeanInit implements ItemListener{
		public void itemStateChanged(ItemEvent event){
			if(forgybutton.isSelected()){
				KmeanMethod = "forgy";
			}else if(RPMbutton.isSelected()){
				KmeanMethod = "RPM";
			}
		}
	}
	// ############################################################################################################
	// handle Neighbors number's number insertion  
	private class EnterHandlerNeighNum implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(event.getSource() == neighborsNumText){
				try{
					neighborNum = Integer.parseInt(event.getActionCommand());
					if(neighborNum < 1){
						playSound();
						neighborNum = 1;
						JOptionPane.showMessageDialog(null, "Neighbors number has to be at least 1!");
						neighborsNumText.setText("1");
					}else if(neighborNum>100){
						playSound();
						neighborNum = 100;
						JOptionPane.showMessageDialog(null, "Neighbors number cannot be greater than 100!");
						neighborsNumText.setText("100");
					}
					else{
						JOptionPane.showMessageDialog(null, "Neighbors number successfully inserted");
					}
				}catch(NumberFormatException ex){ 
					playSound();
					JOptionPane.showMessageDialog(null, "Neighbors number has to be a number!");
					neighborNum = 6;
					neighborsNumText.setText("6");
				}
			}
		}
	}
	// ############################################################################################################
	// handle labeling technique options with radio buttons
	private class labelHandler implements ItemListener{
		public void itemStateChanged(ItemEvent event){
			if(topologybutton.isSelected()){
				labelingGen = false;
			}
			else if(generalbutton.isSelected()){
				labelingGen = true;
			}
		}
	}
	// ############################################################################################################
	// handle K-mean button
	private class ButtonKmeanHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent event){
			// Run the main code inside a New Thread (if error occurs - only thread gets killed, and GUI stays operational)
			new Thread(){
				public void run(){
					// read the database and create an ArrayList of SystemStates 
					FillStates fillings = new FillStates();
					ArrayList<SystemState> allStates = fillings.getStates(USER, PASS, "measurements");
					// apply k-mean algorithm on the read SystemStates t divide them in 4 clusters
					Kmean kmeanTest = new Kmean(allStates, 1e-16, maxIter);
					Clusters = kmeanTest.kMeanClustering(tempClusters,clusterNum,KmeanMethod);
					// label the newly created clusters
					Label myLabel = new Label();
					if(labelingGen){
						myLabel.declareLabelsGeneral(Clusters);
					}else{
						myLabel.declareLabelsSpecific(Clusters);	
					}
					// display the labeled clusters 
					JPanel clusterPanel = new JPanel();
					clusterPanel.setPreferredSize(new Dimension((int)(widthScreen*0.91),(int)(heightScreen*0.81)));
					for(int ii=0; ii<Clusters.size(); ii++){
						String[][] tableCluster = new String[Clusters.get(ii).size()][Clusters.get(ii).get(0).values().length+1];
						System.out.println("Cluster number " + (ii+1) + " Cluster size " + Clusters.get(ii).size());
						for(int i=0; i<Clusters.get(ii).size(); i++){
							 tableCluster[i] = Clusters.get(ii).get(i).stringArrayValues();
						}
						makeTable(tableCluster,ii,clusterPanel);
					}
					JOptionPane.showMessageDialog(null, clusterPanel,"Clusters" , JOptionPane.PLAIN_MESSAGE);
					// unlock buttons that use the label clusters
					buttPlot.setEnabled(true);
					exportButton.setEnabled(true);
					buttKNN.setEnabled(true);
					neighborsNumTitle.setEnabled(customOpt);
					neighborsNumText.setEditable(customOpt);
					neighborsNumText.setEnabled(customOpt);
				}
			}.start();
		}
		
		// ########################
		// add the clusters to clusterPanel and format them
		public void makeTable(String[][] tableCluster, int clusNum, JPanel clusterPanel){
		String[] names = new String[tableCluster[0].length];
		names[0] = "time";
		for( int i=1; i<tableCluster[0].length; i += 2){
			names[i] = "ANG_" + ((i/2)+1);
			names[i+1] = "VOL_" + (i/2+1);
		}
		// create table with given matrix and column names
		JTable Table = new JTable(tableCluster, names );
		// disable auto-resize
		Table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// set Font
		Table.setFont(new Font("Serif",Font.PLAIN, 22));
		
		// set columns' width
		TableColumn column = null;
	    for (int i = 0; i < tableCluster[0].length; i++) {
	        column = Table.getColumnModel().getColumn(i);
	        column.setPreferredWidth((int)(0.8*widthScreen/18)); 
	    }  
	    // set rows' height
	    Table.setRowHeight((int)(0.8*heightScreen/18));
	    // create a scroll pane and add the table to it
		JScrollPane scrollPane = new JScrollPane(Table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setFont(new Font("Serif",Font.PLAIN, 50));
		// set window size and limits
		scrollPane.setPreferredSize( new Dimension((int)(0.5*0.8*widthScreen),(int)(0.5*0.7*heightScreen)));
		// add title for the tables
		if(clusNum==0 || clusNum== 2){
			JTextField clusterTitle = new JTextField("CLUSTER " + Clusters.get(clusNum).get(0).label, (int) (0.5*0.8*widthScreen/15));
			clusterTitle.setFont(new Font("Serif",Font.BOLD, 18));
			clusterTitle.setHorizontalAlignment(JTextField.CENTER);
			clusterTitle.setBackground(Color.GRAY);
			clusterTitle.setEditable(false);
			clusterPanel.add(clusterTitle, null);
			JTextField clusterTitle1 = new JTextField("CLUSTER " + Clusters.get(clusNum+1).get(0).label, (int) (0.5*0.8*widthScreen/15));
			clusterTitle1.setFont(new Font("Serif",Font.BOLD, 18));
			clusterTitle1.setHorizontalAlignment(JTextField.CENTER);
			clusterTitle1.setBackground(Color.GRAY);
			clusterTitle1.setEditable(false);
			clusterPanel.add(clusterTitle1, null);
		}
		clusterPanel.add(scrollPane, null);
		}
	}
	// ############################################################################################################
	// handle cluster-plot button
	private class ButtonPlotHandler implements ActionListener{					
		public void actionPerformed(ActionEvent event){	
		// Run the main code inside a New Thread (if error occurs - only thread gets killed, and GUI stays operational)
			new Thread(){
				public void run(){
					final PlotClusters plotClusters = new PlotClusters("Clusters plot",Clusters);
					plotClusters.pack();
				    RefineryUtilities.centerFrameOnScreen(plotClusters);
				    plotClusters.setVisible(false);
				}
			}.start();
		}
	}
	// ############################################################################################################
	//	handle CSV button
	private class ButtonCSVHandler implements ActionListener{					
		public void actionPerformed(ActionEvent event){
			new Thread(){
				public void run(){
			Kmean.CSV(Clusters, clusterNum);
			JOptionPane.showMessageDialog(null, "Clusters succesfully exported in CSV");
				}
			}.start();
		}
	}
	// ############################################################################################################
	//	handle KNN button
	private class ButtonKNNHandler implements ActionListener{					
		public void actionPerformed(ActionEvent event){
			new Thread(){
				public void run(){
					// create the learn set from the labeled clusters
					FillStates fillings = new FillStates();
					ArrayList<SystemState> learnSet = new ArrayList<SystemState>();
					for (int i=0; i<Clusters.size();i++){
						learnSet.addAll(Clusters.get(i));
					}
					// read the values from the analog_values table and create the relative SystemStates to classify
					ArrayList<SystemState> testSet = fillings.getStates(USER, PASS, "analog_values");
					// set the order of the buses in order to coincide with the learnSet's one
					String[] busOrder = new String[learnSet.get(0).buses.size()];
					for(int k=0; k<learnSet.get(0).buses.size(); k++){
						busOrder[k] = learnSet.get(0).buses.get(k).busID;
					}
					for (int i=0; i<testSet.size();i++){
						testSet.get(i).reNormalize();
						testSet.get(i).sortBuses(busOrder);// Resort Buses
						testSet.get(i).normalize(learnSet.get(0).minAngles, learnSet.get(0).maxAngles, learnSet.get(0).minVolts, learnSet.get(0).maxVolts);
					}
					// classify the test set, via KNN, using the learn set
					ArrayList<String[]> labelPros = KNNmethod.KNN( neighborNum,  testSet, learnSet, clusterNum );
					
					ArrayList<ArrayList<SystemState>> testCluters= new ArrayList<ArrayList<SystemState>>();
					for(int k=0; k<clusterNum; k++){
						testCluters.add(new ArrayList<SystemState>());
					}
					String[] labelLabel = {"High Load","Low Load","Generator Outage","Line Outage"};
					// print out the results and add the label to the test set's element
					for(int i=0; i<labelPros.size(); i++){
						System.out.println("measurement # " + (i+1) + " belongs to cluster " + labelPros.get(i)[0]+ " (probability = "+labelPros.get(i)[1]+" %)");
						testSet.get(i).label = labelPros.get(i)[0];
						for(int j=0; j<labelLabel.length; j++){
							// add all the states classified to the same cluster to the same group
							if(labelLabel[j].equals(labelPros.get(i)[0])){
								testCluters.get(j).add(testSet.get(i));
							}
						}
					}
					// plot test set's SystemStates showing the cluster they have been classified to 
					PlotClusters  plot2 = new PlotClusters("Test set classification",  testCluters);
					plot2.pack();
				    RefineryUtilities.centerFrameOnScreen(plot2);
				    plot2.setVisible(false);
				}
			}.start();
		}
	}
	// ############################################################################################################
	// DOH... (no explanation needed)
	private void playSound(){
		try {
			Clip clip = AudioSystem.getClip();
		    AudioInputStream inputStream = AudioSystem.getAudioInputStream(Gui.class.getResource("/doh.wav"));
		    clip.open(inputStream);
		    clip.start(); 
		} 
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}

