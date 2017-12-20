import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class MainAsync {

    public static boolean threadsAreRunning = true;

    public static AtomicInteger alreadyProduced = new AtomicInteger(0);
    public static AtomicInteger operationsNumber = new AtomicInteger(0);
/*

    public static int myVariable = 0;



    private static int threadsNumber = 100;*/

    static Products products= new Products();
    public static final int bufferDelayInMilliseconds = 0;
    public static final int generalThreadsNumber = 700;


    public static void main(String[] args) throws Exception {
        List<ProducerThread> producers = new LinkedList<>();

        int prodsNumber = MainAsync.generalThreadsNumber;
        for (int i = 0; i < prodsNumber; i++) {
            producers.add(new ProducerThread("P" + (i+1)));
        }

        List<ConsumerThread> consumers = new LinkedList<>();

        int consNumber = MainAsync.generalThreadsNumber;
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

        TimeUnit.SECONDS.sleep(10);

        threadsAreRunning = false;


        System.out.println("Already produced: " + alreadyProduced);
        System.out.println("Operations number: " + operationsNumber);
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

    public static void veryDifficultOperationForHalfASecond(){
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        operationsNumber.addAndGet(1);
    }
}
