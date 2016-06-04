package tryakash;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

 

public class sqlTmp
{
	public static Connection c = null;
	public  static Statement stmt = null;
  public static void main( String args[] )
  {
	  createDBase();
	  createRecord("/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/data/Riverside2.csv");
	  //deleteRecords();
	  viewRecords();
//	  try {
//		loadCSV("/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/data/Riverside2.csv");
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
    
    
    //System.out.println("Opened database successfully");
  }
  
  public static void createDBase()
  {
	  String dbName = "/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/test.db";
	  File file = new File (dbName);

	  if(file.exists()) //here's how to check
	     {
	         System.out.println("This database name already exists. Not creating Again");
	     }
	     else{

	          
	    
	  
	  
	  
	    try {
	    	Class.forName("org.sqlite.JDBC");
	        c = DriverManager.getConnection("jdbc:sqlite:/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/test.db");
	    
	        stmt = c.createStatement();
	        
	        DatabaseMetaData md = c.getMetaData();
	        ResultSet rs = md.getTables(null, null, "%", null);
	        while (rs.next()) {
	          System.out.println("table"+rs.getString(3));
	          //System.out.println("akash");
	        }
	        
	        String sql = "CREATE TABLE IF NOT EXISTS RFIDTABLE " +
                    "(ASSET INT PRIMARY KEY     NOT NULL," +
                    " EPCTAG           TEXT    NOT NULL, " + 
                    " LATITUDE            REAL     NOT NULL, " + 
                    " LONGITUDE            REAL     NOT NULL, " + 
                    " SIGN        CHAR(50)) "; 
	        stmt.executeUpdate(sql);
	        stmt.close();
	        c.close();
	        System.out.println(" table created successfully");

	        
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      
	     // System.exit(0);
	    }
	     }
  }
  
  public static void createRecord(String fileName)
  {
	  try {
	        Class.forName("org.sqlite.JDBC");
	        c = DriverManager.getConnection("jdbc:sqlite:/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/test.db");
	        c.setAutoCommit(false);
	       // System.out.println("Opened database successfully");

	        stmt = c.createStatement();
	        String sql = null;
	       /*  sql = "INSERT OR REPLACE INTO RFIDTABLE (ID,NAME,AGE,ADDRESS,SALARY) " +
	                    "VALUES (( SELECT ID FROM RFIDTABLE WHERE ID = 1), 'Akash', 32, 'California', 210000.00 );";
	        //System.out.println("yeah");
	        stmt.executeUpdate(sql);
	        
	         */
	        
	        
	        
	        String[] rows = loadStrings(fileName);
	        
	      for (String row : rows) {
	          // Reads country name and population density value from CSV row
	          // NOTE: Splitting on just a comma is not a great idea here, because
	          // the csv file might have commas in their entries, as this one
	          // does.
	          // We do a smarter thing in ParseFeed, but for simplicity,
	          // we just use a comma here, and ignore the fact that the first
	          // field is split.//7 coordinates //2 - asset
	      	
	          String[] columns = row.split(",");
	          if (columns.length == 7 && !columns[6].equals("GPSLocation")) {
	          	 
	          	String[] doubleRiversideCoordinates = columns[6].split(":");
	          	RFIDObj tmpObj = new RFIDObj(Double.parseDouble(doubleRiversideCoordinates[0]), Double.parseDouble(doubleRiversideCoordinates[1]), columns[5], columns[2]+" ** "+columns[3], Integer.parseInt(columns[1]));
	          	
	          	
	           

	            //  lifeExpMap.put(columns[1],tmpObj); //asset no
	             // assetEPC.put(tmpObj.epcTag, columns[1]);
	          	//System.out.println(Double.parseDouble(doubleRiversideCoordinates[0])+ Double.parseDouble(doubleRiversideCoordinates[1])+ columns[5]+ columns[2]+" ** "+columns[3]+ Integer.parseInt(columns[1]));
	          	
//	          	 sql = "INSERT OR IGNORE INTO RFIDTABLE (ASSETID,NAME,AGE,ADDRESS,SALARY) " +
//	   	              "VALUES (2, 'Akash', 25, 'Texas', 15000.00 );"; 
	          //	System.out.println(Integer.parseInt(columns[1])+columns[5]+Double.parseDouble(doubleRiversideCoordinates[0])+Double.parseDouble(doubleRiversideCoordinates[1])+columns[3]);
	          	
//	          	 
	          	//sql = "INSERT OR IGNORE INTO RFIDTABLE (ASSET,EPCTAG,LATITUDE,LONGITUDE,SIGN)"+ 
	          		//	"VALUES("+Integer.parseInt(columns[1])+columns[5]+Double.parseDouble(doubleRiversideCoordinates[0])+Double.parseDouble(doubleRiversideCoordinates[1])+columns[3]+");";
	          	sql = String.format("INSERT OR IGNORE INTO RFIDTABLE (ASSET,EPCTAG,LATITUDE,LONGITUDE,SIGN)	VALUES(%d,'%s',%f,%f,'%s' );",Integer.parseInt(columns[1]),columns[5], Double.parseDouble(doubleRiversideCoordinates[0]), Double.parseDouble(doubleRiversideCoordinates[1]),columns[3]);
	   	        stmt.executeUpdate(sql);	
	              
	          }
	         }
	      
	      
	       
	        
	        

	       

//	        sql = "INSERT OR IGNORE INTO RFIDTABLE (ASSETID,NAME,AGE,ADDRESS,SALARY) " +
//	              "VALUES (3, 'John', 23, 'NYC', 20000.00 );"; 
//	        stmt.executeUpdate(sql);
//
//	        sql = "INSERT OR IGNORE INTO RFIDTABLE (ASSETID,NAME,AGE,ADDRESS,SALARY) " +
//	              "VALUES (4, 'Doe', 25, 'Bombay ', 65000.00 );"; 
//	        stmt.executeUpdate(sql);

	        stmt.close();
	        c.commit();
	        c.close();
	        System.out.println("Records created successfully");
	      } catch ( Exception e ) {
	        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	        //System.exit(0);
	      }
  }
  
  public static void viewRecords(){
  try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/test.db");
      c.setAutoCommit(false);
     // System.out.println("Opened database successfully");

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM RFIDTABLE;" );
      while ( rs.next() ) {
         int id = rs.getInt("ASSET");
         String  name = rs.getString("EPCTAG");
         double latitude  = rs.getInt("LATITUDE");
         double longitude  = rs.getInt("LONGITUDE");
         String  sign = rs.getString("SIGN");
        // float salary = rs.getFloat("salary");
         System.out.println( "ID = " + id );
         System.out.println( "NAME = " + name );
         System.out.println( "latitude = " + latitude );
         System.out.println( "longitude = " + longitude );
         System.out.println( "sign = " + sign );
         System.out.println("Displayed records ");
      }
      rs.close();
      stmt.close();
      c.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      //System.exit(0);
    }
  }
  
  
  public static void deleteRecords()
  {
 
  
  try {
	  String delString = "DELETE FROM RFIDTABLE;";
	  Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:/Users/akash/Documents/eclipseWorkspace/RFIDLabReaderFinal/test.db");
      c.setAutoCommit(false);
     // System.out.println("Opened database successfully");

      stmt = c.createStatement();
      stmt.executeUpdate(delString);
      //rs.close();
      //stmt.executeUpdate(sql);

      stmt.close();
      c.commit();
      c.close();
	
} catch ( Exception e ) {
    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    //System.exit(0);
  } 
  }
  
  
  
  
  public static void loadCSV(String fileName) throws FileNotFoundException {
       

      
  }

private static String[] loadStrings(String fileName) throws FileNotFoundException {
	// TODO Auto-generated method stub
	
	Scanner sc = new Scanner(new File(fileName));
	List<String> lines = new ArrayList<String>();
	while (sc.hasNextLine()) {
	  lines.add(sc.nextLine());
	}

	String[] arr = lines.toArray(new String[0]);
	return arr;
}

  
  
  
  
}



//class JDBCExample{
//	public static void main(String[] args) {
//		//set connection
//		
//		try {
//			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ecen689db","akashmysql","akash123");
//			System.out.println("Connection done");
//			Statement st = connection.createStatement();
//			ResultSet rs  = st.executeQuery("SELECT * FROM salespeople");
//			while(rs.next())
//			{
//				System.out.println(rs.getInt("id")+" "+ rs.getString("s_name")+" "+ rs.getString("s_city")+" "+ rs.getFloat("comm"));
//			}
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		//create startement
//		//ex query
//		//result set
//		
//	}
//}






//
////STEP 1. Import required packages
//import java.sql.*;
//
//public class JDBCExample {
// // JDBC driver name and database URL
// static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
// static final String DB_URL = "jdbc:mysql://localhost/";
//
// //  Database credentials
// static final String USER = "root@localhost";
// static final String PASS = "root123";
// 
// public static void main(String[] args) {
// Connection conn = null;
// Statement stmt = null;
// try{
//    //STEP 2: Register JDBC driver
//    Class.forName("com.mysql.jdbc.Driver");
//
//    //STEP 3: Open a connection
//    System.out.println("Connecting to database...");
//    conn = DriverManager.getConnection(DB_URL, USER, PASS);
//
//    //STEP 4: Execute a query
//    System.out.println("Creating database...");
//    stmt = conn.createStatement();
//    
//    String sql = "CREATE DATABASE STUDENTS";
//    stmt.executeUpdate(sql);
//    System.out.println("Database created successfully...");
// }catch(SQLException se){
//    //Handle errors for JDBC
//    se.printStackTrace();
// }catch(Exception e){
//    //Handle errors for Class.forName
//    e.printStackTrace();
// }finally{
//    //finally block used to close resources
//    try{
//       if(stmt!=null)
//          stmt.close();
//    }catch(SQLException se2){
//    }// nothing we can do
//    try{
//       if(conn!=null)
//          conn.close();
//    }catch(SQLException se){
//       se.printStackTrace();
//    }//end finally try
// }//end try
// System.out.println("Goodbye!");
//}//end main
//}//end JDBCExample
