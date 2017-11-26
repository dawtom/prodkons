import java.util.Random;

public class ConsumerThread extends Thread{

    private String name;

    public ConsumerThread(String name){
        this.name = name;
    }

    private Random r = new Random();

    public void run(){
        while(true){
            int index = Main.products.beginConsuming(this.name);

            //consume

                Main.products.buffer.set(index, 0);


            Main.products.finishConsuming(index, this.name);
        }
    }
}
