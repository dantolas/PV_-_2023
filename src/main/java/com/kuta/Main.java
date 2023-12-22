package com.kuta;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
  public static void main(String args[]){
        ReentrantLock lock = new ReentrantLock();
        AtomicInteger schedulesGenerated = new AtomicInteger(0);
        AtomicInteger schedulesRated = new AtomicInteger(0);
        AtomicInteger schedulesBetterThanInitalShared = new AtomicInteger();
        ConcurrentLinkedQueue<byte[][]> generatedSchedules = new ConcurrentLinkedQueue<>();
        AtomicBoolean stopOrder = new AtomicBoolean(false);
        AtomicBoolean keepGenerating = new AtomicBoolean(true);
        byte [][] bestSchedule = Config.INITIAL_SCHEDULE;
        int[] bestScheduleScore = new int[11];


        PermutationGenerator g = new PermutationGenerator(schedulesGenerated, generatedSchedules, stopOrder,keepGenerating);

        Thread[] generatorPool = {
            new Thread(g)
        };

        Pes e1 = new Pes(generatedSchedules,lock,stopOrder,keepGenerating,schedulesBetterThanInitalShared,schedulesRated,bestSchedule,bestScheduleScore) {
            
        };
        Pes e2 = new Pes(generatedSchedules,lock,stopOrder,keepGenerating,schedulesBetterThanInitalShared,schedulesRated,bestSchedule,bestScheduleScore) {
            
        };
        Pes e3 = new Pes(generatedSchedules,lock,stopOrder,keepGenerating,schedulesBetterThanInitalShared,schedulesRated,bestSchedule,bestScheduleScore) {
            
        };
        Thread[] evaluatorPool = {
            new Thread(e3),
            new Thread(e2),
            new Thread(e1)
        };

        Watchdog watchdog = new Watchdog(
            schedulesGenerated, schedulesRated, lock, generatorPool,
            evaluatorPool, bestSchedule,bestScheduleScore,
            stopOrder,schedulesBetterThanInitalShared);

        Thread t = new Thread(watchdog);

        t.start(); 
  } 
}
