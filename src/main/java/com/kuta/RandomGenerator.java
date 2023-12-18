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
import java.util.random.*;


import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.SubjectFromJson;
import com.kuta.objects.Teacher;


public class RandomGenerator implements Runnable{

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
    }

    private ArrayList<SubjectCore> subjects;
    private HashMap<String,ArrayList<Teacher>> subjectTeachers;
    private HashMap<String,ArrayList<Classroom>> classroomsForSubjects;
    private HashMap<String,ArrayList<Classroom>> classroomsForLabSubjects;


    private Queue<HashMap<String,ArrayList<Subject>>> completedSchedules;

    public RandomGenerator(ConcurrentLinkedQueue<HashMap<String,ArrayList<Subject>>> scheduleQueue) throws FileNotFoundException, IOException{
        this.subjects = new ArrayList<>();
        this.subjectTeachers = new HashMap<>();
        this.classroomsForLabSubjects = new HashMap<>();
        this.classroomsForSubjects = new HashMap<>();
        this.completedSchedules = scheduleQueue;

        
        String localDirectory = System.getProperty("user.dir");
        String json = SubjectFromJson.readJsonFileToString(localDirectory+"/src/main/resources/schedule.json");
        SubjectFromJson[] loadedSubjects = SubjectFromJson.createFromJson(json);

        for (SubjectFromJson subjectFromJson : loadedSubjects) {
            SubjectCore newCore = new SubjectCore(subjectFromJson.name, subjectFromJson.shortcut, (subjectFromJson.lab == 1));
            for(int i = 0; i < subjectFromJson.hours; i++){
                subjects.add(newCore);
            }

            subjectTeachers.put(newCore.name, subjectFromJson.teachers);

            if(newCore.lab){
                classroomsForLabSubjects.put(newCore.name, subjectFromJson.classrooms);
                return;
            }

            classroomsForSubjects.put(newCore.name, subjectFromJson.classrooms);

        }

            
    }
    

    @Override
    public void run() {
        HashMap<String,ArrayList<Subject>> schedule;
        HashSet<HashMap<String,ArrayList<Subject>>> generatedSchedules = new HashSet<>();
        while (true) {
             schedule = generateRandomSchedule();
             if(!generatedSchedules.contains(schedule)){
                generatedSchedules.add(schedule);
             }

             if(generatedSchedules.size() == 100_000){
                for (HashMap<String,ArrayList<Subject>> uniqueSchedule : generatedSchedules) {
                    completedSchedules.add(uniqueSchedule);
                }
             }
        }
    }

    /**
     * Generates a completely random schedule, with random subject order, random
     * teachers and random classrooms.
     * 
     * Generation restrictions :
     * - Only teachers that teach that subject will be chosen as subject teachers
     * - Only classrooms where a certain subject can be taught will be chosen
     * - Differentiates between practice and theory (lab = cviceni, !lab = teorie
     * pro potreby skolniho rozvrhu)
     * - All subject quotas must be met (If PV is 2x a week, it must be 2x a week in
     * a generated schedule)
     * 
     * @return A Hashmap with days of the week as keys, and lists of
     *         .com.kuta.ubjects.Subject as values.
     */
    public HashMap<String,ArrayList<Subject>> generateRandomSchedule(){

    ArrayList<SubjectCore> subjects = this.subjects;

    HashMap<String,ArrayList<Subject>> schedule = new HashMap<>();

    for (String day : days) {
        
        ArrayList<Subject> dailySchedule = new ArrayList<>();            

        if(subjects.size() == 0){
            schedule.put(day, dailySchedule);
            continue;
        }

        int hours = 0;
        while(true){
            hours = getRandomNumber(1,10);
            if(subjects.size() - hours > 0) break;
        }
        
        int i = 0;
        SubjectCore core;
        int randomNumber;

        while(i < hours){
            // SubjectNameAndLab subjectName = subjectNames.get(getRandomNumber(this.subjectNames.size()));
            randomNumber = getRandomNumber(subjects.size());
            core = subjects.get(randomNumber);
            subjects.remove(randomNumber);
            

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
            randomNumber = getRandomNumber(10);
            dailySchedule.add(randomNumber,null);
        }

        schedule.put(day, dailySchedule);
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
