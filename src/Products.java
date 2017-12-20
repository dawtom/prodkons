import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



class Products {

    List<Integer> buffer = new ArrayList<>();

//    private final ReentrantLock producerLock = new ReentrantLock();
//    private final ReentrantLock consumerLock = new ReentrantLock();

    private final ReentrantLock emptyQueueLock = new ReentrantLock();
    private final ReentrantLock fullQueueLock = new ReentrantLock();

    private final Condition firstOnEmptyQueue = emptyQueueLock.newCondition();
    private final Condition restOnEmptyQueue = emptyQueueLock.newCondition();
    private final Condition firstOnFullQueue = fullQueueLock.newCondition();
    private final Condition restOnFullQueue = fullQueueLock.newCondition();

//    private final ReentrantLock lock = new ReentrantLock();
//    private final Condition firstProducerWait = lock.newCondition();
//    private final Condition restProducersWait = lock.newCondition();
//    private final Condition firstConsumerWait = lock.newCondition();
//    private final Condition restConsumersWait = lock.newCondition();


//    private boolean producerFirstPlaceIsOccupied = false;
//    private boolean consumerFirstPlaceIsOccupied = false;

    private boolean emptyQueueFirstPlaceIsOccupied = false;
    private boolean fullQueueFirstPlaceIsOccupied = false;


    private Queue<Integer> queueEmpty = new LinkedList<>();
    private Queue<Integer> queueFull = new LinkedList<>();






    Products(){

        int capacity = 200000;
        for (int i = 0; i < capacity; i++) {
            buffer.add(0);
        }

        for (int i = 0; i < capacity; i++) {
            queueEmpty.offer(i);
        }
    }

    List<Integer> beginInserting(int howMany){
        emptyQueueLock.lock();

        if (emptyQueueFirstPlaceIsOccupied){
            try {
                restOnEmptyQueue.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        emptyQueueFirstPlaceIsOccupied = true;

        List<Integer> result = new LinkedList<>();
        while (queueEmpty.size() < howMany){
            try{
                firstOnEmptyQueue.await();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        //System.out.println("Queue empty: " + queueEmpty.toString());

        for (int i = 0; i < howMany; i++) {
            int j = queueEmpty.poll();
            result.add(j);
        }


        if (emptyQueueLock.hasWaiters(restOnEmptyQueue)) {
            restOnEmptyQueue.signal();
        } else {
            emptyQueueFirstPlaceIsOccupied = false;
        }

        emptyQueueLock.unlock();
        return result;
    }

    void finishInserting(List<Integer> indexes){
        fullQueueLock.lock();

        for (Integer index : indexes) {
            queueFull.offer(index);
        }

        try{
            firstOnFullQueue.signal();
        } catch (Exception e){
            e.printStackTrace();
        }

        fullQueueLock.unlock();
    }

    List<Integer> beginConsuming(int howMany){
        fullQueueLock.lock();

        if (fullQueueFirstPlaceIsOccupied){
            try {
                restOnFullQueue.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        fullQueueFirstPlaceIsOccupied = true;

        List<Integer> result = new LinkedList<>();
        while (queueFull.size() < howMany){
            try{
                firstOnFullQueue.await();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        //System.out.println("Queue full: " + queueFull.toString());
        for (int i = 0; i < howMany; i++){

            int j = queueFull.poll();

            result.add(j);
        }

        if (fullQueueLock.hasWaiters(restOnFullQueue)) {
            restOnFullQueue.signal();
        } else {
            fullQueueFirstPlaceIsOccupied = false;
        }

        fullQueueLock.unlock();

        return result;
    }
    void finishConsuming(List<Integer> indexes){
        emptyQueueLock.lock();

        for (Integer x :
                indexes) {
            queueEmpty.offer(x);
        }

        firstOnEmptyQueue.signal();

        emptyQueueLock.unlock();
    }
}
