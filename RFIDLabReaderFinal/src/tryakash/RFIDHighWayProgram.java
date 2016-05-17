package tryakash;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Google.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.llrp.ltk.generated.parameters.TagReportData;
import org.apache.log4j.chainsaw.Main;
import org.llrp.ltk.generated.enumerations.*;
import org.llrp.ltk.generated.interfaces.*;
import org.llrp.ltk.generated.messages.*;
import org.llrp.ltk.generated.parameters.*;
import org.llrp.ltk.net.*;
import org.llrp.ltk.types.*;

import com.jogamp.common.util.RunnableExecutor.CurrentThreadExecutor;
import com.jogamp.opengl.util.av.AudioSink.AudioFormat;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;



 
import controlP5.*;
/**
 * Visualizes life expectancy in different countries.
 * 
 * It loads the country shapes from a GeoJSON file via a data reader, and loads
 * the population density values from another CSV file (provided by the World
 * Bank). The data value is encoded to transparency via a simplistic linear
 * mapping.
 */
public class RFIDHighWayProgram extends PApplet implements LLRPEndpoint, Observer {
	
//	public static HashMap<String, Integer> epcCountMap = new HashMap<String, Integer>();
    
	
	//RFID

    private LLRPConnection reader;
    private static final int TIMEOUT_MS = 10000;
    private static final int ROSPEC_ID = 123;
    public String tagsEPCRead;
    public static HashMap<String, Integer> epcCount = new HashMap<String, Integer>();
    public static HashMap<String, String> assetEPC = new HashMap<String, String>();
    static String GPSLat, GPSLong;
   // public static int gpsStatus = 0;
      
    ControlP5 controlP5;
    boolean definedListBox = false;
     
    ListBox lbCSV;
    	 
    Button startReader;
    Button stopReader;
    Textlabel status;
    Button exitButton;
    Button GPSButton;
	
    SimplePointMarker pM;
    SimplePointMarker gpsPM;
    Location location;
    Location gpsLocation;
    
    PImage backgroundMap;
    
    PGraphics pg;
    PImage buttonImage;
	
    PImage signCurrent;
    PImage signNext;
    
    PImage signStop, signYield, signStreet;
    Rectangle rectCurrent, rectNext;
    
	//RFID
    //distance matrix
    public static double distanceMatrix[][];
    public static HashMap<String, RFIDObj> globalRFDataDump = new HashMap<String, RFIDObj>();
	
	
	void run2(String a )
	{
		
	}
	
	
	
	
	
	public static void main(String args[]) {
	    PApplet.main(new String[] { "--present", "tryakash.RFIDHighWayProgram" });
		System.out.println("RFID Program");		
		try{
			
		//HelloJavaLtk app = new HelloJavaLtk();
		//System.out.println("Starting reader.");
      // run("169.254.1.1");        
       //Thread.sleep(3000);
      //  System.out.println("Stopping reader.");
       // app.stop();
        //epcCountMap = app.epcCount;
      /*  for (Entry<String, Integer> entry : epcCountMap.entrySet()) {
      	  String key = entry.getKey();
      	  int value = entry.getValue();
      	  System.out.println("Key is "+key+" and Value is "+value);
      	  // do stuff
      	}*/
     //   System.out.println("Exiting application.");
      //  System.exit(0);
        //PApplet.main(new String[] { "--present", "tryakash.RFIDMapTry2" });
		
		}
		catch(Exception e)
		{
			
		}
	  }
	
	
	

    UnfoldingMap map;
    HashMap<String, RFIDObj> lifeExpMap;
    List<Feature> countries;
    List<Marker> countryMarkers;
    
    public static int RUNNING_STATUS = 0;

    public void setup() {
    	//controlP5
    	//background(0);
    	controlP5 = new ControlP5(this);
    	status = controlP5.addLabel("",400,20);
    	
    	pg = createGraphics(170, 170);
     
    	
    	
    	
    	 if(definedListBox){
    	 lbCSV = controlP5.addListBox("Asset No & EPC Tag",800+160,20,360+50,560); //addListBox(name,x,y,width,height)
    	 }
    	// ListBox lbCSVSign = controlP5.addListBox("Sign",800+160+120,20,120,560);
    	// ListBox lbCSVRead = controlP5.addListBox("Read (Yes/No)",800+160+240,20,120,560);
    	 signStop = loadImage("1.png");
    	 signStreet =  loadImage("streetSign.png");
    	 signYield = loadImage("yieldSign.png");
    	 signStop.resize(170, 170);
    	 signStreet.resize(170, 170);
    	 signYield.resize(170, 170);
    	 
    	 
    	 buttonImage = loadImage("buttons.jpg");
    	 
    	 
    	 
 		 
//          
//          String tmpSign = rfidTmp.sign;
//          tmpSign = tmpSign.substring(0,4);
//          
//      	
//          
//          if(tmpSign.equalsIgnoreCase("stop"))
//      	{
//      	 signCurrent = signStop;
//      	}
//      else if (tmpSign.equalsIgnoreCase("yiel"))
//      	{
//     	 signCurrent = signYield;
//      	}
//      else if (tmpSign.equalsIgnoreCase("stre"))
//  	{
//     	 signCurrent = signStreet;
//  	}
//       
//          
//    	 
    	 
    	 
    	 
    	 signCurrent = signStop;
      	 signNext = signYield;
      	 
      	rect(850, 100, 170, 170);
    	rect(850, 400, 170, 170);
    	
    	
    	
    	// lbCSV.captionLabel().set("slider speed");
    	// lbCSV.captionLabel().toUpperCase(false);
    	 // lbCSV.captionLabel().toUpperCase(false);
    	// lbCSV.captionLabel().set("Listbox label");
    	 
    	  
    	
    	
    	startReader = controlP5.addButton("Start Reader").setValue(10).setPosition(20,20)
    			.setSize(60,20).setId(1).addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
						 
			                if (event.getAction() == ControlP5.ACTION_RELEASED) {
			                  System.out.println("button clicked.");
			                 // status.setText(" Reader started ");
			                  runReader();
			                  //RUNNING_STATUS = 1;
			                   controlP5.remove(event.getController().getName());
			                }
			                
						
					}
				});
    	
    	stopReader = controlP5.addButton("Stop Reader").setValue(10).setPosition(100,20)
    			.setSize(60,20).setId(2).addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
						 
			                if (event.getAction() == ControlP5.ACTION_RELEASED) {
			                  
			                  if(RUNNING_STATUS == 1){
			                  stop();
			                  System.out.println(" stop button clicked.");
			                  PrintWriter writer = null;
			                  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
							try {
								writer = new PrintWriter("RFID"+timeStamp+".txt", "UTF-8");
							} catch (FileNotFoundException | UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("akash : didnot write ");
							}
			                	 
			                  for (Entry<String, Integer> entry : epcCount.entrySet()) {
			                	  String key = entry.getKey();
			                	  int value = entry.getValue();
			                	 // System.out.println("Key is "+key+" and Value is "+value);
			                	  writer.println("RFID File created on "+timeStamp);
			                	  //lifeExpMap Global database, assetEPC (EPCTag, assetID)
			                	 // RFIDObj tempFileObject = new RFIDObj();
			                	  writer.println("Key:"+key+" #Value:"+value+" #Sign:"); //key is the RFID tag and value is the number of tags read 
			                	  writer.close();
			                	  
			                	  // do stuff
			                	}
			                  
			                  //saving the log of the data
			                  String fileName = "temp.txt";

			                  try {
			                      // Assume default encoding.
			                      FileWriter fileWriter =
			                          new FileWriter(fileName);

			                      // Always wrap FileWriter in BufferedWriter.
			                      BufferedWriter bufferedWriter =
			                          new BufferedWriter(fileWriter);

			                      // Note that write() does not automatically
			                      // append a newline character.
			                      Set<String> keys = epcCount.keySet(); // the read tags
			                      
			                      bufferedWriter.write("Hello there,");
			                      bufferedWriter.write(" here is some text.");
			                      bufferedWriter.newLine();
			                      bufferedWriter.write("We are writing");
			                      bufferedWriter.write(" the text to the file.");
			                      String key;
			                      Iterator<Map.Entry<String, RFIDObj>> i = lifeExpMap.entrySet().iterator(); 
			                      
			                      while(i.hasNext()){
			                          key = i.next().getKey();
			                         
			                          
//			                          for ( String key : epcCount.keySet() ) {
//			                     		 String tmpKey = key.toString().trim();
//			                     		// tmpKey = tmpKey.substring(1, tmpKey.length());
//			                     		 tmpKey = "0x"+tmpKey;
//			                     		 String tmp = assetEPC.get(tmpKey);
//			                     		 System.out.println("Akash assetEPC"+tmp + " , Key = "+tmpKey);
//			                     		 RFIDObj rfidTmp = null;
//			                     		 if(tmp!=null)
//			                     			  rfidTmp = lifeExpMap.get(tmp);
//			                     		 System.out.println("Akash key "+tmp);
//			                          }
			                         // System.out.println("Asset:"+key+", loc: "+lifeExpMap.get(key).x+","+lifeExpMap.get(key).y+" ,EPC:"+(String) lifeExpMap.get(key).epcTag+" ,Sign:"+(String)lifeExpMap.get(key).sign);
			                          bufferedWriter.write( padRight(key.toString(), 5) +padRight((String) lifeExpMap.get(key).epcTag ,45)+padRight((String) lifeExpMap.get(key).sign ,55));
			                          bufferedWriter.newLine();
			                      }

			                      // Always close files.
			                      bufferedWriter.close();
			                  }
			                  catch(IOException ex) {
			                      System.out.println(
			                          "Error writing to file '"
			                          + fileName + "'");
			                      // Or we could just do this:
			                      // ex.printStackTrace();
			                  }
			                  
			                  
			                  
			                  
			                 
			                  }
			                  //0xe300833b2ddd9014035050000
			                   controlP5.remove(event.getController().getName());
			                }
			                
						
					}
				});
    	
    	exitButton = controlP5.addButton("Exit App").setValue(10).setPosition(180,20)
    			.setSize(60,20).setId(2).addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
						 
			                if (event.getAction() == ControlP5.ACTION_RELEASED) {
			                	 //stop(); // check akash
			                	 if(RUNNING_STATUS == 1){
					                  stop();
			                	 }
			                	 System.exit(0);
			                 
			                   controlP5.remove(event.getController().getName());
			                }
			                
						
					}
				});
    			
    	GPSButton = controlP5.addButton("GPS").setValue(10).setPosition(180+80,20)
    			.setSize(60,20).setId(1).addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
						 
			                if (event.getAction() == ControlP5.ACTION_RELEASED) {
			                  System.out.println("button clicked.");
			                 // status.setText(" Reader started ");
			                  try {
			                	//  playSound();  
			                	 // gpsStatus = 1;
								runGPS();
								
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			                  //RUNNING_STATUS = 1;
			                  //controlP5.remove(event.getController().getName());
			                }
			                
						
					}
				});
    	
    	GPSButton = controlP5.addButton("GPS Stop").setValue(10).setPosition(180+80+80,20)
    			.setSize(60,20).setId(1).addCallback(new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent event) {
						 
			                if (event.getAction() == ControlP5.ACTION_RELEASED) {
			                  System.out.println("GPS Stop button clicked.");
			                 // status.setText(" Reader started ");
			                  try {
			                	//  playSound();  
								//runGPS();
			                	 // gpsStatus = 0;
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			                  //RUNNING_STATUS = 1;
			                  //controlP5.remove(event.getController().getName());
			                }
			                
						
					}
				});
    	
    	
    	
    	
    	
    	
        size(1400, 600, OPENGL);
        
        AbstractMapProvider provider = new Google.GoogleMapProvider();
		// Set a zoom level
		int zoomLevel = 15;
		boolean offline = false;
		String mbTilesString = "blankLight-1-3.mbtiles";
		PImage img = createImage(66, 66, RGB);
		if (offline) {
			// If you are working offline, you need to use this provider 
			// to work with the maps that are local on your computer.  
			provider = new MBTilesMapProvider(mbTilesString);
			// 3 is the maximum zoom level for working offline
			 
		}
		
		//Pimage bg = loadImage("bgImage.png")
        
        map = new UnfoldingMap(this, 50, 50, 700, 500, provider);
        
     
        backgroundMap = loadImage("bgImage.png");
        
        backgroundMap.resize(700, 500);
        MapUtils.createDefaultEventDispatcher(this, map);

        // Load lifeExpectancy data
        lifeExpMap = loadLifeExpectancyFromCSV("Riverside2.csv");
        println("Loaded " + lifeExpMap.size() + " data entries");
        Location loc; 
        //ImageMarker marker;
        SimplePointMarker pointMarker;
        String key;
        Iterator<Map.Entry<String, RFIDObj>> i = lifeExpMap.entrySet().iterator(); 
        
        
        
        String format = "%-40s%s%s%n"; //left justified 40String and 
        
        
        while(i.hasNext()){
            key = i.next().getKey();
            System.out.println("Asset:"+key+", loc: "+lifeExpMap.get(key).x+","+lifeExpMap.get(key).y+" ,EPC:"+(String) lifeExpMap.get(key).epcTag+" ,Sign:"+(String)lifeExpMap.get(key).sign);
            if(definedListBox)
            	lbCSV.addItem( padRight(key.toString(), 5) +padRight((String) lifeExpMap.get(key).epcTag ,45)+padRight((String) lifeExpMap.get(key).sign ,55), Integer.parseInt(key));
           // lbCSV.setSize(1, 15);
           // lbCSVSign.addItem(lifeExpMap.get(key).sign, Integer.parseInt(key));
           // lbCSVRead.addItem( "NO", Integer.parseInt(key));
            //lbCSV.bac
       
            
           loc = new Location(lifeExpMap.get(key).x, lifeExpMap.get(key).y);
        	
           
            //marker = new ImageMarker(loc, loadImage("ui/marker.png"));
           // map.addMarkers(marker);
            pointMarker = new SimplePointMarker(loc);
            //change sign
            String tmpSign = (String)lifeExpMap.get(key).sign;
            tmpSign = tmpSign.substring(0,4);
          
            //marker colors
            if(tmpSign.equalsIgnoreCase("stop"))
            	{
            	pointMarker.setColor(color(255,255, 255, 100));
            	//pointMarker.setStrokeColor(color(255, 0, 0));
            	//pointMarker.setStrokeWeight(4);
            	map.addMarker(pointMarker);
            	}
            else if (tmpSign.equalsIgnoreCase("yiel"))
            	{
            	pointMarker.setColor(color(255, 255, 255, 100));
            	map.addMarker(pointMarker);
            	}
            else if (tmpSign.equalsIgnoreCase("stre"))
        	{
            	pointMarker.setColor(color(255, 255, 255, 100));
            	map.addMarker(pointMarker);
        	}
             
            
            

          //  int zoomLevel = 15;
    	    map.zoomAndPanTo(zoomLevel, new Location(30.641602 , -96.4739));
          //  map.zoomAndPanTo(zoomLevel, new Location(30.6235,-96.347619));
            //-96.3476199,30.6235163
    	    // water body location 30.635620 , -96.463557
    	    //epc tag : 0xe300833b2ddd9014035050000
    	  
        	
        }
       // System.out.println("akash"+lbCSV.getItem(1));
       // System.out.println("Asset ID:"+lifeExpMap.get+" GPS_Coordinates:"+tmpObj.x+","+tmpObj.y);
       

        // Load country polygons and adds them as markers
       // countries = GeoJSONReader.loadData(this, "countries_copy.geo2.json");
       // countryMarkers = MapUtils.createSimpleMarkers(countries);
       // map.addMarkers(countryMarkers);

        // Country markers are shaded according to life expectancy (only once)
       // shadeCountries();
    }
    public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}


protected void runGPS() {
		// TODO Auto-generated method stub
//	DatagramSocket serverSocket;
//	try {
//		serverSocket = new DatagramSocket(12345);
//	
//	byte[] receiveData = new byte[1024];         
//	byte[] sendData = new byte[1024];         
//	while(true)                {    
//		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);   
//		serverSocket.receive(receivePacket);       
//		String toSplit = new String( receivePacket.getData());   
//		//System.out.println("RECEIVED: " + sentence);
//		String[] arraySplit = toSplit.split(",");
//		String GPSLong = arraySplit[5].substring(1, 2) + arraySplit[5].substring(3, arraySplit[5].length());
//		String GPSLat = arraySplit[4].substring(2, arraySplit[4].length());
//		System.out.println("lat is "+GPSLat+" and long is "+GPSLong);
//		
//		
//		
//		InetAddress IPAddress = receivePacket.getAddress();       
//		int port = receivePacket.getPort();            
//		String capitalizedSentence = toSplit.toUpperCase();        
//		sendData = capitalizedSentence.getBytes();                   
//		DatagramPacket sendPacket =new DatagramPacket(sendData, sendData.length, IPAddress, port);
//		serverSocket.send(sendPacket);    
//		} 
//	}
//	
//	catch (SocketException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} 
		
	//test t1 = new test();
	  int a = 3;
	  int b = 4;
	   
	  System.out.println("Starting");
	  MyObservable ob = new MyObservable();
	   
	  // Add observers
	  System.out.println("Adding observers");
	  //ob.addObserver(new MyFirstObserver());
	  ob.addObserver(new RFIDHighWayProgram());
	   
	 // System.out.println("Executing Sum :  " + a + " + " + b);
	  ob.sum(a, b);
	  System.out.println("Finished");
	
	}




public static int STATUS_DRAW = 0;
public static int STATUS_GPS_DRAW = 0;
 
    public void draw() {
        // Draw map tiles and country markers
    	background(255);
    	
    	
    	image(backgroundMap, 50, 50);
    	
    	
    	
    	image(signCurrent, 850,100, 170, 170 );
    	image(buttonImage, 0, 0, buttonImage.width, buttonImage.height);
    	//image(signNext, 850,400 );
    	//rect(1100, 100, 170, 170);
    	//rect(1100, 400, 170, 170);
//    	 pg.beginDraw();
//    	 //signCurrent = loadImage("1.png");
//    			 
//    	 pg.image(signCurrent,170,170);
//    	 pg.endDraw();
//    	 image(pg,1100,400,170,170);
//    	 
    	
    	
    	
    	if(STATUS_DRAW == 1){
    	 for ( String key : epcCount.keySet() ) {
    		 String tmpKey = key.toString().trim();
    		// tmpKey = tmpKey.substring(1, tmpKey.length());
    		 tmpKey = "0x"+tmpKey;
    		 String tmp = assetEPC.get(tmpKey);
    		 System.out.println("Akash assetEPC"+tmp + " , Key = "+tmpKey);
    		
    		 RFIDObj rfidTmp = null;
    		 if(tmp!=null){
    			  rfidTmp = lifeExpMap.get(tmp);
    		 	System.out.println("Akash key "+tmp);
    		 }
    		 if (rfidTmp != null) {
    		   
    		 location = new Location(rfidTmp.x, rfidTmp.y);
    		 pM = new SimplePointMarker(location);
    		 pM.setColor(color(0, 255, 0, 100));
             //pointMarker.setStrokeColor(color(255, 0, 0));
             //pointMarker.setStrokeWeight(4);
             map.addMarker(pM);
             
             
//             String tmpSign = rfidTmp.sign;
//             tmpSign = tmpSign.substring(0,4);
//             
//         	
//             
//             if(tmpSign.equalsIgnoreCase("stop"))
//         	{
//         	 signCurrent = signStop;
//         	}
//         else if (tmpSign.equalsIgnoreCase("yiel"))
//         	{
//        	 signCurrent = signYield;
//         	}
//         else if (tmpSign.equalsIgnoreCase("stre"))
//     	{
//        	 signCurrent = signStreet;
//     	}
          
             
             /*
              * signCurrent = signStop;
    	signNext = signYield;
    	image(signCurrent, 850,100 );
    	image(signNext, 850,400 );
              * 
              * 
              */
            
            // System.out.println("Done updating maps Sams");
    		 }
    		 
    	}
    	 STATUS_DRAW = 0;
    	 //System.out.println("Done updating maps Akash Sahoo");
    	}
    	
    	
    	if(STATUS_GPS_DRAW == 1 ){
    		
    		location =  new Location(Double.parseDouble(GPSLat),Double.parseDouble(GPSLong));
    		pM = new SimplePointMarker(location);
    		pM.setRadius(4);
    		pM.setColor(color(255, 0, 0, 30));
            //pointMarker.setStrokeColor(color(255, 0, 0));
            //pointMarker.setStrokeWeight(4);
            map.addMarker(pM);
           
    		
    		STATUS_GPS_DRAW = 0;
    	}
    	
    	map.draw();
    	//image(backgroundMap, 0, 0);
    	
    //	System.out.println("sahoo draw");
    	//runReader();
    //	 pM = new SimplePointMarker(loc);
         //change sign
        // String tmpSign = (String)lifeExpMap.get(key).sign;
         //tmpSign = tmpSign.substring(0,4);
       
         //marker colors
//         if(tmpSign.equalsIgnoreCase("stop"))
//         	{
//         	pointMarker.setColor(color(255, 0, 0, 100));
//         	//pointMarker.setStrokeColor(color(255, 0, 0));
//         	//pointMarker.setStrokeWeight(4);
//         	map.addMarker(pointMarker);
//         	}
    	
        
    }
    
    
    
     
    
    private void runReader(){
try {
	status.setText(" Reader started ");
     
		RUNNING_STATUS =1;
    		
    		Thread.sleep(100);
    		status.setText(" READER started ");
			System.out.println("Starting reader.");
		       //run("169.254.1.1");
				run("192.168.1.50");
		       
		       //Thread.sleep(5000);
		       // System.out.println("Stopping reader.");
		       // stop();
		      //  System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    // Helper method to color each country based on life expectancy
    // Red-orange indicates low (near 40)
    // Blue indicates high (near 100)
    private void shadeCountries() {
        for (Marker marker : countryMarkers) {
            // Find data for country of the current marker
            String countryId = marker.getId();
            if (lifeExpMap.containsKey(countryId)) {
                //float lifeExp = lifeExpMap.get(countryId);
                // Encode value as brightness (values range: 40-90)
               // int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);
              //  marker.setColor(color(255 - colorLevel, 100, colorLevel));
            } else {
                marker.setColor(color(150, 150, 150));
            }
        }
    }

    // Helper method to load life expectancy data from file
    private HashMap<String, RFIDObj> loadLifeExpectancyFromCSV(String fileName) {
        HashMap<String, RFIDObj> lifeExpMap = new HashMap<String, RFIDObj>();
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
                lifeExpMap.put(columns[1],tmpObj); //asset no
                assetEPC.put(tmpObj.epcTag, columns[1]);
                
            }
           }
        
         System.out.println("Akash "+assetEPC.get("300833b2ddd9014035050000"));
        
        
         

        return lifeExpMap;
    }

	
    //RFID Functions
	public ROSpec buildROSpec()
    { 
       // System.out.println("Building the ROSpec.");
          
        // Create a Reader Operation Spec (ROSpec).
        ROSpec roSpec = new ROSpec();
          
        roSpec.setPriority(new UnsignedByte(0));
        roSpec.setCurrentState(new ROSpecState(ROSpecState.Disabled));
        roSpec.setROSpecID(new UnsignedInteger(ROSPEC_ID));
        
          
        // Set up the ROBoundarySpec
        // This defines the start and stop triggers.
        ROBoundarySpec roBoundarySpec = new ROBoundarySpec();
          
        // Set the start trigger to null.
        // This means the ROSpec will start as soon as it is enabled.
        ROSpecStartTrigger startTrig = new ROSpecStartTrigger();
        startTrig.setROSpecStartTriggerType
            (new ROSpecStartTriggerType(ROSpecStartTriggerType.Null));
        roBoundarySpec.setROSpecStartTrigger(startTrig);
          
        // Set the stop trigger is null. This means the ROSpec
        // will keep running until an STOP_ROSPEC message is sent.
        ROSpecStopTrigger stopTrig = new ROSpecStopTrigger();
        stopTrig.setDurationTriggerValue(new UnsignedInteger(0));
        stopTrig.setROSpecStopTriggerType
            (new ROSpecStopTriggerType(ROSpecStopTriggerType.Null));
        roBoundarySpec.setROSpecStopTrigger(stopTrig);
          
        roSpec.setROBoundarySpec(roBoundarySpec);
          
        // Add an Antenna Inventory Spec (AISpec).
        AISpec aispec = new AISpec();
          
        // Set the AI stop trigger to null. This means that
        // the AI spec will run until the ROSpec stops.
        AISpecStopTrigger aiStopTrigger = new AISpecStopTrigger();
        aiStopTrigger.setAISpecStopTriggerType
            (new AISpecStopTriggerType(AISpecStopTriggerType.Null));
        aiStopTrigger.setDurationTrigger(new UnsignedInteger(0));
        aispec.setAISpecStopTrigger(aiStopTrigger);
          
        // Select which antenna ports we want to use.
        // Setting this property to zero means all antenna ports.
        UnsignedShortArray antennaIDs = new UnsignedShortArray();
        antennaIDs.add(new UnsignedShort(0));
        aispec.setAntennaIDs(antennaIDs);
          
        // Tell the reader that we're reading Gen2 tags.
        InventoryParameterSpec inventoryParam = new InventoryParameterSpec();
        inventoryParam.setProtocolID
            (new AirProtocols(AirProtocols.EPCGlobalClass1Gen2));
        inventoryParam.setInventoryParameterSpecID(new UnsignedShort(1));
        aispec.addToInventoryParameterSpecList(inventoryParam);
          
        roSpec.addToSpecParameterList(aispec);
          
        // Specify what type of tag reports we want
        // to receive and when we want to receive them.
        ROReportSpec roReportSpec = new ROReportSpec();
        // Receive a report every time a tag is read.
        roReportSpec.setROReportTrigger(new ROReportTriggerType
            (ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
        roReportSpec.setN(new UnsignedShort(1));
        TagReportContentSelector reportContent =
            new TagReportContentSelector();
        // Select which fields we want in the report.
        reportContent.setEnableAccessSpecID(new Bit(0));
        reportContent.setEnableAntennaID(new Bit(0));
        reportContent.setEnableChannelIndex(new Bit(0));
        reportContent.setEnableFirstSeenTimestamp(new Bit(0));
        reportContent.setEnableInventoryParameterSpecID(new Bit(0));
        reportContent.setEnableLastSeenTimestamp(new Bit(1));
        reportContent.setEnablePeakRSSI(new Bit(0));
        reportContent.setEnableROSpecID(new Bit(0));
        reportContent.setEnableSpecIndex(new Bit(0));
        reportContent.setEnableTagSeenCount(new Bit(0));
        roReportSpec.setTagReportContentSelector(reportContent);
        roSpec.setROReportSpec(roReportSpec);
          
        return roSpec;
    }
      
    // Add the ROSpec to the reader.
    public void addROSpec()
    {
        ADD_ROSPEC_RESPONSE response;
          
        ROSpec roSpec = buildROSpec();
       // System.out.println("Adding the ROSpec.");
        try
        {
            ADD_ROSPEC roSpecMsg = new ADD_ROSPEC();
            roSpecMsg.setROSpec(roSpec);
            response = (ADD_ROSPEC_RESPONSE)
                reader.transact(roSpecMsg, TIMEOUT_MS);
           // System.out.println(response.toXMLString());
              
            // Check if the we successfully added the ROSpec.
            StatusCode status = response.getLLRPStatus().getStatusCode();
            if (status.equals(new StatusCode("M_Success")))
            {
              //  System.out.println
                //    ("Successfully added ROSpec.");
            }
            else
            {
                System.out.println("Error adding ROSpec.");
                System.exit(1);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error adding ROSpec.");
            e.printStackTrace();
        }
    }
      
    // Enable the ROSpec.
    public void enableROSpec()
    {
        ENABLE_ROSPEC_RESPONSE response;
          
       // System.out.println("Enabling the ROSpec.");
        ENABLE_ROSPEC enable = new ENABLE_ROSPEC();
        enable.setROSpecID(new UnsignedInteger(ROSPEC_ID));
        try
        {
            response = (ENABLE_ROSPEC_RESPONSE)
                reader.transact(enable, TIMEOUT_MS);
           // System.out.println(response.toXMLString());
        }
        catch (Exception e)
        {
            System.out.println("Error enabling ROSpec.");
            e.printStackTrace();
        }
    }
      
    // Start the ROSpec.
    public void startROSpec()
    {
        START_ROSPEC_RESPONSE response;
       // System.out.println("Starting the ROSpec.");
        START_ROSPEC start = new START_ROSPEC();
        start.setROSpecID(new UnsignedInteger(ROSPEC_ID));
        try
        {
            response = (START_ROSPEC_RESPONSE)
                reader.transact(start, TIMEOUT_MS);
          //  System.out.println(response.toXMLString());
        }
        catch (Exception e)
        {
            System.out.println("Error deleting ROSpec.");
            e.printStackTrace();
        }
    }
      
    // Delete all ROSpecs from the reader.
    public void deleteROSpecs()
    {
        DELETE_ROSPEC_RESPONSE response;
          
       // System.out.println("Deleting all ROSpecs.");
        DELETE_ROSPEC del = new DELETE_ROSPEC();
        // Use zero as the ROSpec ID.
        // This means delete all ROSpecs.
        del.setROSpecID(new UnsignedInteger(0));
        try
        {
            response = (DELETE_ROSPEC_RESPONSE)
                reader.transact(del, TIMEOUT_MS);
           // System.out.println(response.toXMLString());
        }
        catch (Exception e)
        {
            System.out.println("Error deleting ROSpec.");
            e.printStackTrace();
        }
    }
      
    // This function gets called asynchronously
    // when a tag report is available.
    public static int NUMBER_OF_READ_TAGS = 0;
    public static int soundFlag = 0;
    public void messageReceived(LLRPMessage message)
    {
    	
    	String temp[] = null;
    	int tmpEPCCount = 0;
    	
    	
        if (message.getTypeNum() == RO_ACCESS_REPORT.TYPENUM)
        {
            // The message received is an Access Report.
            RO_ACCESS_REPORT report = (RO_ACCESS_REPORT) message;
            // Get a list of the tags read.
            List <TagReportData> tags =
                report.getTagReportDataList();
            // Loop through the list and get the EPC of each tag.
            for (TagReportData tag : tags)
            {
                //System.out.println(tag.getEPCParameter());
            	int tmp = NUMBER_OF_READ_TAGS;
            	 tagsEPCRead = (String)tag.getEPCParameter().toString();
            	 temp = tagsEPCRead.split(Pattern.quote(":"));
            	 //System.out.println(temp[2]);
            	 if(temp[2].equalsIgnoreCase(" 100000000000000000000002"))
            	 {
            		 if(soundFlag == 0){
            		 System.out.println("sound....");
        			 playSound();
        			 soundFlag = 1;
            		 }
            	 }
            	 if(epcCount.get(temp[2])==null){
            		 epcCount.put(temp[2], 1);
            	 }else{
            	 tmpEPCCount = epcCount.get(temp[2]);
            	 tmpEPCCount = tmpEPCCount + 1;
            	 epcCount.put(temp[2],tmpEPCCount);
            	 tmpEPCCount = 0;
            	 String tmpTagPrint =  "0x"+temp[2].trim();
            	 RFIDObj printTagRFIDObj = new RFIDObj();
            	 printTagRFIDObj = lifeExpMap.get(assetEPC.get(tmpTagPrint));
            	// System.out.println("test: "+assetEPC.get(tmpTagPrint));
            	 if(printTagRFIDObj != null){
            		 System.out.println(printTagRFIDObj.assetID+" # "+printTagRFIDObj.sign+" # "+temp[2]);
            		 signCurrent = loadImage(printTagRFIDObj.assetID+".png");
                     signCurrent.resize(170,170);
                      
                     
            		    
//                   String tmpSign = rfidTmp.sign;
//                   tmpSign = tmpSign.substring(0,4);
//                   
//               	
//                   
//                   if(tmpSign.equalsIgnoreCase("stop"))
//               	{
//               	 signCurrent = signStop;
//               	}
//               else if (tmpSign.equalsIgnoreCase("yiel"))
//               	{
//              	 signCurrent = signYield;
//               	}
//               else if (tmpSign.equalsIgnoreCase("stre"))
//           	{
//              	 signCurrent = signStreet;
//           	}
//                
            		 
            	 }
            	// else
            		// System.out.println("Akash Reead tmp NULL"+temp[2]);
            	 
            	 NUMBER_OF_READ_TAGS = epcCount.size();
            	 if(tmp != NUMBER_OF_READ_TAGS){
            		 System.out.println("New tags Read & updating the maps");
            		 // tags from the 
              		 STATUS_DRAW = 1;
            		 
            	 }
            	 
            	 
            	 }
            	 
            	 
            	// System.out.println(tagsEPCRead);
               // PassEPCTags(tagsEPCRead);
            	 
            	 
            	 
            	 
            	 
            	 
            	 
            	 
                
               // System.out.println("Akash count is ");
            }
            
        }
    }
      
    
	 

	// This function gets called asynchronously
    // when an error occurs.
    public void errorOccured(String s)
    {
        System.out.println("An error occurred: " + s);
    }
      
    // Connect to the reader
    public void connect(String hostname)
    {
        // Create the reader object.
        reader = new LLRPConnector(this, hostname);
          
        // Try connecting to the reader.
        try
        {
            System.out.println("Connecting to the reader.");
                ((LLRPConnector) reader).connect();
        }
        catch (LLRPConnectionAttemptFailedException e1)
        {
            e1.printStackTrace();
            System.exit(1);
        }
    }
      
    // Disconnect from the reader
    public void disconnect()
    {
        ((LLRPConnector) reader).disconnect();
    }
      
    // Connect to the reader, setup the ROSpec
    // and run it.
    public void run(String hostname)
    {
    	 status.setText(" Reader started ");
    	 
         
        connect(hostname);
        deleteROSpecs();
        addROSpec();
        enableROSpec();
        startROSpec();
    }
      
    // Cleanup. Delete all ROSpecs
    // and disconnect from the reader.
    public void stop()
    {
    	RUNNING_STATUS = 0;
    	 status.setText(" Reader stopped ");
         
        deleteROSpecs();
        disconnect();
        tagsEPCRead = "x";
    }





	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//System.out.println("Second Observer Notified:" + o + "  :  " + arg);
		 
		  System.out.println("Observer Notified:" + " " + arg);
		 
		  if(arg.toString().length()>5){
		  STATUS_GPS_DRAW = 1;
		  String[] observerGPS = arg.toString().split(",");
		  GPSLat = observerGPS[0];
		  GPSLong = observerGPS[1];
		  
		  }
		 
		
	}
	
	
	public void playSound(){

        String strFilename = "fsk.wav";
           int BUFFER_SIZE = 128000;
          File soundFile = null;
          AudioInputStream audioStream = null;
          javax.sound.sampled.AudioFormat audioFormat;
          SourceDataLine sourceLine = null;


        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
	
	public static synchronized void playSound1() {
		String url = "fsk.wav";
		  new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run() {
		      try {
		        Clip clip = AudioSystem.getClip();
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
		          Main.class.getResourceAsStream(url));
		        clip.open(inputStream);
		        clip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}

	
	//RFID Functions
	//distance functions
	private void addDistanceMatrix() {
   	 int hSize = globalRFDataDump.size();
   	 System.out.println("Printing the distance Matrix of size "+hSize);
   	//String tmp;
   	 distanceMatrix = new double[hSize][hSize];
   	 
   	 int i=0,j = 0;
   	 
   	 Set<String> keys = globalRFDataDump.keySet();  //get all keys
   	 for(String k: keys)
   	 {
   		 j=0;
   		 for(String k2: keys)
       	 {
   			 if(k.equals(k2)){
   				 distanceMatrix[i][j] = 0;
   			 }
   			 else
   				 {
   				 	
   				 	distanceMatrix[i][j] = getDistance(globalRFDataDump.get(k).x, globalRFDataDump.get(k).y, globalRFDataDump.get(k2).x, globalRFDataDump.get(k2).y);
   				 }
   			 j++;
       	 
       	 }
   	     //System.out.println(globalRFDataDump.get(k));
   		 i++;
   	 }
   	 
   	 
   	/* Iterator it = globalRFDataDump.entrySet().iterator();
   	 Iterator it2 = globalRFDataDump.entrySet().iterator();
   	    while (it.hasNext()) {
   	        Map.Entry pair = (Map.Entry)it.next();
   	       // System.out.println(pair.getKey() + " = " + pair.getValue());
   	        for(int j=0;j<hSize;j++)
      		 	{
   	        	Map.Entry pair2 = (Map.Entry)it2.next();
   	        	  
      			 if(pair.getKey().toString().equals(pair2.getKey().toString())){
      				 distanceMatrix[i][j] = 0;
      			 }
      			 else
      				 {
      				 	
      				 	distanceMatrix[i][j] = getDistance(globalRFDataDump.get(pair.getKey().toString()).Latitude, globalRFDataDump.get(pair.getKey().toString()).Longitude, globalRFDataDump.get(pair2.getKey().toString()).Latitude, globalRFDataDump.get(pair2.getKey().toString()).Longitude);
      				 }
      			 i++;
      			 it2.remove();
      		 }
   	        it.remove(); // avoids a ConcurrentModificationException
   	    }*/
   	 
   	 
   	 
   	    
   	    /*
   	 for( i=0;i<hSize;i++)
   	 {
   		 for(int j=0;j<hSize;j++)
   		 {	
   			 if(i==j){
   				 distanceMatrix[i][j] = 0;
   			 }
   			 else
   				 {
   				 	
   				 	distanceMatrix[i][j] = getDistance(globalRFDataDump.get(Integer.toString(i+1)).Latitude, globalRFDataDump.get(Integer.toString(i+1)).Longitude, globalRFDataDump.get(Integer.toString(j+1)).Latitude, globalRFDataDump.get(Integer.toString(j+1)).Longitude);
   				 }
   		 }
   	 }
   	 */
   	 
   	 for( i=0;i<hSize;i++)
   	 {
   		 //System.out.println(i+": ");
   		 for( j=0;j<hSize;j++)
   		 {	
   			 //System.out.print(j+" ");
   			 System.out.format("%10f,%10s",distanceMatrix[i][j], "     ");
   			 
   		 }
   		 System.out.println("");
   	 }
   	 
	}
   	 
   	 
   	int[] findNearestThreePoints(float nLat, float nLng){
    	int ids[] ={0, 0, 0};
    	
    if( globalRFDataDump.size()>3){
    	RFIDObj threePoints = new RFIDObj();
    	//threePoints.SampleNumber = rssiCalcKey;
    	threePoints.x = nLat;
    	threePoints.y = nLng;
    	
    			
    	globalRFDataDump.put(Integer.toString(threePoints.assetID), threePoints);
    	System.out.println("The nearest 3 points from the coordinates are ");
    	
    	
    	addDistanceMatrix(); //updated with the new coordinate
    	int hmSize = globalRFDataDump.size();
    	double sortDistance[] = new double[hmSize]; 
    	    	
    	HashMap<Double, Integer> matrixThreeMin = new HashMap<Double, Integer>();  
    	Set<String> keys2 = globalRFDataDump.keySet();
    	String Stmp = keys2.toString();
    	Stmp = Stmp.replace("[", "");
    	Stmp = Stmp.replace("]", "");
    	Stmp = Stmp.replace(" ", "");
    	String Skeys[] = (Stmp).split(",");
    	// TODO: add transLat and Lng values
    	double transLat = 33.4; //akash edit these
    	double transLng = -96.3;
    	
    	Double[] transmitDistance =  new Double[hmSize];
    	
    	for( int i = 0;i<hmSize;i++)
    	{
    		transmitDistance [i] = getDistance(transLat,transLng,globalRFDataDump.get(Skeys[i]).x, globalRFDataDump.get(Skeys[i]).y);
    	}
    	for( int i = 1; i<=hmSize;i++)
    	{
    		 //float tempFormat = 2.345343f;
    		 //float formatted =Float.parseFloat(String.format("%.2f", tempFormat));
    		// matrixThreeMin.put(distanceMatrix[hmSize-1][i-1], i); //akash change to distance from transmitter getDistance
    		 
    		matrixThreeMin.put(transmitDistance[i-1], i);
    		
    		sortDistance[i-1] = transmitDistance[i-1];
    		 
    		 	
    	}
    	//sort
    	double tmpDistance[] = new double[hmSize];
    	tmpDistance = sortDistance;
    	Arrays.sort(sortDistance);
    	ids[0] = matrixThreeMin.get(sortDistance[1]); // ids give nearest points to the location
    	ids[1] = matrixThreeMin.get(sortDistance[2]);
    	ids[2] = matrixThreeMin.get(sortDistance[3]);
    	sortDistance = tmpDistance;
    	
    	
    	//solve the 1,2 equations
    	//eq is RSSI = alpha *   *  (distance) ^ -n // solve n and aplha
    // n = log r2 - log r1 / (log d1 + log d2)
    	
    	
    	//System.out.println("akash skeys"+keys.toString());
    	//System.out.println("akash double  distance"+Array.toString() );
    	 //match error with 3rd eq
    	//double error = globalRFDataDump.get(Skeys[ids[2]-1]).RSSI - (alpha / Math.pow(sortDistance[ids[2]], n));
    	
    	//System.out.println("Error from formula is " + error);
    	 
    
    	//find rssi from the equation
    	//TODO: p5Flotat value declare
    	double p5FloatLat = 30.0f;
    	double p5FloatLng = -96.5f;
    	double distanceofCalculatedRssiFromCenter =  getDistance(transLat, transLng, p5FloatLat, p5FloatLng);//transmitDistance[hmSize-1];
    	System.out.println(distanceofCalculatedRssiFromCenter);
    	 
    	System.out.println("distance trans is " + Arrays.toString(transmitDistance));
    	
    	
    	}
    	
    
    
    	return ids;
    }
    
   	 
   	 
		
	
    
    
	
	double getDistance (double lat1, double lng1, double lat2, double lng2) {
  	  int R = 6378137; // Earth’s mean radius in meter
  	  double dLat = rad(lat2 - lat1);
  	  double dLong = rad(lng2 - lng1);
  	  double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
  	    Math.cos(rad(lat1)) * Math.cos(rad(lat2)) *
  	    Math.sin(dLong / 2) * Math.sin(dLong / 2);
  	  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  	  double d = R * c;
  	  return d; // returns the distance in meter
  	}
	
	double rad( double x) {
  	  return (double) (x * Math.PI / 180);
  	}
  
    

}






class RFIDObj{
	public double x,y;
	public String epcTag;
	public String sign;
	public int assetID;
	public boolean readStatus;
	
	public RFIDObj(double x, double y) {
		this.x = x;
		this.y =y;
		// TODO Auto-generated constructor stub
	}
	public RFIDObj(double x, double y, String epc, String sgn, int asset) {
		this.x = x;
		this.y =y;
		this.epcTag = epc;
		this.sign = sgn;
		this.assetID = asset;
		this.readStatus = false;
		// TODO Auto-generated constructor stub
	}
	public RFIDObj() {
		// TODO Auto-generated constructor stub
		x=0;y=0;
		epcTag = null;
		sign = null;
		assetID = 0;
		readStatus = false;
	}
	 
	
	
	
	//RFID Functions
	

	
	
	
}
