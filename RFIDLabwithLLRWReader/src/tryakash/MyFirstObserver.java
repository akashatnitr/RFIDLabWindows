package tryakash;
 



import java.util.Observable;
import java.util.Observer;
 
public class MyFirstObserver implements Observer {
 
 @Override
 public void update(Observable o, Object arg) {
   
  System.out.println("Second Observer Notified:" + o + "  :  " + arg);
 
 }
 
}
