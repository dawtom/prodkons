import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ProducerThread extends Thread {

    private String name;
    private Random r = new Random();

    ProducerThread(String name){
        this.name = name;
    }

    public void run(){
        while(MainAsync.threadsAreRunning){
            List<Integer> indexes;
            indexes =  MainAsync.products.beginInserting(r.nextInt(999));


            for (Integer i :
                    indexes) {
                MainAsync.products.buffer.set(i,((r.nextInt())%45)+55);
            }
            MainAsync.alreadyProduced.addAndGet(indexes.size());

            try {
                TimeUnit.MILLISECONDS.sleep(MainAsync.bufferDelayInMilliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            System.out.println("After producer: " + MainAsync.products.buffer.toString());


            MainAsync.veryDifficultOperationForHalfASecond();

            MainAsync.products.finishInserting(indexes);


        }
    }
}
