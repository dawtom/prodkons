import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ConsumerThread extends Thread{

    private String name;

    public ConsumerThread(String name){
        this.name = name;
    }

    private Random r = new Random();

    public void run(){
        while(MainAsync.threadsAreRunning){
            List<Integer> indexes = new LinkedList<>();

            indexes = MainAsync.products.beginConsuming(r.nextInt(999));

            //consume

            for (Integer index :
                    indexes) {
                MainAsync.products.buffer.set(index, 0);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(MainAsync.bufferDelayInMilliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            MainAsync.veryDifficultOperationForHalfASecond();

//            System.out.println("After consumer: " + MainAsync.products.buffer.toString());



            MainAsync.products.finishConsuming(indexes);
        }
    }
}
