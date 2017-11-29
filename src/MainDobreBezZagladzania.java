
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainDobreBezZagladzania {




/*

    public static int myVariable = 0;



    private static int threadsNumber = 100;*/

    public static Products products= new Products();
    private static int prodsNumber = 40;
    private static int consNumber = 2;

    //public MySemaphore mySemaphore = new MySemaphore();
    // public static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) {
        List<ProducerThread> producers = new LinkedList<>();

        for (int i = 0; i < prodsNumber; i++) {
            producers.add(new ProducerThread("P" + (i+1)));
        }

        List<ConsumerThread> consumers = new LinkedList<>();

        for (int i = 0; i < consNumber; i++) {
            consumers.add(new ConsumerThread("C" + (i+1)));
        }

        for (ProducerThread p:
             producers) {
            try{
                Thread.sleep(100);
            } catch (Exception e){

            }
            p.start();
        }
        for (ConsumerThread c :
                consumers) {
            c.start();
        }


        /*List<Thread> allThreads = new ArrayList<>();

        for (int i = 0; i < threadsNumber; i++) {
            allThreads.add(new Thread(new MyThread()));
        }

        for (int i = 0; i < threadsNumber; i++) {
            allThreads.get(i).start();
        }

        for (int i = 0; i < threadsNumber; i++) {
            try{
                allThreads.get(i).join();
            } catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println("********************************");
            }
        }

        System.out.println("Result is " + myVariable);

        //System.out.println("Hello World!");*/
    }
}
