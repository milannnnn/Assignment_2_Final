package assignment2;

import java.sql.*;
//import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// It connects to a DataBase called "subtables", 
// It reads the specified table's columns and returns the results as a matrix of String
// it allows to print out the read information
// It handles SQL and general exceptions

public class SQLprinter {
	// remember to import driver library ----> use bildbath
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/";
	private static String USER;
	private static String PASS;
	// define the name of the database
	public static String dataBaseName = "subtables";
	public static Connection conn;
	public static Statement stmt;
	// ############################################################################################################
	// constructor to initialize the database
	// Inputs:
		// USER: username of SQL database
		// PASS: password of SQL 
	public SQLprinter(String USER,String PASS){
		// JDBC driver name and database URL
		// Database credentials
		SQLprinter.USER = USER;
		SQLprinter.PASS = PASS;
		conn = null;
		stmt = null;
		try{
			// Register JDBC driver
			Class.forName(JDBC_DRIVER);
			// Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			// remove restriction to be able to look for names too, instead of IDs only
			stmt.executeUpdate("SET SQL_SAFE_UPDATES = 0");
			
		}
		catch(SQLException se){
			//Handle errors for JDBC
//			se.printStackTrace();
			System.out.println("\nSQL error! Please check the SQL server, Driver, URL, Connection, Username and Password, and try again!!!");
			kill();
		}
		catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			kill();
		}
	}
	// ############################################################################################################
	// default constructor with default user and password
	public SQLprinter(){
		this("root","root");
	}
	// ############################################################################################################
	// method to read a table
	// it reads the specified columns (columnNames) of the specified table (tableName) and returns the result as matrix of String
	// INPUTS:
		// tableName: name of the table
		// columnNames: name of the columns as String Array
	// OUTPUT:
		// tabDataRes: matrix of String with read data
	public String[][] readTable(String tableName, String[] columnNames){
		String[][] tabDataRes = null;
		try{
			// Connect to the created database 
			conn = DriverManager.getConnection(DB_URL + dataBaseName, USER, PASS);
			// use the database
			String sql = "use " + dataBaseName;
			stmt.executeUpdate(sql);
			// count the number of rows in the table
			String query = "SELECT COUNT(*) FROM " + tableName;
		    ResultSet countRes = stmt.executeQuery(query);
		    countRes.next();
		    int tabLength = countRes.getInt(1);
		    // read the table content
		    query = "SELECT * FROM " + tableName;
		    ResultSet sql_ResultSet = stmt.executeQuery(query);
		    int k = 0;
		    tabDataRes = new String[tabLength][columnNames.length];
		    while (sql_ResultSet.next()) {
		    	for(int i=0; i < columnNames.length; i++){
			    	String columnName = columnNames[i];
			    	tabDataRes[k][i] = sql_ResultSet.getString(columnName);
		    	    }
		    	k++;
		    }
		    return tabDataRes;
		}
		catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
			System.out.println("\nSQL error! Please check the SQL server, Driver, URL, Connection, Username and Password, and try again!!!");
			kill();
		}
		catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
			kill();
		}
		return tabDataRes;	
	}
	// ############################################################################################################
	// print table
	public void printRes(String[][] tabDataRes, String[] columnNames){
		for(int k=0; k<tabDataRes[0].length;k++){
			System.out.printf("%5s",columnNames[k]);
		}
		System.out.println();
		for(int i=0; i< tabDataRes.length; i++){
			for(int k=0; k<tabDataRes[i].length;k++){
				System.out.printf("%10s",tabDataRes[i][k] + "  ");
			}
			System.out.println();
		}
	}
	// ############################################################################################################
	// close connection with database
	public void exit(){
		try {
			conn.close();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("\nError while closing SQL connection! Please check the SQL server, Driver, URL, Connection, Username and Password, and try again!!!");
			kill();
		}
	}
	// ############################################################################################################
	// method to kill the program
	@SuppressWarnings("deprecation")
	public static void kill(){
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

