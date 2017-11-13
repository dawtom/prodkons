import java.util.Random;

public class ConsumerThread extends Thread{

    private String name;

    public ConsumerThread(String name){
        this.name = name;
    }

    private Random r = new Random();

    public void run(){
        while(true){
            Main.products.finishConsuming(
                    Main.products.startConsuming(this.name), this.name);
        }
    }
}
