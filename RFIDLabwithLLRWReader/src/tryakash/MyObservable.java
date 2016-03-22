package tryakash;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;
 
 
public class MyObservable extends Observable {
	static String GPSLat,GPSLong;

  
 public void sum(int a, int b){
   
  // If not set then observers will think nothing is changed
  // hence no action required. 
  setChanged();
   
  // Perform the business logic
  int c = a+b;
   
  System.out.println("Notifying Observers");
  // A call to notifyObservers() also clears the changed flag
  
  
  
  
  notifyObservers(new Integer(c)); 
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
		notifyObservers(new String(GPSLat +","+ GPSLong));
		//notifyObservers(new String());
		 
		//System.out.println("lat is "+GPSLat+" and long is "+GPSLong);
		
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
		
  
  
  
   
 
 } }
	});  
	t1.start();
	// return c;
 }

}
