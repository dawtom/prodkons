import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ConsumerThread extends Thread{

    private String name;

    public ConsumerThread(String name){
        this.name = name;
    }

    private Random r = new Random();

    public void run(){
        while(true){
            List<Integer> indexes = new LinkedList<>();

            indexes = Main.products.beginConsuming(this.name, (r.nextInt() % 3) + 3);

            //consume

            for (Integer index :
                    indexes) {
                Main.products.buffer.set(index, 0);
            }

//            System.out.println("After consumer: " + Main.products.buffer.toString());



            Main.products.finishConsuming(indexes, this.name);
        }
    }
}
