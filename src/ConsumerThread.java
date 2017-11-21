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
            if (index <= Products.getCapacity()){
                Main.products.buffer.set(index, 0);
            }
            //here actual consuming

            Main.products.finishConsuming(index, this.name);
        }
    }
}
