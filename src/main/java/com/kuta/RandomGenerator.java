package com.kuta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.random.*;


import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;


public class RandomGenerator implements Runnable{

    private final String[] days = {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday"
    };

    private class SubjectNameAndLab{
        public String name;
        public boolean lab;
    }

    private ArrayList<SubjectNameAndLab> subjectNames;
    private HashMap<String,String> subjectShortcuts;
    private HashMap<String,ArrayList<Teacher>> subjectTeachers;
    private HashMap<String,ArrayList<Classroom>> classroomsForSubjects;
    private HashMap<String,ArrayList<Classroom>> classroomsForLabSubjects;
    

    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    public RandomGenerator(){

    }
    
    /**
     * Generates a completely random schedule, with random subject order, random teachers and random classrooms.
     * 
     * Generation restrictions : 
     *  - Only teachers that teach that subject will be chosen as subject teachers
     *  - Only classrooms where a certain subject can be taught will be chosen
     *  - Differentiates between practice and theory (lab = cviceni, !lab = teorie pro potreby skolniho rozvrhu)
     *  - All subject quotas must be met (If PV is 2x a week, it must be 2x a week in a generated schedule)
     * 
     * @return A Hashmap with days of the week as keys, and lists of .com.kuta.ubjects.Subject as values.
     */
    public HashMap<String,ArrayList<Subject>> generateRandomSchedule(){

        ArrayList<SubjectNameAndLab> subjects = this.subjectNames;

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
            while(i < hours){
                SubjectNameAndLab subjectName = subjectNames.get(getRandomNumber(this.subjectNames.size()));

                Subject subject = new Subject(
                    subjectName.name,
                    this.subjectShortcuts.get(subjectName.name),
                    getRandomTeacherForSubject(subjectName.name),
                    getRandomClassroomForSubject(subjectName.name, subjectName.lab),
                    subjectName.lab
                );
                dailySchedule.add(subject);
                i++;
            }

            schedule.put(day, dailySchedule);
        }

        return schedule;
    }

    /**
     * Generate random number
     * @param range 
     * @return - Random integer in given range ranging (0,range-1)
     */
    private int getRandomNumber(int range){
        return (int)Math.floor(Math.random() * (range));
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

    private Classroom[] getRandomClassroomForSubject(String subjectName,boolean lab){
        if(lab){
            return new Classroom[] {this.classroomsForLabSubjects.get(subjectName).get(getRandomNumber(this.classroomsForLabSubjects.get(subjectName).size()))};
        }
        return new Classroom[] {this.classroomsForSubjects.get(subjectName).get(getRandomNumber(this.classroomsForSubjects.get(subjectName).size()))};
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
}
