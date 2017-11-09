import java.util.Random;

public class ProducerThread extends Thread {

    private String name;
    private Random r = new Random();

    public ProducerThread(String name){
        this.name = name;
    }

    public void run(){
        while(true){
            Main.products.insert(((r.nextInt() % 5) + 5), this.name);
        }
    }
}
