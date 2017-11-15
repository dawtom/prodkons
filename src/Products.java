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

    private Random r = new Random();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition firstProducer = lock.newCondition();
    private final Condition restOfProducers = lock.newCondition();
    private final Condition firstConsumer = lock.newCondition();
    private final Condition restOfConsumers = lock.newCondition();

    private boolean firstPlaceForProducerIsOccupied = false;
    private boolean firstPlaceForConsumerIsOccupied = false;


    public Products(){
        for (int i = 0; i < capacity; i++) {
            buffer.add(0);
        }
    }

    public int howManyTimesHasFirstProducerBeenRejected = 0;

    public void insert(int howMany, String myName){

        lock.lock();
        //if (lock.getWaitQueueLength(firstProducer) > 0){
        if (firstPlaceForProducerIsOccupied){
            try{
                if (myName.equals("P1")){
                    howManyTimesHasFirstProducerBeenRejected++;
                    //System.out.println("Reject " + howManyTimesHasFirstProducerBeenRejected);
                }
                restOfProducers.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        firstPlaceForProducerIsOccupied = true;
        while(capacity - count < howMany){
            try{
                firstProducer.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }


        if (myName.equals("P1")){
            System.out.println("Rejects: " + howManyTimesHasFirstProducerBeenRejected + "" +
                    "     ::::   random: " + r.nextInt()%50);
            howManyTimesHasFirstProducerBeenRejected = 0;
        }

        for (int i = 0; i < howMany; i++) {
            toInsertIndex = (toInsertIndex + 1) % capacity;

            buffer.set(toInsertIndex, r.nextInt()%50 + 50);

            count ++;
        }

//        System.out.println("Producer "+myName+" inserted " + howMany + ", buffer is: " + buffer.toString()
//         + ", count: " + count);

        firstPlaceForProducerIsOccupied = false;
        restOfProducers.signal();
        firstConsumer.signal();


        lock.unlock();

    }

    public void consume(int howMany, String myName){
        lock.lock();

        //if (lock.getWaitQueueLength(firstConsumer) > 0){
        if (firstPlaceForConsumerIsOccupied){
            try{
                restOfConsumers.await();
            } catch (Exception e){
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        firstPlaceForConsumerIsOccupied = true;
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
        firstPlaceForConsumerIsOccupied = false;
        restOfConsumers.signal();
        firstProducer.signal();

//        System.out.println("Consumer "+myName+" consumed "+ howMany +", buffer is: " + buffer.toString()
//         + ", count: " + count);

        lock.unlock();

    }
}

/*
Współbieżność: ogarnąć artykuł na moodlu (ten z C++),
        zrobić porządnie asynchroniczne prodkons,
        będziemy go w formie kartkówkowej/ustnej omawiać.
        Monitor jest klasą, bufor jest osobną klasą poza monitorem.
        Prod, kons mają podzieloną prod/kons na dwie części,
        monitor ogarnia tylko pozycje.
        Jak zorganizować monitor i przekazywanie przepustek?
        Nie musimy śledzić dokładnie każdego pola i condition
        na tych polach. Możemy posiadać kolejkę elementów pełnych
        i pustych (LIFO).
        Przychodzimy, bierzemy pole z kolejki, które jest wolne
        i idziemy produkować/konsumować.
        NIE zajmujemy się wyciekami zasobów (nic nie wycieka -
        wszystko pięknie działa). Producenci i konsumenci produkują
        i konsumują po jednym elemencie. Żeby oddać zadanie trzeba
        mieć wyniki liczbowe dla zagłodzonego programu, wyniki liczbowe
        dla niezagłodzonego programu, potem patrzymy w kod i znać dobrze ten kod.
        Ogarnąć active object.
*/

