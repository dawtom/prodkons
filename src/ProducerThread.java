import java.util.Random;

public class ProducerThread extends Thread {

    private String name;
    private Random r = new Random();

    public ProducerThread(String name){
        this.name = name;
    }

    public void run(){
        while(true){
            if (this.name.equals("P1")){
                //Main.products.insert(((r.nextInt() % 5) + 5), this.name);
                Main.products.insert(19,this.name);
            } else{
                Main.products.insert(1,this.name);
            }
        }
    }
}
