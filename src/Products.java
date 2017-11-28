import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class Products {

    private int count = 0;

    private int front = 0;
    private int rear = 0;
    private int toInsertIndex = 0;
    private int toGetIndex = 0;

    public static int getCapacity() {
        return capacity;
    }

    private static int capacity = 20;
    public List<Integer> buffer = new ArrayList<>();
    private List<FieldState> states = new ArrayList<>();

    private Random r = new Random();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition waitProducer = lock.newCondition();
    private final Condition waitConsumer = lock.newCondition();
    private final Condition giveProducer = lock.newCondition();
    private final Condition locked = lock.newCondition();
    private final Condition [] giveConsumer = new Condition[capacity];

    private boolean firstPlaceForProducerIsOccupied = false;
    private boolean firstPlaceForConsumerIsOccupied = false;

    Queue<Integer> queueEmpty = new LinkedList<>();
    Queue<Integer> queueFull = new LinkedList<>();

    private boolean [] isAvailable = new boolean[capacity];

    AtomicInteger availableElementsNumber = new AtomicInteger(0);





    public Products(){
        for (int i = 0; i < capacity; i++) {
            buffer.add(0);
            states.add(FieldState.Empty);
        }
        for (int i = 0; i < capacity; i++) {
            giveConsumer[i] = lock.newCondition();
        }
        for (int i = 0; i < capacity; i++) {
            queueEmpty.offer(i);
        }

/*        for (int i = 0; i < capacity; i++) {
            isAvailable
        }*/

    }

    public List<Integer> beginInserting(String myName, int howMany){
        lock.lock();
        //
        List<Integer> result = new LinkedList<>();
        while (/*queueEmpty.isEmpty()*/queueEmpty.size() < howMany){
            try{
                waitProducer.await();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Queue empty: " + queueEmpty.toString());

        for (int i = 0; i < howMany; i++) {
            int j = queueEmpty.poll();
            queueFull.offer(j);
            result.add(j);
        }


        lock.unlock();
        return result;
    }

    public void finishInserting(List<Integer> indexes, String myName){
        lock.lock();

        try{
            waitConsumer.signal();
        } catch (Exception e){
            e.printStackTrace();
        }
        lock.unlock();
    }

    public  List<Integer> beginConsuming(String myName, int howMany){
        lock.lock();
        List<Integer> result = new LinkedList<>();
        while (/*queueFull.isEmpty()*/ queueFull.size() < howMany){
            try{
                waitConsumer.await();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < howMany; i++){

            int j = queueFull.poll();

            result.add(j);
        }
        lock.unlock();
        return result;
    }
    public  void finishConsuming(List<Integer> indexes, String myName){
        lock.lock();
        for (Integer x :
                indexes) {
            queueEmpty.offer(x);
        }

        waitProducer.signal();

        lock.unlock();
    }



}
