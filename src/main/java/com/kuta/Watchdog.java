package com.kuta;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;;

public class Watchdog implements Runnable{

    
    private ReentrantLock lock;

    private final int MAX_RUNTIME_IN_SECONDS = Config.MAX_RUNTIME_IN_SECONDS;
    private long startTime;

    //private AtomicInteger randomSchedulesGenerated;
    private AtomicInteger schedulesPermutated;
    private AtomicInteger schedulesRated;
    private AtomicInteger betterThanInitialShared;

    private byte[][] bestSchedule;
    private int[] bestScheduleScore;

    private Thread[] GENERATOR_POOL;

    private Thread[] EVALUATOR_POOL;

    private  AtomicBoolean THREAD_STOP_ORDER;

    
    
    


    
    public Watchdog(
        AtomicInteger schedulesPermutated,AtomicInteger schedulesProcessed,
        ReentrantLock lock, Thread[] generatorPool, Thread[] evaluatorPool,
        byte[][] bestScheduleShared,int[] bestScheduleScore, AtomicBoolean stopOrder,
        AtomicInteger betterThanInitialShared
        ) {
        
        this.lock = lock;
        this.schedulesPermutated = schedulesPermutated;
        this.schedulesRated = schedulesProcessed;
        this.EVALUATOR_POOL = evaluatorPool;
        this.GENERATOR_POOL = generatorPool;
        this.bestSchedule = bestScheduleShared;
        this.bestScheduleScore = bestScheduleScore;
        this.THREAD_STOP_ORDER = stopOrder;
        this.betterThanInitialShared = betterThanInitialShared;
        

    }

    





    @Override
    public void run() {

        this.startTime = System.currentTimeMillis();
        System.out.println("Initial schedule");

        try {

            startGenerators();
            startEvaluators();

            while (getRuntimeInSeconds(startTime) < MAX_RUNTIME_IN_SECONDS) {

                Thread.sleep(1000);

                printRuntimeInformation(startTime);

            }

            this.THREAD_STOP_ORDER.set(true);

            for (Thread Evaluator : EVALUATOR_POOL) {
                Evaluator.join();
            }

            for (Thread Generator : GENERATOR_POOL) {
                Generator.join();
            }

            printSchedule(bestSchedule);
        } catch (OutOfMemoryError e) {
            System.out.println("The watchdog has run out of heap memory space and will now shut down.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Watchdog was interrupted.");    
            e.printStackTrace();
        } catch(Exception e){
            System.out.println("Unexpected exception caught.");
            e.printStackTrace();
        }

        System.out.println("Watchdog ended");

        

    }

    private void startGenerators(){
        for (Thread thread : GENERATOR_POOL) {
            thread.start();
        }
    }

    private void startEvaluators(){
        for (Thread thread : EVALUATOR_POOL) {
            thread.start();
        }
    }

    private void printRuntimeInformation(long startTime) throws InterruptedException{
        int runtimeSpaceBuffer = 19;
        int scheduleInfoSpaceBuffer = 110;

        String runtime = "Runtime :"+getRuntimeInSeconds(startTime);
        runtime+= "s";
        for (int i = 0; i < runtimeSpaceBuffer - runtime.length(); i++) {
            runtime+=" ";
        }

        runtime+= "| ";

        

        String scheduleInfo = getSchedulesInformation();
        for(int i = 0; i < scheduleInfoSpaceBuffer - scheduleInfo.length(); i++){
            scheduleInfo += " ";
        }
        System.out.println(runtime+scheduleInfo);
    }

    private String getSchedulesInformation() throws InterruptedException{
        StringBuilder returnString = new StringBuilder();
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
            
            returnString.append("Schedules generated:" + (String.format("%,d", schedulesPermutated.get())));
            returnString.append(" | ");
            returnString.append("Schedules rated:" + (String.format("%,d", schedulesRated.get())));
            returnString.append(" | ");
            returnString.append("Schedules better than original:"+(String.format("%,d", betterThanInitialShared.get())));
        }
        else{
            returnString.append("Could not get schedule information in time.");
        }
        } catch (Exception e) {
            System.out.println("Watchdog Lock Exception occured");
        }finally{
            if(lock.isHeldByCurrentThread()) lock.unlock();
        }
        
        

        return returnString.toString();

    }

    private int getRuntimeInSeconds(long startTime){
        return (int)((System.currentTimeMillis() - startTime) / 1000);
    }

    /**
     * Helper method to print schedule information
     * by printing the Subject mapped for each byte of given array.
     * @param array
     */
    private void printSchedule(byte[][] schedule){

        String[] days = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
        };
        byte currentByte = 0b0;

        System.out.println("Schedule rating: "+bestScheduleScore[10]);
        
        for (int i = 0; i < schedule.length; i++) {
            System.out.println("| "+days[i]+" |");
            for (byte b : schedule[i]) {
                
                if(b  == Config.EMPTY_LESSON){
                    System.out.println("| - - - - - - - - - -");
                    continue;
                }
                System.out.println(Config.SUBJECT_BYTE_MAP.get(b));
            }
            System.out.println("==================================");
        }

    }
    
}
