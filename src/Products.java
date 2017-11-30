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
    private final ReentrantLock producerLock = new ReentrantLock();
    private final ReentrantLock consumerLock = new ReentrantLock();
    private final Condition firstProducerWait = producerLock.newCondition();
    private final Condition restProducersWait = producerLock.newCondition();
    private final Condition firstConsumerWait = consumerLock.newCondition();
    private final Condition waitRestConsumers = consumerLock.newCondition();

    private final Condition locked = producerLock.newCondition();
    private final Condition [] giveConsumer = new Condition[capacity];

    private boolean producerFirstPlaceIsOccupied = false;
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
            giveConsumer[i] = producerLock.newCondition();
        }
        for (int i = 0; i < capacity; i++) {
            queueEmpty.offer(i);
        }

/*        for (int i = 0; i < capacity; i++) {
            isAvailable
        }*/

    }

    public List<Integer> beginInserting(String myName, int howMany){
        producerLock.lock();
        //

        if (producerFirstPlaceIsOccupied){
            try {
                restProducersWait.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        producerFirstPlaceIsOccupied = true;
        List<Integer> result = new LinkedList<>();
        while (/*queueEmpty.isEmpty()*/queueEmpty.size() < howMany){
            try{
                firstProducerWait.await();
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



        producerLock.unlock();
        return result;
    }

    public void finishInserting(List<Integer> indexes, String myName){
        producerLock.lock();

        if (producerLock.hasWaiters(restProducersWait)) {
            restProducersWait.signal();
        } else {
            producerFirstPlaceIsOccupied = false;
        }

        try{
            firstConsumerWait.signal();
        } catch (Exception e){
            e.printStackTrace();
        }
        producerLock.unlock();
    }

    public  List<Integer> beginConsuming(String myName, int howMany){
        consumerLock.lock();
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
        consumerLock.unlock();
        return result;
    }
    public  void finishConsuming(List<Integer> indexes, String myName){
        consumerLock.lock();
        for (Integer x :
                indexes) {
            queueEmpty.offer(x);
        }

        waitProducer.signal();

        consumerLock.unlock();
    }



}
