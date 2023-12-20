package com.kuta;

import java.util.HashMap;
import java.util.Map;

import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;

public class PermutationGenerator {
   
    public final Subject[] subjects = {
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

    public final Map<Byte,Subject> subjectMap = new HashMap<>(){{
        put((byte)0b00000000,subjects[0] );
        put((byte)0b00000001,subjects[1] );
        put((byte)0b00000010,subjects[2] );
        put((byte)0b00000011,subjects[3] );
        put((byte)0b00000100,subjects[4] );
        put((byte)0b00000101,subjects[5] );
        put((byte)0b00000110,subjects[6] );
        put((byte)0b00000111,subjects[7] );
        put((byte)0b00001000,subjects[8] );
        put((byte)0b00001001,subjects[9] );
        put((byte)0b00001010,subjects[10] );
        put((byte)0b00001011,subjects[11] );
        put((byte)0b00001100,subjects[12] );
        put((byte)0b00001101,subjects[13] );
        put((byte)0b00001110,subjects[14] );
        put((byte)0b00001111,subjects[15] );
        put((byte)0b00010000,subjects[16] );
    }};
}
