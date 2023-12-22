package com.kuta;

import java.util.HashMap;
import java.util.Map;

import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;

public abstract class Config {

    /**
     * Set the value to maximum runtime of the applications. (Seconds)
     */
    public static final int MAX_RUNTIME_IN_SECONDS = 10;

    /**
     * Byte that defines an empty lesson.
     * Change to your preffered byte.
     */
    public static final byte EMPTY_LESSON = (byte)0b11111111;

    /*
     * My School schedule represented with unique bytes
     */
    public static final byte[][] INITIAL_SCHEDULE = {
        {0b00001000,0b00001001,0b00000100,0b00000110,0b00001010,EMPTY_LESSON,0b00000000,0b00000000,EMPTY_LESSON,EMPTY_LESSON},
        {0b00001010,0b00001111,0b00001011,0b00001011,0b00000110,0b00000101,EMPTY_LESSON,0b00000111,EMPTY_LESSON,EMPTY_LESSON},
        {0b00001110,0b00000100,0b00010000,0b00010000,0b00000101,0b00001010,0b00001100,EMPTY_LESSON,EMPTY_LESSON,EMPTY_LESSON,},
        {0b00001001,0b00001010,0b00001110,0b00000001,0b00000110,0b00000100,0b00000011,EMPTY_LESSON,EMPTY_LESSON,EMPTY_LESSON},
        {EMPTY_LESSON,0b00001101,0b00001101,0b00000110,0b00000111,0b00000010,0b00000010,EMPTY_LESSON,EMPTY_LESSON,EMPTY_LESSON}
    };

    /**
     * All my school subjects in an array
     */
    public static final Subject[] SUBJECTS = {
        new Subject("Programove Vybaveni - Cviceni","PV",new Teacher("Mgr.Alena Reichlova & Ing. Ondrej Mandik", "Re/Ma", " "),new Classroom("18a", 3),true),       //0
        new Subject("Programove Vybaveni - Teorie","PV",new Teacher("Alena Reichlova", "Re", "Mgr."),new Classroom("24", 4),false),                                 //1
        new Subject("Pocitacove Systemy a Site - Cviceni","PSS",new Teacher("Lukas Masopust", "Ms", "Ing."),new Classroom("8a", 2),true),                           //2
        new Subject("Pocitacove Systemy a Site - Teorie","PSS",new Teacher("Lukas Masopust", "Ms", "Ing."),new Classroom("24", 4),false),                           //3
        new Subject("Cesky Jazyk","CJ",new Teacher("Kristina Studenkova", "Su", "MUDr."),new Classroom("8a", 2),false),                                             //4
        new Subject("Aplikovana Matematika","AM",new Teacher("Filip Kallmunzer", "Kl", "Ing."),new Classroom("24", 4),false),                                       //5
        new Subject("Anglicky Jazyk","AJ",new Teacher("Tomas Juchelka", "Ju", "Ing."),new Classroom("5a", 2),false),                                                //6
        new Subject("Telocvik","TV",new Teacher("Pavel Lopocha", "Lo", "Mgr."),new Classroom("TV", 0),true),                                                            //7
        new Subject("Webova Aplikace - Cviceni","WA",new Teacher("Jan Pavlat", "Pv", "Mgr."),new Classroom("17a", 3),true),                                         //8
        new Subject("Webova Aplikace - Teorie","WA",new Teacher("Jan Pavlat", "Pv", "Mgr."),new Classroom("24", 4),false),                                          //9
        new Subject("Matematika","M",new Teacher("Eva Neugebauerova", "Ne", "Mgr."),new Classroom("24", 4),false),                                                  //10
        new Subject("Databazove Systemy - Cviceni","DS",new Teacher("Ivana Kantnerova", "Ka", "Ing."),new Classroom("18b", 3),true),                                //11
        new Subject("Databazove Systemy - Teorie","DS",new Teacher("Ivana Kantnerova", "Ka", "Ing."),new Classroom("24", 4),false),                                 //12
        new Subject("Pocitacove Informacni Systemy - Cviceni","PIS",new Teacher("Ing. Lucie Brcakova & Ing. Vit Nohejl", "Br/No", ""),new Classroom("17a", 3),true),//13
        new Subject("Pocitacove Informacni Systemy - Teorie","PIS",new Teacher("Lucie Brcakova", "Br", "Ing."),new Classroom("24", 4),false),                       //14
        new Subject("Technicky Projekt","TP",new Teacher("Vit Nohejl", "No", "Ing."),new Classroom("24", 4),false),                                                 //15
        new Subject("Cviceni ze spravy IT","CIT",new Teacher("Jakub Mazuch", "Mz", "Mgr."),new Classroom("17a", 3),true),                                           //16
    };

    /**
     * Map that maps bytes to Subject 
     * key(byte) => value(Subject)
     */
    public static final Map<Byte,Subject> SUBJECT_BYTE_MAP = new HashMap<>(){{
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
    
    
}
