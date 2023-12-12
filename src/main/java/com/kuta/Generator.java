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


public class Generator implements Runnable{

    private final String[] days = {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday"
    };

    private ArrayList<String> subjects;
    private HashMap<String,String> subjectShortcuts;
    private HashMap<String,ArrayList<Teacher>> subjectTeachers;
    private HashMap<String,ArrayList<Classroom>> possibleClassroooms;
    private int hoursPerWeek;
    

    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    

    public HashMap<String,ArrayList<Subject>> generateRandomSchedule(){

        ArrayList<String> subjects = this.subjects;

        HashMap<String,ArrayList<Subject>> schedule = new HashMap<>();

        for (String day : days) {

            
            int hours = getRandomNumber(1,10); 
            int i = 0;
            while(i < hours){
                String subjectName = subjects.get(getRandomNumber(subjects.size()));


                // Subject subject = new Subject(
                //     subjectName,
                //     this.subjectShortcuts.get(subjectName),
                //     getRandomTeacherForSubject(subjectName),



                // );
            }



        }
        return null;
    }

    /**
     *  Method to generate random number
     * @param range 
     * @return - Random integer in given range ranging (0,range-1)
     */
    private int getRandomNumber(int range){
        return (int)Math.floor(Math.random() * (range));
    }

    /**
     * Method to return random teacher for a subject from all teachers that teach the subject
     * , recorded in this.subjectTeachers
     * 
     * @param subjectname
     * @return - Teacher object
     */
    private Teacher getRandomTeacherForSubject(String subjectname){
        return this.subjectTeachers.get(subjectname).get(getRandomNumber(subjectTeachers.get(subjectname).size()));
    }

    /**
     * Method to generate random number
     * 
     * @param min
     * @param max
     * @return - Random integer in giver range ranging (min,max)
     */
    private int getRandomNumber(int min,int max){
        return (int)Math.floor(Math.random() * (max)+min);
    }
}
