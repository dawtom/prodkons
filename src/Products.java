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
                //lock.lock();
                System.out.println("Producer is waiting:, sizeEmpty is " + queueEmpty.size()
                 + "howMany is: " + howMany);
                waitProducer.await();
                //lock.unlock();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Queue empty: " + queueEmpty.toString());

        for (int i = 0; i < howMany; i++) {
            int j = queueEmpty.poll();
            //isAvailable[j] = false;
            queueFull.offer(j);
            result.add(j);
        }


        lock.unlock();
//        System.out.println("Producer " + myName + " began inserting. " + buffer.toString() + ", " +
//                "index: " + toInsertIndex);
        return result;
    }

    public void finishInserting(List<Integer> indexes, String myName){
        lock.lock();
        /*for (Integer x:
             indexes) {
            isAvailable[x] = true;
        }*/
        try{
//            lock.lock();
            waitConsumer.signal();
//            lock.unlock();
        } catch (Exception e){
            e.printStackTrace();
        }
        //giveConsumer[i].signal();
//        System.out.println("Producer " + myName + " finished inserting. " + buffer.toString() + ", " +
//                "i: " + i);
        lock.unlock();
    }

    public  List<Integer> beginConsuming(String myName, int howMany){
        lock.lock();
        List<Integer> result = new LinkedList<>();
        while (/*queueFull.isEmpty()*/ queueFull.size() < howMany){
            try{
//                lock.lock();
                System.out.println("Consumer is waiting:, sizeFull is " + queueFull.size()
                        + "howMany is: " + howMany);
                waitConsumer.await();
//                lock.unlock();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < howMany; i++){
            String str = "null";
            String size = "nll";
            if (queueFull != null){
                str = queueFull.toString();
                size = "" + queueFull.size();
            }
            System.out.println("****");
            System.out.println("Thread name: " + myName);
            System.out.println("Queue full: " + str);
            System.out.println("queue full size:" + size);
            System.out.println("i: " + i);
            System.out.println("How many: " + howMany);
            System.out.println("****");
            int j = queueFull.poll();

            /*while (!isAvailable[j]){
                queueFull.offer(j);
                j = queueFull.poll();
                *//*try{
                    waitConsumer.await();
                } catch (Exception e){
                    e.printStackTrace();
                }*//*
            }*/
            result.add(j);
        }
//        System.out.println("Consumer " + myName + " began consuming. " + buffer.toString());
        lock.unlock();
        return result;
    }
    public  void finishConsuming(List<Integer> indexes, String myName){
        lock.lock();
        for (Integer x :
                indexes) {
            queueEmpty.offer(x);
        }

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
