package com.kuta;


import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;;

public class Watchdog implements Runnable{

    private Lock lock;

    private final int MAX_RUNTIME_IN_MINUTES = Config.MAX_RUNTIME_IN_MINUTES;
    private Queue<Exception> exceptionsToHandle;
    private long startTime;
    private AtomicInteger schedulesGenerated;
    private int schedulesRated;

    private ArrayList<Runnable> GENERATOR_POOL;

    private ArrayList<Runnable> EVALUATOR_POOL;

    
    



    public Watchdog(Lock lock, AtomicInteger schedulesGenerated) {
        this.lock = lock;
        this.schedulesGenerated = schedulesGenerated;

        this.startTime = System.currentTimeMillis();
    }






    @Override
    public void run() {

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
                        System.out.println("Schedules generated:" + schedulesGenerated);
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
