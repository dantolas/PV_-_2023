package com.kuta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.util.random.*;

import javax.management.Query;

import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.SubjectFromJson;
import com.kuta.objects.Teacher;

/**
 * This class generates completely random Schedules.
 * It firsts loads all the information necessary for schedule generation from a JSON file,
 * and starts generating random schedules up to a certain limit.
 * 
 * This method of generating is  inneficient, slow and consumes a lot of memory.
 * It isn't supposed to be optimal, it's just meant to be random.
 * There is an upper limit to the amount that can be generated this way, that depends
 * on the power of system hardware.
 * 
 * The chance for the same schedule to be generated using this method is extremely unlikely,
 * but with enough schedules generated there might be a small amount of duplication.
 * That is technically cheating according to the assignment,
 * however it would be more time expensive to check uniqueness of a schedule than to rate it. 
 */
public class RandomGenerator implements Runnable{

    private Lock lock;
    private AtomicInteger schedulesGeneratedCount;
    private final int GENERATION_LIMIT = 10_000_000;
    private final int ADD_TO_QUEUE_THRESHOLD = 50_000;
    

    private final String[] days = {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday"
    };

    private class SubjectCore{
        public String name;
        public String shortcut;
        public boolean lab;

        public SubjectCore(String name,String shortcut,boolean lab){
            this.name = name;
            this.lab = lab;
            this.shortcut = shortcut;
        }

        @Override
        public String toString() {
            return "SubjectCore [name=" + name + ", shortcut=" + shortcut + ", lab=" + lab + "]";
        }

        
    }

    private ArrayList<SubjectCore> subjects;
    private HashMap<String,ArrayList<Teacher>> subjectTeachers;
    private HashMap<String,ArrayList<Classroom>> classroomsForSubjects;
    private HashMap<String,ArrayList<Classroom>> classroomsForLabSubjects;


    private Queue<HashMap<String,ArrayList<Subject>>> scheduleQueue;

    public RandomGenerator(ConcurrentLinkedQueue<HashMap<String,ArrayList<Subject>>> scheduleQueue,String relativeFilePath, Lock lock, AtomicInteger schedulesGeneratedCount) throws FileNotFoundException, IOException{
        this.subjects = new ArrayList<>();
        this.subjectTeachers = new HashMap<>();
        this.classroomsForLabSubjects = new HashMap<>();
        this.classroomsForSubjects = new HashMap<>();
        this.scheduleQueue = scheduleQueue;

        this.lock = lock;
        this.schedulesGeneratedCount = schedulesGeneratedCount;

        
        String localDirectory = System.getProperty("user.dir");
        String json = SubjectFromJson.readJsonFileToString(localDirectory+relativeFilePath);
        SubjectFromJson[] loadedSubjects = SubjectFromJson.createFromJson(json);

        for (SubjectFromJson subjectFromJson : loadedSubjects) {
            SubjectCore newCore = new SubjectCore(subjectFromJson.name, subjectFromJson.shortcut, (subjectFromJson.lab == 1));
            for(int i = 0; i < subjectFromJson.hours; i++){
                subjects.add(newCore);
            }

            subjectTeachers.put(newCore.name, subjectFromJson.teachers);

            if(newCore.lab){
                classroomsForLabSubjects.put(newCore.name, subjectFromJson.classrooms);
                continue;
            }

            classroomsForSubjects.put(newCore.name, subjectFromJson.classrooms);

        }

            
    }
    

    @Override
    public void run() {
        HashMap<String,ArrayList<Subject>> schedule;
        Queue<HashMap<String,ArrayList<Subject>>> generatedSchedules = new LinkedList<>();
        int repetitions = GENERATION_LIMIT;

        int i = 0;
        int hours = 0;
        int randomNumber = 0;
        System.out.println("Generator started.");
        try {
            while (repetitions > 0) {
                schedule = generateRandomSchedule(hours,i,randomNumber);
                generatedSchedules.add(schedule);

                if(generatedSchedules.size() % ADD_TO_QUEUE_THRESHOLD == 0){
                    scheduleQueue.addAll(generatedSchedules);
                    generatedSchedules.clear();
                    try {
                        if(lock.tryLock(10, TimeUnit.SECONDS)){
                            schedulesGeneratedCount.addAndGet(ADD_TO_QUEUE_THRESHOLD);
                            
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally{
                        //release lock
                        lock.unlock();
                    }

                }

                if(i % 1_000_000 == 0) System.gc();
                i--;  
        }
        } catch (OutOfMemoryError e) {
            System.out.println("Generator has run out of heap memory space, and will now shut down. Thread:"+Thread.currentThread().getName());
        }
    }

    /**
     * Generates a completely random schedule, with random subject order, random
     * teachers and random classrooms.
     * 
     * Generation restrictions :
     * - Only teachers that teach that subject will be chosen as subject teachers
     * - Only classrooms where a certain subject can be taught will be chosen
     * - Differentiates between practice and theory
     * - All subject quotas must be met (If PV is 2x a week, it must be 2x a week in
     * a generated schedule)
     * 
     * @return A Hashmap with days of the week as keys, and lists of
     *         .com.kuta.ubjects.Subject as values.
     */
    public HashMap<String,ArrayList<Subject>> generateRandomSchedule(int hours,int i,int randomNumber){

    ArrayList<SubjectCore> subjectsCopy = this.subjects;

    HashMap<String,ArrayList<Subject>> schedule = new HashMap<>();

    for (String day : days) {
        
        ArrayList<Subject> dailySchedule = new ArrayList<>();            

        if(subjectsCopy.size() == 0){
            schedule.put(day, dailySchedule);
            continue;
        }

        hours = 0;
        while(true){
            hours = getRandomNumber(1,10);
            if((subjectsCopy.size() - hours) > 0) break;
        }
        
        i = 0;
        SubjectCore core;

        while(i < hours){
            randomNumber = getRandomNumber(subjectsCopy.size());
            core = subjectsCopy.get(randomNumber);
            subjectsCopy.remove(randomNumber);
            

            Subject subject = new Subject(
                core.name,
                core.shortcut,
                getRandomTeacherForSubject(core.name),
                getRandomClassroomForSubject(core.name, core.lab),
                core.lab
            );
            dailySchedule.add(subject);
            i++;
        }

        while (dailySchedule.size() < 10) {
            randomNumber = getRandomNumber(dailySchedule.size());
            dailySchedule.add(randomNumber,null);
        }
        
        schedule.put(day, dailySchedule);
    }

    while(subjectsCopy.size() > 0){
        ArrayList<Subject> randomSchedule = schedule.get(days[getRandomNumber(days.length)]);

        for (i = 0; i < randomSchedule.size(); i++) {
            if(randomSchedule.get(i) == null){
            SubjectCore core = subjectsCopy.get(0);
            Subject subject = new Subject(core.name,core.shortcut,getRandomTeacherForSubject(core.name),getRandomClassroomForSubject(core.name, core.lab),core.lab);
            subjectsCopy.remove(0);
            randomSchedule.set(i,subject);
            break;
            }
        }
    }
    return schedule;
    }

    /**
     * Return random teacher for a subject from all teachers that teach the subject
     * , recorded in this.subjectTeachers
     * 
     * @param subjectname
     * @return - Teacher object
     */
    private Teacher getRandomTeacherForSubject(String subjectname){
        return this.subjectTeachers.get(subjectname).get(getRandomNumber(subjectTeachers.get(subjectname).size()));
    }

    private Classroom getRandomClassroomForSubject(String subjectName,boolean lab){
        ArrayList<Classroom> possibleClassrooms;
        if(lab){
            possibleClassrooms = this.classroomsForLabSubjects.get(subjectName);
            return possibleClassrooms.get(getRandomNumber(possibleClassrooms.size()));
        }
        possibleClassrooms = this.classroomsForSubjects.get(subjectName);
        return possibleClassrooms.get(getRandomNumber(possibleClassrooms.size()));
    }

    /**
     * Generate random number
     * 
     * @param min
     * @param max
     * @return - Random integer in giver range ranging (min,max)
     */
    private int getRandomNumber(int min,int max){
        return (int)Math.floor(Math.random() * (max)+min);
    }

    /**
     * Generate random number
     * @param range 
     * @return - Random integer in given range ranging (0,range-1)
     */
    private int getRandomNumber(int range){
        return (int)Math.floor(Math.random() * (range));
    }
}
