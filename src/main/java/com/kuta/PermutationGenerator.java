package com.kuta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;

/**
 * This class generates different permutations of my school schedule
 * There is an INITIAL SCHEDLE defined, and from there permutations are generated.
 * 
 * Schedules are stored as 2D arrays (essentialy matrices) of bytes.
 * This is for memory storage reasons, so that each schedule takes up the least amount of memory. (Yes it can very likely be more optimized in that regard)
 * 
 * Schedules are added to a shared space between threads.
 * Schedules generated are counted and added to a shared variable between threads.
 * 
 * 
 */
public class PermutationGenerator implements Runnable{
    
    public final byte[][] INITIAL_SCHEDULE = {
        {0b00001000,0b00001001,0b00000100,0b00000110,0b00001010,0b1111111,0b00000000,0b00000000,0b1111111,0b1111111},
        {0b00001010,0b00001111,0b00001011,0b00001011,0b00000110,0b00000101,0b1111111,0b00000111,0b1111111,0b1111111},
        {0b00001110,0b00000100,0b00010000,0b00010000,0b00000101,0b00001010,0b00001100,0b1111111,0b1111111,0b1111111,},
        {0b00001001,0b00001010,0b00001110,0b00000001,0b00000110,0b00000100,0b00000011,0b1111111,0b1111111,0b1111111},
        {0b1111111,0b00001101,0b00001101,0b00000110,0b00000111,0b00000010,0b00000010,0b1111111,0b1111111,0b1111111}
    };

    private final int ADD_TO_QUEUE_THRESHOLD = 10_000;

    private Lock lock;
    private AtomicInteger schedulesGeneratedCount;

    private final ArrayList<Queue<byte[]>> UNIQUE_DAY_PERMUTATIONS = new ArrayList<>() {{
        add(new LinkedList<byte[]>());
        add(new LinkedList<byte[]>());
        add(new LinkedList<byte[]>());
        add(new LinkedList<byte[]>());
        add(new LinkedList<byte[]>());
    }};
    private ConcurrentLinkedQueue<byte[][]> generatedSchedules; 

    /**
     * 
     * @param lock - Lock shared between threads 
     * @param schedulesGeneratedCount - Variable to store the amount of generated schedules
     * @param generatedSchedules - Shared space for accessing and adding generated schedules
     */
    public PermutationGenerator(Lock lock,AtomicInteger schedulesGeneratedCount, ConcurrentLinkedQueue<byte[][]> generatedSchedules){
        this.lock = lock;
        this.schedulesGeneratedCount = schedulesGeneratedCount;
        this.generatedSchedules = generatedSchedules;
    }
   

    /**
     * Generates a new schedule, where only the specified day is changed.
     * The res of the schedule matches INITIAL_SCHEDULE
     * 
     * @param uniqueDayPermutationsCopy - A copy of the list containing all unique permutations for given day
     * @param day - Which day is supposed to be changed for a permutation
     * @return - a new 2D array of bytes representing a schedule
     */
    private byte[][] generateScheduleFromDayPermutation(Queue<byte[]> uniqueDayPermutationsCopy,int day){
        byte[][] newSchedule = new byte[][] {
            INITIAL_SCHEDULE[0],
            INITIAL_SCHEDULE[1],
            INITIAL_SCHEDULE[2],
            INITIAL_SCHEDULE[3],
            INITIAL_SCHEDULE[4],
        };
        newSchedule[day] = uniqueDayPermutationsCopy.poll();

        if(newSchedule[day] == null){
            return null;
        }

        return newSchedule;
    }

    /**
     * Generates a schedule for every permutation for every day of the week.
     * It adds generated schedules to shared space between threads every time 
     * the temporary holder reaches size specified by ADD_TO_QUEUE_THRESHOLD 
     * @param generatedSchedulesTempHolder - A queue to temporarily hold generated schedules,
     * as to not slow down all threads by constanly waiting for access to shared space
     */
    private void generateScheduleFromAllDayPermutations(Queue<byte[][]> generatedSchedulesTempHolder){
        Queue<byte[]> dayPermutationsCopy;
        for(int i = 0; i < UNIQUE_DAY_PERMUTATIONS.size();i++){
            dayPermutationsCopy = new LinkedList<>(UNIQUE_DAY_PERMUTATIONS.get(i));

            while (true) {


                byte[][] scheduleFromDayPermutation = generateScheduleFromDayPermutation(dayPermutationsCopy,i);
                if(scheduleFromDayPermutation == null) break;


                generatedSchedulesTempHolder.offer(scheduleFromDayPermutation);
                if(generatedSchedulesTempHolder.size()% ADD_TO_QUEUE_THRESHOLD == 0){
                    this.generatedSchedules.addAll(generatedSchedulesTempHolder);
                    addToScheduleCount(generatedSchedulesTempHolder.size());
                    generatedSchedulesTempHolder.clear();
                }
            }

            this.generatedSchedules.addAll(generatedSchedulesTempHolder);
            addToScheduleCount(generatedSchedulesTempHolder.size());
            generatedSchedulesTempHolder.clear();

        }
    }

    /**
     * An implementation of Heap's algorhitm for generating all possible permutations
     * source : https://en.wikipedia.org/wiki/Heap%27s_algorithm
     * 
     * 
     * Generates all permutations of a day in the week,
     * and adds them into UNIQUE_DAY_PERMUTATIONS list.
     * This should result in 5x10! different permutations for all days of the week,
     * so 18_144_000 different permutations
     * 
     * @param n - n! permutations will be generated
     * @param day - Array of bytes representing a day in the schedule 
     * @return - Number of permutations generated if there is need for that information
     */    
    private int generateDayPermutations(int n,byte[] daySchedule,Queue<byte[]> generatedPermutationsTempHolder){
        
        int permsGenerated = 1;
        int[] c = new int[n];
        int i = 0;
        for(i = 0; i < n; i++){
            c[i] = 0;
        }

        generatedPermutationsTempHolder.offer(daySchedule);

        i = 1;
        while (i < n) {
            if(c[i] < i){
                if(i%2==0) swap(daySchedule, 0, i);
                else swap(daySchedule, c[i], i);

                c[i]++;
                i=1;
                permsGenerated++;
                generatedPermutationsTempHolder.offer(daySchedule);
                continue;

            }
            
            c[i] = 0;
            i++;
        }
        return permsGenerated;
    }

    /**
     * Generates permutations for all days of the week
     * with the use of the generateDayPermutations method
     * 
     */
    private void generateDayPermutationsForAllDays(){
        Queue<byte[]> generatedDayPermutationsTempHolder = new LinkedList<>();
        for(int i = 0;i < INITIAL_SCHEDULE.length; i++){
            generateDayPermutations(10, INITIAL_SCHEDULE[i],generatedDayPermutationsTempHolder);
            this.UNIQUE_DAY_PERMUTATIONS.get(i).addAll(generatedDayPermutationsTempHolder);
            generatedDayPermutationsTempHolder.clear();
        }
    }





    /**
     * Generates a combination of different day permutations.
     * It leaves the current day as is defined by INITIAL_SCHEDULE,
     * and changes every other day for a permutation of that day.
     * 
     * @param currentDay - Which day is left intact and generated around
     * @param permutationsCopy - A copy of all permutations
     * @return - A new 2D array of bytes representing a schedule
     */
    private byte[][] generateCombination(int currentDay,ArrayList<Queue<byte[]>> permutationsCopy){
        byte[][] newSchedule = new byte[][] {
            INITIAL_SCHEDULE[0],
            INITIAL_SCHEDULE[1],
            INITIAL_SCHEDULE[2],
            INITIAL_SCHEDULE[3],
            INITIAL_SCHEDULE[4],
        };

        for(int dayIndex = 0; dayIndex < 5;dayIndex++){
            if(dayIndex == currentDay) continue;
            if(permutationsCopy.get(dayIndex).peek() == null){
                System.out.println("Permutation queue empty"+dayIndex);
                return null;
            }
            byte[] dayPermutation = Arrays.copyOf(permutationsCopy.get(dayIndex).poll(),10);
            
            newSchedule[currentDay] = dayPermutation; 

        };
        return newSchedule;
    }

    /**
     * Generates combinations for every day
     * using the generate combination method.
     * 
     * NOTE: The reason for the ugly copy creation is that
     * a deep copy of all permutations has to be created,
     * and ArrayList has no good way of creating a deep copy.
     * 
     * @param generatedSchedulesTempHolder - A queue to temporarily hold generated schedules,
     * as to not slow down all threads by constanly waiting for access to shared space.
     */
    private void generateCombinations(Queue<byte[][]> generatedSchedulesTempHolder){

        ArrayList<Queue<byte[]>> permutationsCopy = new ArrayList<>(){{
            add(new LinkedList<byte[]>());
            add(new LinkedList<byte[]>());
            add(new LinkedList<byte[]>());
            add(new LinkedList<byte[]>());
            add(new LinkedList<byte[]>());
        }};
        
        for(int i = 0; i < UNIQUE_DAY_PERMUTATIONS.size(); i++){

            for(int j = 0;  j< permutationsCopy.size();j++){
                permutationsCopy.set(j, new LinkedList<>(UNIQUE_DAY_PERMUTATIONS.get(j)));

            }
                        
            while (true) {
                byte[][] scheduleFromInitialDay = generateCombination(i,permutationsCopy);
                if(scheduleFromInitialDay == null){ System.out.println("Break");break;}          
                generatedSchedulesTempHolder.offer(scheduleFromInitialDay);

                if(generatedSchedulesTempHolder.size() >= ADD_TO_QUEUE_THRESHOLD){
                    this.generatedSchedules.addAll(generatedSchedulesTempHolder);
                    addToScheduleCount(generatedSchedulesTempHolder.size());
                    generatedSchedulesTempHolder.clear();
                }
            }

            this.generatedSchedules.addAll(generatedSchedulesTempHolder);
            addToScheduleCount(generatedSchedulesTempHolder.size());
            generatedSchedulesTempHolder.clear();
        }
        permutationsCopy.clear();
        
    }

   




    @Override
    public void run() {

        System.out.println("Generator started :"+Thread.currentThread().getName());

        Queue<byte[][]> generatedSchedulesTempHolder = new LinkedList<>();
        
        try {
            generateDayPermutationsForAllDays();    
        } catch (Exception e) {
            System.out.println("PermutationGenerator failed generating day permutations. TheadName:"+Thread.currentThread().getName()); 
            
        }

        try {
            generateScheduleFromAllDayPermutations(generatedSchedulesTempHolder); 
        } catch (Exception e) {

            System.out.println("PermutationGenerator failed generating schedules from day permutations. TheadName:"
                    + Thread.currentThread().getName());
            e.printStackTrace();
        }

        try {
            generateCombinations(generatedSchedulesTempHolder);
        } catch (Exception e) {
            System.out.println("PermutationGenerator failed generating schedules combinations. TheadName:"
                    + Thread.currentThread().getName());
                    e.printStackTrace();
        }

        UNIQUE_DAY_PERMUTATIONS.clear();

        System.out.println("Generator ended :"+Thread.currentThread().getName());
    }

    /**
     * Helper method to swap two array values
     * @param array 
     * @param i
     * @param j
     */
    private void swap(byte[] array,int i, int j){
        byte temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * Add to shared generated schedules count
     * @param n - Amount to add
     */
    private void addToScheduleCount(int n){
        try {
            if(lock.tryLock(10, TimeUnit.SECONDS)){
                schedulesGeneratedCount.addAndGet(n);
                
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            //release lock
            lock.unlock();
        }
    }
}
