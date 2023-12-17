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



    public static SubjectFromJson[] createFromJson(String json){
        Gson gson = new Gson();
        SubjectFromJson[] subjects = gson.fromJson(json, SubjectFromJson[].class);
        
        return subjects;
    }
//STOP NOW
    public static String readJsonFileToString(String filepath) throws IOException,FileNotFoundException{
        File file = new File(filepath);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String jsonOutput = "";
        String line;    
        
        while ((line = reader.readLine()) != null) {
            jsonOutput+= line;    
        }
        reader.close();
        return jsonOutput;
    }

    @Override
    public String toString() {
        return "Name=" + name + ", Shortcut=" + shortcut + ", Teachers=" + teachers + ", Hours="
                + hours + ", Classrooms=" + classrooms + ", Lab=" + lab;
    }

}

