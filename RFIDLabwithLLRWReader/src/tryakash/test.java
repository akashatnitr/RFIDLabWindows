package tryakash;

import java.io.*;
import java.net.*;
import java.util.Observable; 
class test extends Observable { 
	static String GPSLat;
	static String GPSLong;
	 test(){     
		
		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		    	 DatagramSocket serverSocket = null;
				try {
					serverSocket = new DatagramSocket(12345);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		 		byte[] receiveData = new byte[1024];         
		 		byte[] sendData = new byte[1024];         
		 		while(true)                {    
		 			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);   
		 			try {
						serverSocket.receive(receivePacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}       
		 			String sentence = new String( receivePacket.getData());   
		 			//System.out.println("RECEIVED: " + sentence); 
		 			setChanged();
		 			String[] arraySplit = sentence.split(",");
		 			GPSLong = arraySplit[5].substring(1, 2) + arraySplit[5].substring(3, arraySplit[5].length());
		 			GPSLat = arraySplit[4].substring(2, arraySplit[4].length());
		 			notifyObservers(new String(GPSLong));
		 			notifyObservers(new String(GPSLat));
		 			 
		 			System.out.println("lat is "+GPSLat+" and long is "+GPSLong);
		 			
		 			InetAddress IPAddress = receivePacket.getAddress();       
		 			int port = receivePacket.getPort();            
		 			String capitalizedSentence = sentence.toUpperCase();        
		 			sendData = capitalizedSentence.getBytes();                   
		 			DatagramPacket sendPacket =new DatagramPacket(sendData, sendData.length, IPAddress, port);
		 			try {
						serverSocket.send(sendPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
		 			} 
		          // code goes here.
		     }
		});  
		t1.start();
		 
		}
	} 
//- See more at: https://systembash.com/a-simple-java-udp-server-and-udp-client/#sthash.WCAz6wpe.dpuf