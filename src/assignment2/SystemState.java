package assignment2;

import java.util.ArrayList;

// ### Object Container Class - for Power System State Representation with Additional Data Manipulation Methods
public class SystemState {
	
	class BusObject{
		public String busID;
		public double voltage;
		public double angle;
		public BusObject(){
			busID 	= "";
			voltage = 0;
			angle 	= 0;
		}
	}
	
	public String label;
	public int time;
	
	//### Save Min and Max Values - to be able to revert to original values (if needed)
	public double[] minAngles, maxAngles, minVolts, maxVolts;

	public ArrayList<BusObject> buses = new ArrayList<BusObject>(); // Bus ID, Voltage, Angle
	
	// ###########################################################
	// Initialize a New System State
	public SystemState(){}
	public SystemState(String[] readData){
		// readData[0] - Name column
		// readData[1] - Time column
		// readData[2] - Value column
		// readData[3] - Bus ID column

		this.time = Integer.parseInt(readData[1]);
		BusObject tmpBus = new BusObject();
		tmpBus.busID = readData[3];
		if(isAngle(readData[0])){
			tmpBus.angle = Double.parseDouble(readData[2]);
		}
		else{
			tmpBus.voltage = Double.parseDouble(readData[2]);
		}
		this.buses.add(tmpBus);
	}
	
	// ###########################################################
	// Add New Data to Current State
	public void addData(String[] readData){
		// readData[0] - Name column
		// readData[1] - Time column
		// readData[2] - Value column
		// readData[3] - Bud ID column
		
		//### Search for a matching bus within the current state
		int pos = -1;
		for(int k=0; k<buses.size(); k++){
			if(buses.get(k).busID.equals(readData[3])){
				pos = k;
				break;
			}
		}
		
		//### If we have not found a matching bus, add a new one
		if(pos==-1){
			BusObject tmpBus = new BusObject();
			tmpBus.busID = readData[3];
			if(isAngle(readData[0])){
				tmpBus.angle = Double.parseDouble(readData[2]);
			}
			else{
				tmpBus.voltage = Double.parseDouble(readData[2]);
			}
			buses.add(tmpBus);
		}
		
		//### If we have found a matching bus, just add the volt / angle
		else{
			if(isAngle(readData[0])){
				buses.get(pos).angle   = Double.parseDouble(readData[2]);
			}
			else{
				buses.get(pos).voltage = Double.parseDouble(readData[2]);
			}
		}
	}
	
	// ###########################################################
	// Check if Measurement Is an Angle
	private boolean isAngle(String name){
		if(name.substring(name.length()-3, name.length()).equals("ANG")){
			return true;
		}
		return false;
	}
	
	// ###############################################################################################
	// Sorts Buses in Given Order of Appearance, and Eliminates all Buses Not Included in Sorting List
	public void sortBuses(String[] busOrder){
		ArrayList<BusObject> tmpBuses = new ArrayList<BusObject>();
		for(int k=0; k<busOrder.length; k++){
			for(int j=0; j<buses.size(); j++){
				if(busOrder[k].equals(buses.get(j).busID)){
					tmpBuses.add(buses.get(j));
					break;
				}
			}
		}
		buses.clear();
		buses.addAll(tmpBuses);
	}
	
	
	// ###########################################################
	// Normalize all State Variables with Min and Max Values
	public void normalize(double[] minAngles1, double[] maxAngles1, double[] minVolts1, double[] maxVolts1){
		// Memorize the Mins and Maxs before normalizing
		this.minAngles = minAngles1;
		this.maxAngles = maxAngles1;
		this.minVolts  = minVolts1;
		this.maxVolts  = maxVolts1;
		// Normalize the values
		for(int k=0; k<minAngles1.length; k++){
			if(maxAngles1[k]!=minAngles1[k]){
				buses.get(k).angle   = (buses.get(k).angle-minAngles1[k])/(maxAngles1[k]-minAngles1[k]);
			}
			else{
				buses.get(k).angle = 1;
			}
			
			if(maxAngles1[k]!=minAngles1[k]){
				buses.get(k).voltage = (buses.get(k).voltage-minVolts1[k])/(maxVolts1[k]-minVolts1[k]);
			}
			else{
				buses.get(k).voltage = 1;
			}
		}
	}
	
	// ###########################################################
	// Returns the Vector of State Variables (angles and voltages)
	public double[] values(){
		double[] vals = new double[buses.size()*2];
		for(int k=0; k<buses.size(); k++){
			vals[2*k]   = buses.get(k).angle;
			vals[2*k+1] = buses.get(k).voltage;
		}
		return vals;
	}
	
	// ###########################################################
	// Prints the Vector of State Variables (angles and voltages)
	public void printValues(){
		for(int k=0; k<buses.size(); k++){
			System.out.print(buses.get(k).angle  +"\t");
			System.out.print(buses.get(k).voltage);
			if(k!=(buses.size()-1)){
				System.out.print("\t");
			}
		}
		System.out.print("\n");
	}
	
	// ###############################################################
	// Returns the CSV String of State Variables (angles and voltages)
	public String stringValues(){
		String s = "";
		for(int k=0; k<buses.size(); k++){
			s += Double.toString(buses.get(k).angle)+",";
			s += Double.toString(buses.get(k).voltage);
			if(k!=(buses.size()-1)){
				System.out.print("\t");
				s += ",";
			}
		}
		s += "\n";
		return s;
	}

	// ###############################################################
	// Calculates the Complex Voltage Drop Between 2 Buses
	public double calcComplexVoltDrop(String id1, String id2){
		int q1=-1, q2=-1;
		for(int k=0; k<buses.size(); k++){
			if(buses.get(k).busID.equals(id1)){
				q1 = k;
			}
			if(buses.get(k).busID.equals(id2)){
				q2 = k;
			}
		}
		if(q1>=0 && q2>=0){
			double v1, v2, a1, a2, re1, re2, im1, im2, dist;
			
			// Revert Back to Original Values (so we can calculate COMPLEX volt drop)
			v1 = buses.get(q1).voltage*(maxVolts[q1]-minVolts[q1])+minVolts[q1];
			v2 = buses.get(q2).voltage*(maxVolts[q2]-minVolts[q2])+minVolts[q2];
			a1 = buses.get(q1).angle*(maxAngles[q1]-minAngles[q1])+minAngles[q1];
			a2 = buses.get(q2).angle*(maxAngles[q2]-minAngles[q2])+minAngles[q2];
						
			re1 = v1*Math.cos(a1*Math.PI/180);
			re2 = v2*Math.cos(a2*Math.PI/180);
			im1 = v1*Math.sin(a1*Math.PI/180);
			im2 = v2*Math.sin(a2*Math.PI/180);
			
			dist = Math.sqrt((re1-re2)*(re1-re2) + (im1-im2)*(im1-im2));
			return dist;
		}
		else{
			return -1;
		}
	}
	
	// ###############################################################
	// Renormalize the Input Data (return to original values)
	public void reNormalize(){
		for(int k=0; k<buses.size(); k++){
			buses.get(k).voltage = buses.get(k).voltage*(maxVolts[k] -minVolts[k] )+minVolts[k];
			buses.get(k).angle   = buses.get(k).angle  *(maxAngles[k]-minAngles[k])+minAngles[k];		
		}
	}
	
	// ###############################################################
	// Return the Original Voltage (unnormalized)
	public double orgVoltage(int k){
		double volt = buses.get(k).voltage*(maxVolts[k]-minVolts[k])+minVolts[k];
		return volt;
	}
	
	// ###############################################################
	// Return the Original Angle (unnormalized)
	public double orgAngle(int k){
		double angle = buses.get(k).angle*(maxAngles[k]-minAngles[k])+minAngles[k];
		return angle;
	}
	
	// ###############################################################
	// Merge Time and State Values to a String Array
	public String[] stringArrayValues(){
		String[] s = new String[buses.size()*2+1];
		s[0] = "" + time ;
		for(int k=1; k<s.length; k += 2){
			s[k] = String.format( "%.2f ", buses.get(k/2).angle);
			s[k+1]= String.format("%.2f ",buses.get(k/2).voltage);
		}
		return s;
	}
}
