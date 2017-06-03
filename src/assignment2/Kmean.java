package assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

// Given a set of measurements, the algorithm separates them in the required number of clusters applying the k-mean method
// The first centroids can be determined either using the fory method or the RPM
// up-scaling can be applied (a larger number of clusters is used and then reduced to the required number)

public class Kmean {
	// SystemList is an ArrayList, where each element is a SystemState
	// tol is the tolerance when checking the distance between centroids of two different iterations
	// maxiter is the maximum  allowed number of iterations
	public static ArrayList<SystemState> SystemList;
	public static double tol ;
	public static int maxIter ;
	// ##################################################################################
	// constructor
	public Kmean(ArrayList<SystemState> SystemList, double tol, int maxIter){
		Kmean.SystemList = SystemList;
		Kmean.tol = tol;
		Kmean.maxIter = maxIter;
	}
	// ##################################################################################
	// INPUTS:
		// k: the number of clusters to use
		// kOrg: the number of cluters to return as output
	// when up-scaling is applied we run the k-mean algorithm considering k clusters and 
	// at the end we down-scale the clusters to kOrg
	public ArrayList<ArrayList<SystemState>> kMeanClustering(int k, int kOrg, String initMethod) {
			
			// create an arrayList of clusters, which are ArrayList of SystemState
			ArrayList<ArrayList<SystemState>> Clusters = new ArrayList<ArrayList<SystemState>>();
			
			for(int i=0; i<k;i++){
				Clusters.add(new ArrayList<SystemState>());
			}

			// create initial centroid using either forgy or RPM method
			ArrayList<double[]> Centroid = new ArrayList<double[]>();
			if(initMethod.equals("RPM")){
				Centroid =  RPM(k);
			}else{
				Centroid =  forgy(k);
			}
			// boolean value used in while loop
			boolean check = true;
			// to count the iteration
			int keepTime = 0;
			
			// start the loop
			while(check){
				keepTime++;
				for(int ii= 0; ii<SystemList.size(); ii++){
					// assigned cluster
					int assignedClusterIndex = 0; 
					double minDistance = 1e9;
					// extract values of ii-th SystemState
					double[] values = SystemList.get(ii).values();
					// ## in this loop we determine to which centroid the studied SystemState  is closer
					// i is the number of the cluster
					for(int i=0; i<k; i++ ){
						// calculate distance of each value from the centroids
						double newDistance = EuDistance(values,Centroid.get(i));
						if(newDistance < minDistance){
							minDistance = newDistance;
							 assignedClusterIndex = i;
						}
					}
					// place the object in the closest cluster
					Clusters.get(assignedClusterIndex).add(SystemList.get(ii));
				}
				
				// deal with empty clusters
				Clusters = fillIt( Clusters, k);
				
				// calculate the new centroids from given clusters
				ArrayList<double[]> newCentroid = calCentroids(Clusters);
				// set check to false to break the loop 
				check = false;
				// calculate distance old and new centroid for each cluster
				for(int i=0; i<k; i++){
					double delta = EuDistance( Centroid.get(i), newCentroid.get(i));
					// if at least one distance is bigger than the tolerance and 
					// the number of iterations is smaller than the max value, check is set to true and the while loop goes on
					if(delta > tol && keepTime < maxIter){
						check=true;
						// clear clusters
						clear(Clusters);
					}
				}
				// new centroids become the centroids used in the next iteration
				Centroid= newCentroid;
			}
			// the clusters are down-scaled to the requested number (kOrg)
			Clusters = downscaleClusters(Clusters, kOrg);
			if(kOrg < k){
			System.out.println("Up-scaling successfully applied!");
			}
			System.out.println("Total number of iterations: " + keepTime);
			return Clusters;
		}
	// ##################################################################################
	// deal with empty cluster
	public ArrayList<ArrayList<SystemState>> fillIt(ArrayList<ArrayList<SystemState>> Clusters, int k){
		// deal with empty clusters
		Random rand = new Random();
		for(int i=0; i<Clusters.size();i++){
			if(Clusters.get(i).isEmpty()){
				System.out.println("fill me up");
				for(int ii=0; ii<Clusters.size();ii++){
					if(Clusters.get(ii).size()>1){
						int elementIndex = rand.nextInt(Clusters.get(ii).size());
						Clusters.get(i).add(Clusters.get(ii).get(elementIndex));
						// remove from old cluster
						Clusters.get(ii).remove(elementIndex);
						break;
					}
				}
			}
		}
		return Clusters;
	}
	// ##################################################################################
	// clear list
	public void clear(ArrayList<ArrayList<SystemState>> Clusters){
		for(int i=0; i<Clusters.size();i++){
			Clusters.get(i).clear();
		}
	}
	// ##################################################################################
    // forgy method
	public ArrayList<double[]> forgy(int k){
		// create a random number to assign centroid
		ArrayList<Integer> bowl = new ArrayList<Integer>();
		// fill the bowl with number from 0 to SystemList.size()
		for(int i=0; i<SystemList.size();i++){
			bowl.add(i);
		}
		ArrayList<double[]> Centroid = new ArrayList<double[]>();
		// extract k numbers from the bowl
		for(int i=0; i<k ;i++){
			Random rand = new Random();
			// determine random position to extract from the bowl
			int  ranNumindex = rand.nextInt(bowl.size());
			// take the number in that position from the bowl
			int ranNum = bowl.get(ranNumindex);
			// add the ranNum number element to centroid
			double[] newValueSet = SystemList.get(ranNum).values();
			Centroid.add(newValueSet);
			// remove the number from the bowl
			bowl.remove(ranNumindex);
		}
		System.out.println("Successfully initialized with forgy method!");
		return Centroid;
	}
	// ##################################################################################
    // random partition method
	public ArrayList<double[]> RPM(int k){
		ArrayList<double[]> Centroid = new ArrayList<double[]>();
		ArrayList<ArrayList<SystemState>> Clusters = new ArrayList<ArrayList<SystemState>>();
		for(int i=0; i<k;i++){
			Clusters.add(new ArrayList<SystemState>());
		}
		// create a random number to assign centroid
		ArrayList<Integer> bowl = new ArrayList<Integer>();
		// fill the bowl with number from 0 to SystemList.size()
		for(int i=0; i<SystemList.size();i++){
			bowl.add(i);
		}
		// assign each object to a cluster randomly
		Random rand = new Random();
		for(int i=0; i<SystemList.size();i++){
			int  ranNumindex = rand.nextInt(bowl.size());
			int  ranNClusindex = 0 ;
			for(int ii=0; ii<k; ii++){
				if(i <= (SystemList.size()/(k) + SystemList.size()/(k)*ii)){
					ranNClusindex=ii;
					break;
				}
			}
			// insert the ranNumindex element from SystemList to the ranNClusindex Cluster
			Clusters.get(ranNClusindex).add(SystemList.get(ranNumindex));
			bowl.remove(ranNumindex);
		}
		
		Centroid = calCentroids(Clusters);
		System.out.println("Successfully initialized with random partition method!");
		return Centroid;
	}
	// ##################################################################################
	// calculate new centroids
	private static  ArrayList<double[]> calCentroids(ArrayList<ArrayList<SystemState>> Clusters){
		 ArrayList<double[]> Centroid = new ArrayList<double[]>();
		for(int i=0; i< Clusters.size(); i++){
			if(Clusters.get(i).isEmpty()){
				System.out.println("cluster " + (1+i) + " is empty");
			}else{
				Centroid.add(MeanOneCluster(Clusters.get(i)));
			}
		}
		return Centroid;
	}
	// ##################################################################################
	// calculate euclidian distance
	public static double EuDistance(double[] X1, double[] X2){
		double distance;
		double sum = 0;
		for(int i=0; i<X1.length; i++){
			sum = sum + Math.pow((X2[i] - X1[i]),2); 
		}
		distance = Math.sqrt(sum);
		return distance;
	}
	// ##################################################################################
	// calculate the average value of the cluster 
	private static double[] MeanOneCluster(ArrayList<SystemState> clusterElements){
		double[] mean = new double[clusterElements.get(0).values().length];
		for(int i=0; i<clusterElements.size();i++){
			double[] newValues = clusterElements.get(i).values();
			mean = ArraySum(newValues,mean,'+');
		}
		for(int i=0; i<mean.length;i++){
			mean[i] = mean[i]/clusterElements.size();
		}
		return mean;
	}
	// ##################################################################################
	// perform the sum between two arrays
	private static double[] ArraySum(double[] A1, double[] A2, char sign){
		if(sign=='-'){
			double[] result = new double[A1.length]; 
			for(int i=0; i<A1.length;i++){
				result[i] = A1[i] - A2[i];
			}
			return result;
		}else{
			double[] result = new double[A1.length]; 
			for(int i=0; i<A1.length;i++){
				result[i] = A1[i] + A2[i];
			}
			return result;
		}
	}
	// ################################################################################################################
	// Down scale clusters - Start Merging Closest Clusters together until we reach the desired number of clusters (n)
	private static ArrayList<ArrayList<SystemState>> downscaleClusters(ArrayList<ArrayList<SystemState>> clusters, int n){
		ArrayList<ArrayList<SystemState>> tmpClusters = new ArrayList<ArrayList<SystemState>>();
		tmpClusters.addAll(clusters);
		ArrayList<double[]> centroids = calCentroids(tmpClusters);
		
		//### Until we reach the desired size, start merging closest clusters
		while(tmpClusters.size()>n){
			double minDist = 0, tmpDist = 0;
			int q1=0, q2=0;
			//### Find two closest clusters (q1 and q2)
			for(int k=0; k<tmpClusters.size(); k++){
				for(int j=k+1; j<tmpClusters.size(); j++){
					tmpDist = EuDistance(centroids.get(k), centroids.get(j));
					if(k==0 && j==1){
						minDist = tmpDist;
						q1 = k;
						q2 = j;
					}
					else{
						if(tmpDist<minDist){
							minDist = tmpDist;
							q1 = k;
							q2 = j;
						}
					}
				}
			}
			//### Add all states from q2 to q1
			tmpClusters.get(q1).addAll(tmpClusters.get(q2));
			//### Kill cluster q2
			tmpClusters.remove(q2);
			//### Recalculate New Centroids
			centroids = calCentroids(tmpClusters);
		}
		return tmpClusters;
	}
	// ##################################################################################
	// export csv
	public static void CSV(ArrayList<ArrayList<SystemState>> Clusters , int k) {
        PrintWriter pw;
		try {
			ArrayList<PrintWriter> pwArray = new ArrayList<PrintWriter>();
			for(int i=0; i < k ;i++){
				String name = "cluster_" + Clusters.get(i).get(0).label + ".csv";
				pw = new PrintWriter(new File(name));
				pwArray.add(pw);
			}
			
			for(int ii=0; ii<Clusters.size(); ii++){
				StringBuilder sb = new StringBuilder();
				String header = "time,";
				for(int j=0; j < Clusters.get(ii).get(0).values().length; j++){
					int mod = j % 2;
					if(j<Clusters.get(ii).get(0).values().length-1){
						if(mod==0){
							header += "ANG_" + ((j/2)+1) + ",";
						}else{
							header += "VOL_" + (j/2+1) + ",";
						}
					}else{
						if(mod==0){
							header += "ANG_" + ((j/2)+1) + ",";
						}else{
							header += "VOL_" + (j/2+1) + "\n";
						}
					}
				}
				sb.append(header);
				for(int i=0; i<Clusters.get(ii).size(); i++){
					String line = Clusters.get(ii).get(i).time + ","  + Clusters.get(ii).get(i).stringValues() ;
					sb.append(line);
				}
				pw = pwArray.get(ii);
				pw.write(sb.toString());
		        pw.close();
			}

	        System.out.println("CSV created!");
			
		} catch (FileNotFoundException e) {
			
			System.out.println("Problem occured while writing the CSV file, please check writing permissions and try again!!!");
			FillStates.terminateProgram();
			// e.printStackTrace();
		}
    }
}
