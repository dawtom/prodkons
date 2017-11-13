import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class Products {

    private int count = 0;

    private int front = 0;
    private int rear = 0;
    private int toInsertIndex = 0;
    private int toGetIndex = 0;

    private int capacity = 20;
    private List<Integer> buffer = new ArrayList<>();
    private List<FieldState> states = new ArrayList<>();

    private Random r = new Random();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition firstProducer = lock.newCondition();
    private final Condition restOfProducers = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition restOfConsumers = lock.newCondition();
    private final Condition locked = lock.newCondition();

    private boolean firstPlaceForProducerIsOccupied = false;
    private boolean firstPlaceForConsumerIsOccupied = false;


    public Products(){
        for (int i = 0; i < capacity; i++) {
            buffer.add(0);
            states.add(FieldState.Empty);
        }
    }

    public int beginInserting(String myName){
        int i;
        if (!states.get(toInsertIndex).equals(FieldState.Empty)){
            i = capacity;
        } else{
            i = toInsertIndex;
            states.set(toInsertIndex, FieldState.Occupied);
            toInsertIndex = (toInsertIndex + 1) % capacity;
        }

        System.out.println("Producer " + myName + " began inserting. " + buffer.toString());

        return i;
    }

    public void finishInserting(int i, String myName){
        states.set(i,FieldState.Full);
        if (i == toGetIndex){
            locked.signal();
        }

        System.out.println("Producer " + myName + " finished inserting. " + buffer.toString());
    }

    public int startConsuming(String myName){
        int i = 0;
        if (states.get(toGetIndex).equals(FieldState.Full)){
            try {
                locked.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        i = toGetIndex;
        toGetIndex = (toGetIndex + 1) % capacity;
        System.out.println("Consumer " + myName + " began consuming. " + buffer.toString());
        return i;
    }

    public void finishConsuming(int i, String myName){
        states.set(toGetIndex,FieldState.Empty);
        System.out.println("Consumer " + myName + " finished consuming. " + buffer.toString());
    }
}
