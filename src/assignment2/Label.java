package assignment2;

import java.util.ArrayList;

//TODO Label the data Without Normalization

public class Label {
	
	// ############################################################################################################
	// Method for Declaring Cluster Operating-State Labels for the Specific System (obtained by topology analysis)
	public void declareLabelsSpecific(ArrayList<ArrayList<SystemState>> Clusters) {
		
		//### Declare Generator Pair Buses
		String[][] genPairs = new String[3][2];
		genPairs[0][0] = "_2BCC3D5923464FED9E08EB12EC388BD7"; //CLAR
		genPairs[0][1] = "_58F637D8B03A4B12A67DF2E5797F9B6A"; //BOWM
		genPairs[1][0] = "_4273A87151C0409780237BFD866C23DA"; //AMHE
		genPairs[1][1] = "_95D3CD0256FB4C9DB2860CFEFA45CD57"; //WAUT
		genPairs[2][0] = "_4F715892155341C8A76534537F095B49"; //WINL
		genPairs[2][1] = "_7AC1BC6CDFAF4F26A97AD322E9F5AD31"; //MAPL
		
		//### Declare Line Pair Buses
		String[][] linePairs = new String[9][2];
		linePairs[0][0] = "_2BCC3D5923464FED9E08EB12EC388BD7"; //CLAR
		linePairs[0][1] = "_58F637D8B03A4B12A67DF2E5797F9B6A"; //BOWM
		linePairs[1][0] = "_4273A87151C0409780237BFD866C23DA"; //AMHE
		linePairs[1][1] = "_95D3CD0256FB4C9DB2860CFEFA45CD57"; //WAUT
		linePairs[2][0] = "_4F715892155341C8A76534537F095B49"; //WINL
		linePairs[2][1] = "_7AC1BC6CDFAF4F26A97AD322E9F5AD31"; //MAPL
		linePairs[3][0] = "_58F637D8B03A4B12A67DF2E5797F9B6A"; //BOWM
		linePairs[3][1] = "_7324D6723635494784A4D8A9578FCE8A"; //TROY
		linePairs[4][0] = "_58F637D8B03A4B12A67DF2E5797F9B6A"; //BOWM
		linePairs[4][1] = "_9D8BB8E8B5DB40F6ABF515042B7DFF97"; //CROS	
		linePairs[5][0] = "_7AC1BC6CDFAF4F26A97AD322E9F5AD31"; //MAPL
		linePairs[5][1] = "_7324D6723635494784A4D8A9578FCE8A"; //TROY		
		linePairs[6][0] = "_7AC1BC6CDFAF4F26A97AD322E9F5AD31"; //MAPL
		linePairs[6][1] = "_7DD325DCEFC248989B72AAD58D3DD4E9"; //GRAN	
		linePairs[7][0] = "_95D3CD0256FB4C9DB2860CFEFA45CD57"; //WAUT
		linePairs[7][1] = "_7DD325DCEFC248989B72AAD58D3DD4E9"; //GRAN
		linePairs[8][0] = "_95D3CD0256FB4C9DB2860CFEFA45CD57"; //WAUT
		linePairs[8][1] = "_9D8BB8E8B5DB40F6ABF515042B7DFF97"; //CROS
		
		//### Calculate All Indicators (Gen, Line and Avg Voltages)
		double[]  genIndicators = analyzeGenOutage(Clusters, genPairs);
		double[] lineIndicators = analyzeLineOutage(Clusters, linePairs);
		double[][] avg = calcComplexAverages(Clusters);
		
		int hl=-1, ll=-1, go=0, lo=0;
		
		//### Map the Generator Outage and Line Outage Clusters
		for(int k=1; k<avg.length; k++){
			// Find the position of Min Gen Indicator (Gen Outage)
			if(genIndicators[k]<genIndicators[go]){
				go=k;
			}
			// Find the position of Max Line Indicator (Line Outage)
			if(lineIndicators[k]>lineIndicators[lo]){
				lo=k;
			}
		}
		
		//### Map the remaining 2 Clusters (Low Load and High Load)
		for(int k=0; k<avg.length; k++){
			if(k!=go && k!= lo && hl<0){
				hl=k;
			}
			if(k!=go && k!= lo && k!=hl && ll<0){
				ll=k;
			}
		}
		//### If High Load has a Larger Volt Amplitude - Change Places
		if(avg[hl][1]>avg[ll][1]){
			int tmp = hl;
			hl = ll;
			ll = tmp;
		}
		//### Label Each State within Clusters
		for(int k=0; k<Clusters.size(); k++){
			String label1;
			if(k==hl){
				label1 = "High Load";
			}
			else if(k==ll){
				label1 = "Low Load";
			}
			else if(k==go){
				label1 = "Generator Outage";
			}
			else{
				label1 = "Line Outage";
			}
			for(int j=0; j<Clusters.get(k).size(); j++){
				Clusters.get(k).get(j).label = label1;
			}
		}
	}

	// ############################################################################################################
	// Method for Declaring Cluster Operating-State Labels for General System (label rule abstracted from specific case)
	public void declareLabelsGeneral(ArrayList<ArrayList<SystemState>> Clusters){
		
		//### Calculate Average State Voltages and Angles for the System
		double[][] avg = calcAverages(Clusters);
		
		int hl=0, ll=0, go=-1, lo=-1;
		//### Find the Positions of High Load and Low Load
		for(int k=1; k<avg.length; k++){
			// Find the position of Min Volt (High Load)
			if(avg[k][1]<avg[hl][1]){
				hl=k;
			}
			// Find the position of Max Volt (Low Load)
			if(avg[k][1]>avg[ll][1]){
				ll=k;
			}
		}
		//### Map the remaining 2 Clusters
		for(int k=0; k<Clusters.size(); k++){
			if((k!=hl) && (k!=ll) && (go<0)){
				go=k;
			}
			if((k!=hl) && (k!=ll) && (k!=go) && (lo<0)){
				lo=k;
			}
		}
		//### If Line Outage has a Higher Avg Angle - Change Them
		if(avg[go][0]>avg[lo][0]){
			int tmp = go;
			go = lo;
			lo = tmp;
		}
		//### Label Each State
		for(int k=0; k<Clusters.size(); k++){
			String label1="";
			if(k==hl){
				label1 = "High Load";
			}
			if(k==ll){
				label1 = "Low Load";
			}
			if(k==go){
				label1 = "Generator Outage";
			}
			if(k==lo){
				label1 = "Line Outage";
			}
			for(int j=0; j<Clusters.get(k).size(); j++){
				Clusters.get(k).get(j).label = label1;
			}
		}
		
	}

	// ############################################################################################################
	// Method for Computing Cluster Generator Outage Indicators
	private double[] analyzeGenOutage(ArrayList<ArrayList<SystemState>> Clusters, String[][] genPairs){
		double[] clusGenDiff = new double[Clusters.size()];
		
		//### Over each cluster
		for(int k=0; k<Clusters.size(); k++){
			//### Over each state
			for(int j=0; j<Clusters.get(k).size(); j++){
				double minStateDiff = 0;
				//### Over each bus pair
				for(int m=0; m<genPairs.length; m++){
					double tmpVoltDiff = Clusters.get(k).get(j).calcComplexVoltDrop(genPairs[m][0], genPairs[m][1]);
					if(m==0){
						minStateDiff = tmpVoltDiff;
					}
					else{
						if(tmpVoltDiff<minStateDiff){
							minStateDiff = tmpVoltDiff;
						}
					}
				}
				clusGenDiff[k] += minStateDiff/Clusters.get(k).size();
			}
		}
		return clusGenDiff;
	}

	// ############################################################################################################
	// Method for Computing Cluster Line Outage Indicators
	private double[] analyzeLineOutage(ArrayList<ArrayList<SystemState>> Clusters, String[][] linePairs){
		double[] clusGenDiff = new double[Clusters.size()];
		
		//### Over each cluster
		for(int k=0; k<Clusters.size(); k++){
			//### Over each state
			for(int j=0; j<Clusters.get(k).size(); j++){
				double maxStateDiff = 0;
				//### Over each bus pair
				for(int m=0; m<linePairs.length; m++){
					double tmpVoltDiff = Clusters.get(k).get(j).calcComplexVoltDrop(linePairs[m][0], linePairs[m][1]);
					if(m==0){
						maxStateDiff = tmpVoltDiff;
					}
					else{
						if(tmpVoltDiff>maxStateDiff){
							maxStateDiff = tmpVoltDiff;
						}
					}
				}
				clusGenDiff[k] += maxStateDiff/Clusters.get(k).size();
			}
		}
		return clusGenDiff;
	}
	
	// ############################################################################################################
	// Method for Calculating Complex Mean State-Average Voltage and Angle of a Cluster
	private double[][] calcComplexAverages(ArrayList<ArrayList<SystemState>> Clusters){
		double[][] avg = new double[Clusters.size()][2];
		
		//### Over each cluster
		for(int k=0; k<Clusters.size(); k++){
			avg[k][0]=0;
			avg[k][1]=0;
			//### Over each state
			for(int j=0; j<Clusters.get(k).size(); j++){
				//### Over Each Bus:
				double s1 = Clusters.get(k).size();
				double s2 = Clusters.get(k).get(j).buses.size();
				for(int m=0; m<s2; m++){
					avg[k][0] += Clusters.get(k).get(j).orgAngle(m)  /s1/s2;
					avg[k][1] += Clusters.get(k).get(j).orgVoltage(m)/s1/s2;
					
				}
			}
			
		}
		return avg;
	}

	// ############################################################################################################
	// Method for Calculating Normalized Mean State-Average Voltage and Angle of a Cluster
	private double[][] calcAverages(ArrayList<ArrayList<SystemState>> Clusters){
		double[][] avg = new double[Clusters.size()][2];
		
		//### Over each cluster
		for(int k=0; k<Clusters.size(); k++){
			avg[k][0]=0;
			avg[k][1]=0;
			//### Over each state
			for(int j=0; j<Clusters.get(k).size(); j++){
				//### Over Each Bus:
				double s1 = Clusters.get(k).size();
				double s2 = Clusters.get(k).get(j).buses.size();
				for(int m=0; m<s2; m++){
					avg[k][0] += Clusters.get(k).get(j).buses.get(m).angle  /s1/s2;
					avg[k][1] += Clusters.get(k).get(j).buses.get(m).voltage/s1/s2;
					
				}
			}
			
		}
		return avg;
	}
}
