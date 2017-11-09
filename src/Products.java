import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Products {

    private int count = 0;

    private int front = 0;
    private int rear = 0;
    private int toInsertIndex = 0;
    private int toGetIndex = 0;

    private int capacity = 20;
    private List<Integer> buffer = new ArrayList<>();

    private Random r = new Random();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition firstProducer = lock.newCondition();
    private final Condition restOfProducers = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition restOfConsumers = lock.newCondition();





    public Products(){
        for (int i = 0; i < capacity; i++) {
            buffer.add(0);
        }
    }


    //private int buff = 0;

    public void insert(int howMany, String myName){

        lock.lock();
        if (lock.getWaitQueueLength(firstProducer) > 0){
            try{
                restOfProducers.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        while(capacity - count < howMany){
            try{
                firstProducer.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        /*dziura w rozwiązaniu: jest tam gdzie if
        * Jeśli założymy, żę spr że jesteśmy pierwsi, odwieszamy się
        * Jesteśmy pierwsi i chcemy ileś zasobów. W takim razie wieszamy się
        * w while. W javie wtedy proces jest wyrzucony z monitora i musi
        * się znowu ubiegać o wejście do monitora ze wszystkimi
        * Ktoś inny jest pierwszy raz, sprawdza ifa
        * Będą podjadane zasoby, nad którymi nie mamy kontroli
        * W javie musimy załatać dziurę między zwolnieniem a wejściem
        * Zatem if nie ma być jako "czy jest w kolejce" tylko
        * "Czy na pewno ktoś sobie nie zarezerował kolejki"
        * Na następnej kartkówce będzie trzeba to opisać i pokazać
        * jak zakleszczyć powyższe rozwiązanie
        * To zadanie jest bardzo ważne, ma chodzić, na ocenę
        * Na zadanie domowe zaimplementować to ze zrozumieniem.
        * Oprócz tego: zad 4.4.4 (zrobić)
        * Za karę :P:
        * Przeczytać 3 drukarki (4.4.7), stolik dwuosobowy (4.4.9),
        * 4.4.10 (zasoby dwóch typów)*/
        /*while (count + howMany >= capacity){
            try{
                notFull.await();
            } catch (Exception e){
                System.out.println("ERROR, " + e.getMessage());
            }
        }*/

        for (int i = 0; i < howMany; i++) {
            toInsertIndex = (toInsertIndex + 1) % capacity;

            buffer.set(toInsertIndex, r.nextInt()%50 + 50);

            count ++;
        }

        System.out.println("Producer "+myName+" inserted " + howMany + ", buffer is: " + buffer.toString()
         + ", count: " + count);

        restOfProducers.signal();
        firstConsumer.signal();

/*        notEmpty.signal();

        lock.unlock();*/
        lock.unlock();

    }

    public void consume(int howMany, String myName){
        lock.lock();
        /*lock.lock();
        while (count - howMany <= 0){
            try{
                notEmpty.await();

            } catch (Exception e){
                System.out.println("ERROR, " + e.getMessage());
            }
        }*/

        if (lock.getWaitQueueLength(firstConsumer) > 0){
            try{
                restOfConsumers.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        while(count < howMany){
            try{
                firstConsumer.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        for (int i = 0; i < howMany; i++) {
            toGetIndex = (toGetIndex + 1) % capacity;
            buffer.set(toGetIndex, 0);
            count--;
        }

        restOfConsumers.signal();
        firstProducer.signal();

        System.out.println("Consumer "+myName+" consumed "+ howMany +", buffer is: " + buffer.toString()
         + ", count: " + count);

        lock.unlock();
        /*notFull.signal();
        lock.unlock();
*/
    }
}
