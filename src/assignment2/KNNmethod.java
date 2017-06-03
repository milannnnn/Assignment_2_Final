package assignment2;

import java.util.*;

// Given a learn list, a test list, the number of neighbors to consider and the number of possible labels,
// the algorithm allows to classify each element of the test list (assigning a label) using the information gained 
// from the learn list.
// Along with the label, also the probability that the state belongs to the assigned cluster is estimated 

public class KNNmethod {
	
	// INPUTS:
		// k: number of neighbors to consider
		// testList: list of system states to classify
		// learnList: list of system states classified with k-mean
		// numLabels: number of clusters
	// OUTPUTS:
		// labelPros: list of label and probability for each state
	public static ArrayList<String[]> KNN(int k, ArrayList<SystemState> testList, ArrayList<SystemState> learnList, int numLabels ){
		// ArrayList of labels and probabilities for each SystemState
		ArrayList<String[]> labelPros = new ArrayList<String[]>();
		// loop through each SystemState
		for(SystemState tempSystemState : testList){
			// extract values
			double[] query = tempSystemState.values(); 
			// create list of K closest neighbors 
			ArrayList<Result> NeighborsList = createNeighborList(query, learnList, k);
			// find the most common label within the neighbors
			String[] labelPro = sortState( NeighborsList, query, numLabels);
			labelPros.add(labelPro);
		}
		return labelPros;
	}
	// ##################################################################################
	// euclidean distance
	 private static double EuDistance(double[] X1, double[] X2){
		double distance;
		double sum = 0;
		for(int i=0; i<X1.length; i++){
			sum = sum + Math.pow((X2[i] - X1[i]),2); 
		}
		distance = Math.sqrt(sum);
		return distance;
	}
	//##################################################################################
	// create neighborList
	 private static ArrayList<Result> createNeighborList(double[] query, ArrayList<SystemState> learnList, int k){
		ArrayList<Result> NeighborsList = new ArrayList<Result>();
		ArrayList<Result> resultList = new ArrayList<Result>();
		for(SystemState newSystemState : learnList){
			// calculate distance from the given values (query) and each element in the learn list
			double distance = EuDistance(query, newSystemState.values());
			// for each element of the learn list we save the label and the distance from query
			resultList.add(new Result(distance, newSystemState.label));
		}
		// sort the results from closest to further
		Collections.sort(resultList, new DistanceComparator());
		// take the closest k neighbors from resultList
		for(int i=0; i<k; i++){
			NeighborsList.add(resultList.get(i));
		}
		return NeighborsList;
	 }
	//##################################################################################
	// determine the label of the System State looking at the most common label in the neighborhood
	 private static String[]  sortState(ArrayList<Result> NeighborsList, double[] query, int numLabels){
		 String probability;
		 String label = "";
		 double[] labelArray = new double[numLabels];
		 String[] labelLabel = {"High Load","Low Load","Generator Outage","Line Outage"};
		 // count how many System States belong to each cluster
		 for(Result neighbor : NeighborsList){
			 label = neighbor.label;
			 for(int i=0; i<numLabels; i++){
				 if(label.equals(labelLabel[i])){
				 	labelArray[i]++;
				 }
			 }
		 }
		 // determine the most common cluster
		 int maxIndex = 0;
		 double maxValue = 0;
		 for(int i=0; i<labelArray.length; i++){
			 if(labelArray[i]>maxValue){
				 maxValue = labelArray[i];
				 maxIndex = i;
			 }
		 }
		 // calculate probability as the ratio between the number of elements of the main cluster of the neighborhood
		 // and the total dimension of the neighborhood
		 probability = 	String.format("%.2f ",100*maxValue/NeighborsList.size());
		 
		 // check if the solution is unique
		 int nonUniqueness = 0;
		 for(int i=0; i<labelArray.length; i++){
			 if(labelArray[i]==maxValue){
				nonUniqueness++;
			 }
		 }
		 if(nonUniqueness>1){
			 System.out.println("The solution is not unique! Change number of neighbors!");
		 }
		 
		 // create labeled SystemState
		 label = labelLabel[maxIndex];
		 String[] labelPro = {label,probability};
		 return labelPro;
	 }
}
