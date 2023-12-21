package com.kuta;


import java.text.DecimalFormat;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;;

public class Watchdog implements Runnable{

    
    private Lock lock;

    private final int MAX_RUNTIME_IN_MINUTES = Config.MAX_RUNTIME_IN_MINUTES;
    private long startTime;

    private Queue<Exception> exceptionsToHandle;
    private AtomicInteger randomSchedulesGenerated;
    private AtomicInteger schedulesPermutated;
    private int schedulesRated;

    private ArrayList<Runnable> GENERATOR_POOL;

    private ArrayList<Runnable> EVALUATOR_POOL;

    
    



    public Watchdog(Lock lock, AtomicInteger randomSchedulesGenerated) {
        this.lock = lock;
        this.randomSchedulesGenerated = randomSchedulesGenerated;

    }

    public Watchdog(AtomicInteger schedulesPermutated,Lock lock) {
        this.lock = lock;
        this.schedulesPermutated = schedulesPermutated;

    }

    public Watchdog(Lock lock, AtomicInteger schedulesPermutated,AtomicInteger randomSchedulesGenerated) {
        this.lock = lock;
        this.randomSchedulesGenerated = randomSchedulesGenerated;
        this.schedulesPermutated = schedulesPermutated;
    }






    @Override
    public void run() {

        this.startTime = System.currentTimeMillis();
        int runtimeInSeconds = 0;

        try {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                runtimeInSeconds = (int) (System.currentTimeMillis() - startTime) / 1000;
                System.out.print("Runtime:" + runtimeInSeconds + "s | ");
                try {
                    if (lock.tryLock(10, TimeUnit.SECONDS)) {
                        System.out.println("Schedules generated:" + (String.format("%,d", schedulesPermutated.get())));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // release lock
                    lock.unlock();
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("The watchdog has run out of heap memory space and will now shut down.");
        }

        
        
        
    }
    
}
