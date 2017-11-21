import java.util.Random;

public class ProducerThread extends Thread {

    private String name;
    private Random r = new Random();

    public ProducerThread(String name){
        this.name = name;
    }

    public void run(){
        while(true){
            int index = Main.products.beginInserting(this.name);

            //System.out.println("i: " + index + ", capacity: " + Products.getCapacity());
            if (index < Products.getCapacity()){
                Main.products.buffer.set(index,((r.nextInt())%45)+55);
            }
            //here insert to an external buffer

            Main.products.finishInserting(index, this.name);

        }
    }
}
