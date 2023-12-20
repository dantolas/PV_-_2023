package com.kuta;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;

public class PermutationGenerator {
   
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

    public final byte[][] INITIAL_SCHEDULE = {
        {0b00001000,0b00001001,0b00000100,0b00000110,0b00001010,0b1111111,0b00000000,0b00000000,0b1111111,0b1111111},
        {0b00001010,0b00001111,0b00001011,0b00001011,0b00000110,0b00000101,0b1111111,0b00000111,0b1111111,0b1111111},
        {0b00001110,0b00000100,0b00010000,0b00010000,0b00000101,0b00001010,0b00001100,0b1111111,0b1111111,0b1111111,},
        {0b00001001,0b00001010,0b00001110,0b00000001,0b00000110,0b00000100,0b00000011,0b1111111,0b1111111,0b1111111},
        {0b1111111,0b00001101,0b00001101,0b00000110,0b00000111,0b00000010,0b00000010,0b1111111,0b1111111,0b1111111}
    };


    private ConcurrentLinkedQueue<byte[][]> schedules;

    /**
     * An implementation of Heap's algorhitm for generating all possible permutations
     * source : https://en.wikipedia.org/wiki/Heap%27s_algorithm
     * 
     * 
     * 
     * @param n - n! permutations will be generated
     * @param day - Array of bytes representing a day in the schedule 
     * @return - Queue<byte[]> containing byte[] arrays 
     */
    public void generateDayPermutations(int n,byte[] day){
        
        int numberOfPerms = 1;
        int[] c = new int[n];
        int i = 0;
        for(i = 0; i < n; i++){
            c[i] = 0;
        }

        i = 1;
        while (i < n) {
            if(c[i] < i){
                if(i%2==0) swap(day, 0, i);
                else swap(day, c[i], i);

                c[i]++;
                i=1;
                numberOfPerms ++;
                continue;
            }
            
            c[i] = 0;
            i++;

        }

        System.out.println(numberOfPerms);

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

 
}
