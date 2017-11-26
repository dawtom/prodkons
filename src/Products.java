import java.lang.reflect.Field;
import java.util.*;
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

    public int beginInserting(String myName){
        lock.lock();
        //
        while (queueEmpty.isEmpty()){
            try{
                //lock.lock();
                waitProducer.await();
                //lock.unlock();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Queue empty: " + queueEmpty.toString());
        int i = queueEmpty.poll();
        queueFull.offer(i);
        isAvailable[i] = false;
        try{
//            lock.lock();
            waitConsumer.signal();
//            lock.unlock();
        } catch (Exception e){
            e.printStackTrace();
        }
        lock.unlock();
//        System.out.println("Producer " + myName + " began inserting. " + buffer.toString() + ", " +
//                "index: " + toInsertIndex);
        return i;
    }

    public  void finishInserting(int i, String myName){
//        lock.lock();
        isAvailable[i] = true;
        //giveConsumer[i].signal();
//        System.out.println("Producer " + myName + " finished inserting. " + buffer.toString() + ", " +
//                "i: " + i);
//        lock.unlock();
    }

    public  int beginConsuming(String myName){
        lock.lock();
        while (queueFull.isEmpty()){
            try{
//                lock.lock();
                waitConsumer.await();
//                lock.unlock();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Queue full: " + queueFull.toString());
        int i = queueFull.poll();
        if (!isAvailable[i]){
            try{
                waitConsumer.await();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
//        System.out.println("Consumer " + myName + " began consuming. " + buffer.toString());
        lock.unlock();
        return i;
    }
    public  void finishConsuming(int i, String myName){
        lock.lock();
        queueEmpty.offer(i);
//        lock.lock();
        waitProducer.signal();
//        lock.unlock();
//        System.out.println("Consumer " + myName + " finished consuming. " + buffer.toString());

        lock.unlock();
    }


    /*public synchronized int beginInserting(String myName){
        lock.lock();
        System.out.println("Producer " + myName + " began inserting. " + buffer.toString() + ", " +
                "index: " + toInsertIndex);
        System.out.println("States: " + states.toString());
        int i = -1;
        if (!states.get(toInsertIndex).equals(FieldState.Empty)){
            toInsertIndex = (toInsertIndex + 1) % capacity;
            i = capacity;
            lock.unlock();
            return i;
        } else{
            i = toInsertIndex;
            states.set(toInsertIndex, FieldState.Occupied);
            toInsertIndex = (toInsertIndex + 1) % capacity;
        }


        lock.unlock();

        return i;
    }

    public synchronized void finishInserting(int i, String myName){
        lock.lock();
        if (i < capacity){
            //if (i == toGetIndex){
            states.set(i,FieldState.Full);


            //}
        }
        if (i == toInsertIndex){
            locked.signal();
        }

        System.out.println("Producer " + myName + " finished inserting. " + buffer.toString() + ", " +
                "i: " + i);
        lock.unlock();
    }

    public synchronized int beginConsuming(String myName){
        lock.lock();
        int i = 0;
        while (!states.get(toGetIndex).equals(FieldState.Full)){
            try {


                locked.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        i = toGetIndex;
        toGetIndex = (toGetIndex + 1) % capacity;
        System.out.println("Consumer " + myName + " began consuming. " + buffer.toString());
        lock.unlock();
        return i;
    }

    public synchronized void finishConsuming(int i, String myName){
        lock.lock();
        states.set(toGetIndex,FieldState.Empty);
        System.out.println("Consumer " + myName + " finished consuming. " + buffer.toString());
        System.out.println("States: " + states.toString());
        lock.unlock();
    }*/
}
