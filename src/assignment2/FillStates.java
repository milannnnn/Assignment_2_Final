package assignment2;

import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// Class for 

public class FillStates {
	
	// #######################################################################################################
	// Method to Extract (from SQL), Sort and Normalize System State Data (returns ArrayList of System States)  
	public ArrayList<SystemState> getStates(String user, String pass, String tableName){
		
		//### Read Data from SQL Table
		SQLprinter newPrinter = new SQLprinter(user,pass);
		String[] colName = {"name","time","value","sub_rdfid"};
		String[][] resData = newPrinter.readTable(tableName, colName);
		newPrinter.exit();
		
		//### Push the Data into SystemState objects
		ArrayList<SystemState> allStates = new ArrayList<SystemState>();
		for(int k=0; k<resData.length; k++){
			int tmpTime = Integer.parseInt(resData[k][1]);
			int pos = -1;
			// Check if we already have a state for given time
			for(int j=0; j<allStates.size(); j++){
				if(allStates.get(j).time == tmpTime){
					pos = j;
					break;
				}
			}
			// If we don't have the state for given time, create a new one
			if(pos == -1){
				allStates.add(new SystemState(resData[k]));
			}
			// If we have the state for given time, just update the values
			else{
				allStates.get(pos).addData(resData[k]);
			}
		}
		
		//### Check the Number of States Found:
		if(allStates.size()==0){
			System.out.println("No States Found - Please Check Input Database!!!");
			terminateProgram();
		}
		
		//### Check if All States have the Same Length:
		int k1=0;
		for(int k=0; k<allStates.size(); k++){
			if(k==0){
				k1 = allStates.get(k).buses.size();
			}
			else{
				if(k1!=allStates.get(k).buses.size()){
					System.out.println("Inconsistent State Dimensions (measurements)!!!");
					System.out.println("Please correct input data and try again!!!");
					terminateProgram();
				}
			}
		}
		
		//### Sort the Buses in the Same Order:
		String[] busOrder = new String[allStates.get(0).buses.size()];
		for(int k=0; k<allStates.get(0).buses.size(); k++){
			busOrder[k] = allStates.get(0).buses.get(k).busID;
		}
		for(int k=1; k<allStates.size(); k++){
			allStates.get(k).sortBuses(busOrder);
		}
		
		//### Normalize the Data:
		
		// Find Mins and Maxs
		double[] minVolts  = new double[allStates.get(0).buses.size()];
		double[] maxVolts  = new double[allStates.get(0).buses.size()];
		double[] minAngles = new double[allStates.get(0).buses.size()];
		double[] maxAngles = new double[allStates.get(0).buses.size()];
		for(int k=0; k<allStates.size(); k++){
			if(k==0){
				for(int j=0; j<allStates.get(k).buses.size(); j++){
					minAngles[j] = allStates.get(k).buses.get(j).angle;
					maxAngles[j] = allStates.get(k).buses.get(j).angle;
					minVolts[j]  = allStates.get(k).buses.get(j).voltage;
					maxVolts[j]  = allStates.get(k).buses.get(j).voltage;
				}
			}
			else{
				for(int j=0; j<allStates.get(k).buses.size(); j++){
					minAngles[j] = java.lang.Math.min(minAngles[j], allStates.get(k).buses.get(j).angle);
					maxAngles[j] = java.lang.Math.max(maxAngles[j], allStates.get(k).buses.get(j).angle);
					minVolts[j]  = java.lang.Math.min(minVolts[j],  allStates.get(k).buses.get(j).voltage);
					maxVolts[j]  = java.lang.Math.max(maxVolts[j],  allStates.get(k).buses.get(j).voltage);
				}
			}
		}

		//### Normalize All States
		for(int k=0; k<allStates.size(); k++){
			allStates.get(k).normalize(minAngles, maxAngles, minVolts, maxVolts);
		}
		
		return allStates;
	}
	
	@SuppressWarnings("deprecation")
	public static void terminateProgram(){
		try {
			Clip clip = AudioSystem.getClip();
		    AudioInputStream inputStream = AudioSystem.getAudioInputStream(Gui.class.getResource("/doh.wav"));
		    clip.open(inputStream);
		    clip.start(); 
		} 
		catch (Exception e) {
			System.err.println(e.getMessage());
		}	
		System.out.println("\n=> Program Intentionally Terminated (Kill it before it lays eggs!!!)");
		Thread.currentThread().stop();
	}
}
