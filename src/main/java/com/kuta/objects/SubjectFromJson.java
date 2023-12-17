package com.kuta.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * This class serves as an object that a json file can be read and transformed to.
 */
public class SubjectFromJson {
    @SerializedName("Name")
    public String name;
    @SerializedName("Shortcut")
    public String shortcut;
    @SerializedName("Teachers")
    public ArrayList<Teacher> teachers = new ArrayList<>();
    @SerializedName("Hours")
    public int hours;
    @SerializedName("Classrooms")
    public ArrayList<Classroom> classrooms = new ArrayList<>();
    @SerializedName("Lab")
    public int lab;

    public SubjectFromJson(){

    }

    

    public SubjectFromJson(String name, String shortcut, ArrayList<Teacher> teachers, int hours,
            ArrayList<Classroom> classrooms, int lab) {
            this.name = name;
            this.shortcut = shortcut;
            this.teachers = teachers;
            this.hours = hours;
            this.classrooms = classrooms;
            this.lab = lab;
    }



    public static ArrayList<SubjectFromJson> createFromJson(ArrayList<String> json){
        Gson gson = new Gson();
        ArrayList<SubjectFromJson> subjects = new ArrayList<>();
        for (String jsonLine : json) {
            System.out.println("Creating from line:"+jsonLine);
            SubjectFromJson subject = gson.fromJson(jsonLine, SubjectFromJson.class);
            subjects.add(subject);
        }
        
        return subjects;
    }

    public static ArrayList<String> readJsonFileToStringArray(String filepath) throws IOException,FileNotFoundException{
        File file = new File(filepath);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        ArrayList<String> jsonOutput = new ArrayList<>();
        String line;
        
        while ((line = reader.readLine()) != null) {
            if(line.equals("]")||line.equals("[")) continue;
            jsonOutput.add(line.substring(0,line.length()-1));
        }
        System.out.println("=======================================\n"+jsonOutput);
        System.out.println("=====================================");
        reader.close();
        return jsonOutput;
    }

    @Override
    public String toString() {
        return "Name=" + name + ", Shortcut=" + shortcut + ", Teachers=" + teachers + ", Hours="
                + hours + ", Classrooms=" + classrooms + ", Lab=" + lab;
    }

}
