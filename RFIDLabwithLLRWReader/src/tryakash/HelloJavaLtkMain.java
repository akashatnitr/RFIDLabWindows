package tryakash;
public class HelloJavaLtkMain
{
    public static void main(String[] args) throws InterruptedException
    {
    	//System.out.println("Hello RFID Lab. Enter S to start Q to Quit");
        HelloJavaLtk app = new HelloJavaLtk();
        
          
        System.out.println("Starting reader.");
        app.run("169.254.1.1");
        Thread.sleep(3000);
        System.out.println(app.tagsEPCRead);
        System.out.println("Stopping reader.");
        app.stop();
        System.out.println("Exiting application.");
        System.exit(0);
    }
}