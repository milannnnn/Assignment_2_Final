package assignment2;

	
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// Plot the average angle vs the average voltage for each system state of each cluster
// 2 external libraries need to be downloaded  

@SuppressWarnings("serial")
public class PlotClusters extends Frame {
	public PlotClusters(final String title, ArrayList<ArrayList<SystemState>> Clusters) {
	    super(title);
	    final XYSeriesCollection data = new XYSeriesCollection();
	    // each cluster is treated as a XYSeries in order to have different colors and symbols for each cluster
	    for(int i=0; i<Clusters.size(); i++){
	    	if(!Clusters.get(i).isEmpty()){
	    		// create the legend
		    	String namePlot = "Cluster " + Clusters.get(i).get(0).label;
			    final XYSeries series = new XYSeries(namePlot);
			    ArrayList<SystemState> newCluster = Clusters.get(i);
			    meanOneCluster( newCluster, series);
			    data.addSeries(series);
	    	}
	    }
	    // create the graph as a scatter plot
	    // set labels for the axes
		final JFreeChart chart = ChartFactory.createScatterPlot(
	        title,
	        "average state angle", 
	        "avarage state voltage", 
	        data,
	        PlotOrientation.VERTICAL,
	        true,
	        true,
	        false
	    );
		// customize plot's aspect
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double widthScreen = screenSize.getWidth();
		double heightScreen = screenSize.getHeight();
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		chart.getXYPlot().setDomainGridlinesVisible(true);
		chart.getXYPlot().setRangeGridlinePaint(Color.GRAY);
		chart.getXYPlot().setDomainGridlinePaint(Color.GRAY);
	    final ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setPreferredSize(new java.awt.Dimension( (int) (widthScreen*0.6), (int) (heightScreen*0.6)));
	    // insert the graph in JPane message dialog
	    JOptionPane.showMessageDialog(null, chartPanel, "", JOptionPane.PLAIN_MESSAGE);
	}
	// ##################################################################################
	// calculate mean voltage and angle of one cluster
	// add the calculated values to the XYseries
	private static void meanOneCluster(ArrayList<SystemState> newCluster, XYSeries series){
		ArrayList<Double> meanVOneCluster = new ArrayList<Double>();
		ArrayList<Double> meanAngOneCluster = new ArrayList<Double>();
		for(int i=0; i < newCluster.size(); i++){
			double[] values =  newCluster.get(i).values();
			double meanV = 0;
			double meanAng = 0;
			for(int ii=0; ii < values.length/2 ; ii ++ ){
				meanV = meanV + values[2*ii+1]/(values.length/2);
				meanAng = meanAng + values[2*ii]/(values.length/2);
			}
			meanVOneCluster.add(meanV);
			meanAngOneCluster.add(meanAng);
			series.add(meanAng,meanV);
		}
	}
}


