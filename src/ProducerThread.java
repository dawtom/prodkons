import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ProducerThread extends Thread {

    private String name;
    private Random r = new Random();

    public ProducerThread(String name){
        this.name = name;
    }

    public void run(){
        while(true){
            List<Integer> indexes = new LinkedList<>();
            indexes =  Main.products.beginInserting(this.name, (r.nextInt() % 3) + 3);

            //System.out.println("i: " + index + ", capacity: " + Products.getCapacity());
            //produce
            //if (index < Products.getCapacity()){

            for (Integer i :
                    indexes) {
                Main.products.buffer.set(i,((r.nextInt())%45)+55);
            }

            //}

            Main.products.finishInserting(indexes, this.name);

        }
    }
}
