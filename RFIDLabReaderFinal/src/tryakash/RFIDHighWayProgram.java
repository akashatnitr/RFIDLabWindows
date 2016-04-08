package tryakash;

import processing.core.PApplet;
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



import org.llrp.ltk.generated.parameters.TagReportData;

import org.llrp.ltk.generated.enumerations.*;
import org.llrp.ltk.generated.interfaces.*;
import org.llrp.ltk.generated.messages.*;
import org.llrp.ltk.generated.parameters.*;
import org.llrp.ltk.net.*;
import org.llrp.ltk.types.*;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
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
      
    ControlP5 controlP5;
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
	
	//RFID
	
	
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
    	controlP5 = new ControlP5(this);
    	status = controlP5.addLabel("",400,20);
    	
    	
    	
    	
    	 lbCSV = controlP5.addListBox("Asset No & EPC Tag",800+160,20,360+50,560); //addListBox(name,x,y,width,height)
    	// ListBox lbCSVSign = controlP5.addListBox("Sign",800+160+120,20,120,560);
    	// ListBox lbCSVRead = controlP5.addListBox("Read (Yes/No)",800+160+240,20,120,560);
    	 
    	
    	
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
			                  for (Entry<String, Integer> entry : epcCount.entrySet()) {
			                	  String key = entry.getKey();
			                	  int value = entry.getValue();
			                	  System.out.println("Key is "+key+" and Value is "+value);
			                	  
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
        System.out.println("akash"+lbCSV.getItem(1));
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
    	//background(0);
    	
    	
    	image(backgroundMap, 50, 50);
    	if(STATUS_DRAW == 1){
    	 for ( String key : epcCount.keySet() ) {
    		 String tmpKey = key.toString().trim();
    		// tmpKey = tmpKey.substring(1, tmpKey.length());
    		 tmpKey = "0x"+tmpKey;
    		 String tmp = assetEPC.get(tmpKey);
    		 System.out.println("Akash assetEPC"+tmp + " , Key = "+tmpKey);
    		 RFIDObj rfidTmp = null;
    		 if(tmp!=null)
    			  rfidTmp = lifeExpMap.get(tmp);
    		 System.out.println("Akash key "+tmp);
    		 if (rfidTmp != null) {
    		   
    		 location = new Location(rfidTmp.x, rfidTmp.y);
    		 pM = new SimplePointMarker(location);
    		 pM.setColor(color(0, 255, 0, 100));
             //pointMarker.setStrokeColor(color(255, 0, 0));
             //pointMarker.setStrokeWeight(4);
             map.addMarker(pM);
            
            // System.out.println("Done updating maps Sams");
    		 }
    		 
    	}
    	 STATUS_DRAW = 0;
    	 //System.out.println("Done updating maps Akash Sahoo");
    	}
    	
    	
    	if(STATUS_GPS_DRAW == 1){
    		
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
                lifeExpMap.put(columns[1],tmpObj);
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
            	 System.out.println(temp[2]);
            	 
            	 if(epcCount.get(temp[2])==null){
            		 epcCount.put(temp[2], 1);
            	 }else{
            	 tmpEPCCount = epcCount.get(temp[2]);
            	 tmpEPCCount = tmpEPCCount + 1;
            	 epcCount.put(temp[2],tmpEPCCount);
            	 tmpEPCCount = 0;
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

	
	//RFID Functions
    
    
    

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
	
	
	
	
	//RFID Functions
	

	
	
	
}