package tryakash;

 

public class test2 {
 
 public static void main(String[] args) {
   
  int a = 3;
  int b = 4;
   
  System.out.println("Starting");
  MyObservable ob = new MyObservable();
   
  // Add observers
  System.out.println("Adding observers");
  //ob.addObserver(new MyFirstObserver());
  //ob.addObserver(new MySecondObserver());
   
  System.out.println("Executing Sum :  " + a + " + " + b);
  ob.sum(a, b);
  System.out.println("Finished");
   
 }
}