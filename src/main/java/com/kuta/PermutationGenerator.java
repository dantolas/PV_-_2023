package com.kuta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;

public class PermutationGenerator implements Runnable{
    
    public final byte[][] INITIAL_SCHEDULE = {
        {0b00001000,0b00001001,0b00000100,0b00000110,0b00001010,0b1111111,0b00000000,0b00000000,0b1111111,0b1111111},
        {0b00001010,0b00001111,0b00001011,0b00001011,0b00000110,0b00000101,0b1111111,0b00000111,0b1111111,0b1111111},
        {0b00001110,0b00000100,0b00010000,0b00010000,0b00000101,0b00001010,0b00001100,0b1111111,0b1111111,0b1111111,},
        {0b00001001,0b00001010,0b00001110,0b00000001,0b00000110,0b00000100,0b00000011,0b1111111,0b1111111,0b1111111},
        {0b1111111,0b00001101,0b00001101,0b00000110,0b00000111,0b00000010,0b00000010,0b1111111,0b1111111,0b1111111}
    };
    public final Subject[] SUBJECTS = {
        new Subject("Programove Vybaveni - Cviceni","PV",new Teacher("Mgr.Alena Reichlova & Ing. Ondrej Mandik", "Re/Ma", " "),new Classroom("18a", 3),true),
        new Subject("Programove Vybaveni - Teorie","PV",new Teacher("Alena Reichlova", "Re", "Mgr."),new Classroom("24", 4),false),
        new Subject("Pocitacove Systemy a Site - Cviceni","PSS",new Teacher("Lukas Masopust", "Ms", "Ing."),new Classroom("8a", 2),true),
        new Subject("Pocitacove Systemy a Site - Teorie","PSS",new Teacher("Lukas Masopust", "Ms", "Ing."),new Classroom("24", 4),false),
        new Subject("Cesky Jazyk","CJ",new Teacher("Kristina Studenkova", "Su", "MUDr."),new Classroom("8a", 2),false),
        new Subject("Aplikovana Matematika","AM",new Teacher("Filip Kallmunzer", "Kl", "Ing."),new Classroom("24", 4),false),
        new Subject("Anglicky Jazyk","AJ",new Teacher("Tomas Juchelka", "Ju", "Ing."),new Classroom("5a", 2),false),
        new Subject("Telocvik","TV",new Teacher("Pavel Lopocha", "Lo", "Mgr."),new Classroom("TV", 0),true),
        new Subject("Webova Aplikace - Cviceni","WA",new Teacher("Jan Pavlat", "Pv", "Mgr."),new Classroom("17a", 3),true),
        new Subject("Webova Aplikace - Teorie","WA",new Teacher("Jan Pavlat", "Pv", "Mgr."),new Classroom("24", 4),false),
        new Subject("Matematika","M",new Teacher("Eva Neugebauerova", "Ne", "Mgr."),new Classroom("24", 4),false),
        new Subject("Databazove Systemy - Cviceni","DS",new Teacher("Ivana Kantnerova", "Ka", "Ing."),new Classroom("18b", 3),true),
        new Subject("Databazove Systemy - Teorie","DS",new Teacher("Ivana Kantnerova", "Ka", "Ing."),new Classroom("24", 4),false),
        new Subject("Pocitacove Informacni Systemy - Cviceni","PIS",new Teacher("Ing. Lucie Brcakova & Ing. Vit Nohejl", "Br/No", ""),new Classroom("17a", 3),true),
        new Subject("Pocitacove Informacni Systemy - Teorie","PIS",new Teacher("Lucie Brcakova", "Br", "Ing."),new Classroom("24", 4),false),
        new Subject("Technicky Projekt","TP",new Teacher("Vit Nohejl", "No", "Ing."),new Classroom("24", 4),false),
        new Subject("Cviceni ze spravy IT","CIT",new Teacher("Jakub Mazuch", "Mz", "Mgr."),new Classroom("17a", 3),true),
    };
    public final Map<Byte,Subject> SUBJECT_BYTE_MAP = new HashMap<>(){{
        put((byte)0b00000000,SUBJECTS[0] ); // PV - Cviceni
        put((byte)0b00000001,SUBJECTS[1] ); // PV - Teorie
        put((byte)0b00000010,SUBJECTS[2] ); // PSS - Cviceni
        put((byte)0b00000011,SUBJECTS[3] ); // PSS - Teorie
        put((byte)0b00000100,SUBJECTS[4] ); // CJ
        put((byte)0b00000101,SUBJECTS[5] ); // AM
        put((byte)0b00000110,SUBJECTS[6] ); // Aj
        put((byte)0b00000111,SUBJECTS[7] ); // TV
        put((byte)0b00001000,SUBJECTS[8] ); // WA - Cviceni
        put((byte)0b00001001,SUBJECTS[9] ); // WA - Teorie
        put((byte)0b00001010,SUBJECTS[10]); // M
        put((byte)0b00001011,SUBJECTS[11]); // DS - Cviceni
        put((byte)0b00001100,SUBJECTS[12]); // DS - Teorie
        put((byte)0b00001101,SUBJECTS[13]); // PIS - Cviceni
        put((byte)0b00001110,SUBJECTS[14]); // PIS - Teorie
        put((byte)0b00001111,SUBJECTS[15]); // TP
        put((byte)0b00010000,SUBJECTS[16]); // CIT
    }};

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

    public PermutationGenerator(Lock lock,AtomicInteger schedulesGeneratedCount, ConcurrentLinkedQueue<byte[][]> generatedSchedules){
        this.lock = lock;
        this.schedulesGeneratedCount = schedulesGeneratedCount;
        this.generatedSchedules = generatedSchedules;
    }
   


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


    private void generateScheduleFromAllDayPermutations(Queue<byte[][]> generatedSchedulesTempHolder){
        Queue<byte[]> dayPermutationsCopy;
        for(int i = 0; i < UNIQUE_DAY_PERMUTATIONS.size();i++){
            dayPermutationsCopy = new LinkedList<>(UNIQUE_DAY_PERMUTATIONS.get(i));

            while (true) {


                byte[][] scheduleFromDayPermutation = generateScheduleFromDayPermutation(dayPermutationsCopy,i);
                if(scheduleFromDayPermutation == null) break;


                generatedSchedulesTempHolder.offer(scheduleFromDayPermutation);
                if(generatedSchedulesTempHolder.size()% 10_000 == 0){
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
     * and adds them into uniqueDayPermutations list.
     * This should result in 5x10! different permutations for all days of the week,
     * so 18_144_000 different permutations
     * 
     * @param n - n! permutations will be generated
     * @param day - Array of bytes representing a day in the schedule 
     * @return - Number of permutations generated
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

    public void generateDayPermutationsForAllDays(){
        Queue<byte[]> generatedDayPermutationsTempHolder = new LinkedList<>();
        int generatedPermutations = 0;
        for(int i = 0;i < INITIAL_SCHEDULE.length; i++){
            generatedPermutations +=  generateDayPermutations(10, INITIAL_SCHEDULE[i],generatedDayPermutationsTempHolder);
            this.UNIQUE_DAY_PERMUTATIONS.get(i).addAll(generatedDayPermutationsTempHolder);
            generatedDayPermutationsTempHolder.clear();
        }
    }






    private byte[][] generateCombination(int currentDay){
        byte[][] newSchedule = new byte[][] {
            INITIAL_SCHEDULE[0],
            INITIAL_SCHEDULE[1],
            INITIAL_SCHEDULE[2],
            INITIAL_SCHEDULE[3],
            INITIAL_SCHEDULE[4],
        };

        for(int dayIndex = 0; dayIndex < 5;dayIndex++){
            if(dayIndex == currentDay) continue;
            byte[] dayPermutation = UNIQUE_DAY_PERMUTATIONS.get(dayIndex).poll();
            if(dayPermutation == null){
                System.out.println("Null day permutation at dayIndex:"+dayIndex);
                return null;
            }
            newSchedule[currentDay] = dayPermutation; 

        };
        return newSchedule;
    }

    private void generateCombinations(Queue<byte[][]> generatedSchedulesTempHolder){

        for(int i = 0; i < UNIQUE_DAY_PERMUTATIONS.size(); i++){
            while (true) {
                byte[][] scheduleFromInitialDay = generateCombination(i);
                if(scheduleFromInitialDay == null){ System.out.println("Break");break;}          

                generatedSchedulesTempHolder.offer(scheduleFromInitialDay);
                if(generatedSchedulesTempHolder.size() >= 10_000){
                    this.generatedSchedules.addAll(generatedSchedulesTempHolder);
                    addToScheduleCount(generatedSchedulesTempHolder.size());
                    System.out.println("Local queue size:" + generatedSchedulesTempHolder.size());
                    generatedSchedulesTempHolder.clear();
                    System.out.println("Adding to queue, schedules remaining:"+UNIQUE_DAY_PERMUTATIONS.get(i).size());
                }
                    
            }
        }
        
    }

   




    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        Queue<byte[][]> generatedSchedulesTempHolder = new LinkedList<>();
        
        try {
            generateDayPermutationsForAllDays();    
        } catch (Exception e) {
            System.out.println("PermutationGenerator failed generating day permutations. TheadName:"+Thread.currentThread().getName()); 
        }

        try {
            System.out.println("Started schedulesFromDayPermutations");
            generateScheduleFromAllDayPermutations(generatedSchedulesTempHolder); 
        } catch (Exception e) {

            System.out.println("PermutationGenerator failed generating schedules from day permutations. TheadName:"
                    + Thread.currentThread().getName());
            e.printStackTrace();
        }

        try {
            System.out.println("Started schedules from initial day");

            System.out.println(UNIQUE_DAY_PERMUTATIONS.get(0).size());
            System.out.println(UNIQUE_DAY_PERMUTATIONS.get(1).size());
            System.out.println(UNIQUE_DAY_PERMUTATIONS.get(2).size());
            System.out.println(UNIQUE_DAY_PERMUTATIONS.get(3).size());
            System.out.println(UNIQUE_DAY_PERMUTATIONS.get(4).size());

            generateCombinations(generatedSchedulesTempHolder);
        } catch (Exception e) {
            System.out.println("PermutationGenerator failed generating schedules from day permutations. TheadName:"
                    + Thread.currentThread().getName());
        }

        

        System.out.println("I ran for:"+((System.currentTimeMillis()-startTime)/1000+"s"));
        
    }

 
    private void swap(byte[] array,int i, int j){
        byte temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void printArray(byte[] array){

        for (byte b : array) {
                    System.out.println(this.SUBJECT_BYTE_MAP.get(b));
                }

        System.out.println("==================================");
    }

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
