import java.util.LinkedList;
import java.util.List;


public class Main {




/*

    public static int myVariable = 0;



    private static int threadsNumber = 100;*/

    static Products products= new Products();

    //public MySemaphore mySemaphore = new MySemaphore();
    // public static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws Exception {
        List<ProducerThread> producers = new LinkedList<>();

        int prodsNumber = 30;
        for (int i = 0; i < prodsNumber; i++) {
            producers.add(new ProducerThread("P" + (i+1)));
        }

        List<ConsumerThread> consumers = new LinkedList<>();

        int consNumber = 30;
        for (int i = 0; i < consNumber; i++) {
            consumers.add(new ConsumerThread("C" + (i+1)));
        }

        for (ProducerThread p:
             producers) {
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
