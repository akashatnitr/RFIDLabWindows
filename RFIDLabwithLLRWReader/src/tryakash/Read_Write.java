package tryakash;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;

import processing.core.*;

public class Read_Write{
	public static void main(String[] args) {
		 Read_Write rw = new Read_Write();
		 rw.ACTION();
	}
	private void ACTION(){
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		try {
			pos.connect(pis);
			String str = "Hello Akash";
			byte[] b = str.getBytes();
			pos.write(b);
			 
			int i;
			while((i=pis.read())!=-1){
				System.out.println((char)i);
			}
			pis.close();
			pos.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
				
	}
}