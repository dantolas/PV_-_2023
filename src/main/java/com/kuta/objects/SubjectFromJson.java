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
 * This class serves as an object that a json file can be deserialized into.
 * 
 */
public class SubjectFromJson {
    /**
     * Represents the name of the subject.
     * Example: Programove Vybaveni - Cviceni
     */
    @SerializedName("Name")
    public String name;
     /**
     * Represents the shortcut of the subject.
     * Example: PV
     */
    @SerializedName("Shortcut")
    public String shortcut;
    /**
     * Represents all teachers that teach the subject.
     * Example: Mgr. Alena Reichlová, Ing. Ondřej Mandík
     */
    @SerializedName("Teachers")
    public ArrayList<Teacher> teachers = new ArrayList<>();
    /**
     * Represents lessons per week.
     * Example: Hours = 2 -> This subject is taught twice a week.
     */
    @SerializedName("Hours")
    public int hours;
    /**
     * Represents all classes where the subject can be taught;
     * Example: 18b, floor:4; 18a, floor:4
     */
    @SerializedName("Classroom")
    public ArrayList<Classroom> classrooms = new ArrayList<>();
    /**
     * Represents if a special classroom is required
     * Example: PC Lab is required to teach PV
     */
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


    /**
     * 
     * @param json - Json String in a specific format
     * @return - Array of SubjectFromJson objects with values corresponding to given json.
     */
    public static SubjectFromJson[] createFromJson(String json){
        Gson gson = new Gson();
        SubjectFromJson[] subjects = gson.fromJson(json, SubjectFromJson[].class);
        
        return subjects;
    }

    /**
     * 
     * @param filepath - Absolute filepath to the json file
     * @return - String in json format
     * @throws IOException
     * @throws FileNotFoundException
     */
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
        return  name + " | " + shortcut + " | Teachers:" + teachers + " | Hours:"
                + hours + " | Classrooms:" + classrooms + " | Lab:" + lab;
    }

}

