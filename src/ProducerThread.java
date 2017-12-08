import java.util.List;
import java.util.Random;

public class ProducerThread extends Thread {

    private String name;
    private Random r = new Random();

    ProducerThread(String name){
        this.name = name;
    }

    public void run(){
        while(true){
            List<Integer> indexes;
            indexes =  Main.products.beginInserting(this.name, (r.nextInt() % 3) + 3);


            for (Integer i :
                    indexes) {
                Main.products.buffer.set(i,((r.nextInt())%45)+55);
            }

//            System.out.println("After producer: " + Main.products.buffer.toString());

            Main.products.finishInserting(indexes, this.name);

        }
    }
}
