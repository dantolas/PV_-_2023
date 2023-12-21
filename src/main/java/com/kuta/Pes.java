package com.kuta;

import com.kuta.interfaces.Evaluator;
import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import com.kuta.objects.Teacher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.RowFilter.Entry;

import java.util.ArrayList;

/**
 * This class functions as my Evaluator
 * Perfect Evaluator of Schedules (PES)
 */
public class Pes implements Runnable,Evaluator{

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

    private byte[][] bestShedule;


    ConcurrentLinkedQueue<byte[][]> scheduleQueue;
    Queue<byte[][]> queueCopy = new LinkedList<>();


    public Pes(ConcurrentLinkedQueue<byte[][]> queue){
        this.scheduleQueue = queue;
    }

    /**
     * |Criteria 1|
     * 
     * @param schedule
     * @return
     */
    
    @Override
    public int rateEveryCell(byte[][] schedule){
        int rating = 100;

        for(int i = 0; i < schedule.length; i++){
            for(int j = 0; j < schedule[i].length; i++){
                if((schedule[i][j] & 0b11111111) == 0b11111111) continue;

                rating -= 10*j;
            }
        }
        
        return rating;
    }
    @Override
    public int rateSubjectFrequency(byte[][] schedule){

        int rating = 0;
        int count = 0;
        Map<Byte,Integer> frequency = new HashMap<>();
        for (byte b : SUBJECT_BYTE_MAP.keySet()) {
            for(int i = 0; i < schedule.length; i++){
                count = 0;
                for(int j = 0; j < schedule[i].length;j++){
                    if(schedule[i][j] == b) count++;
                }
            }
            frequency.put(b, count);
        }

        for (byte b : frequency.keySet()) {
            if(frequency.get(b) == 1){
                if(SUBJECT_BYTE_MAP.get(b).isLab()){
                    rating -= 60;
                    continue;
                }
                rating += 20;
                continue;
            }
            
            if(frequency.get(b) > 1){
                if(SUBJECT_BYTE_MAP.get(b).isLab()){
                    rating += 60;
                }
                rating -= 20;
            }
        }

        return rating;
    }


    @Override
    public int rateMovingBetweenClassroomsAndFloors(byte[][] schedule) {
        int rating = 100;
        Classroom currentClassroom;
        Classroom nextClassroom;
        for(int i = 1; i < schedule.length;i++){

            for(int j = 0; j < schedule[i].length;i++){
                currentClassroom = SUBJECT_BYTE_MAP.get(schedule[i][j]).getClassroom();
                nextClassroom = SUBJECT_BYTE_MAP.get(schedule[i][j+1]).getClassroom();
                if(!currentClassroom.getId().equals(nextClassroom.getId())) rating-=5;
                if(currentClassroom.getFloor() != nextClassroom.getFloor()) rating-=10; 
            }
                   
        }
        return rating;
    }
    

    @Override
    public int rateLunchBreak(byte[][] schedule) {
        int rating = -10;
        for (int i = 0; i < schedule.length; i++) {
            if(schedule[i][4] == 0b11111111) rating+=30;
            else if(schedule[i][4] == 0b11111111) rating+=50;
            else if(schedule[i][4] == 0b11111111) rating+=20;
            else if(schedule[i][4] == 0b11111111) rating+=10;
            
        }
        
        return rating;
    }

    @Override
    public int rateLessonAmount(byte[][] schedule) {
        int rating = 100;
        int[] lastHourPosition = new int[schedule.length];
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                if(schedule[i][j] != 0b11111111) lastHourPosition[i] =j+1;
            }
        }

        for (int i = 0; i < schedule.length; i++) {

            switch (lastHourPosition[i]) {
                
                case 5:
                    rating += 100;
                    break;
                case 6:
                    rating+= 50;
                
                case 8:
                    rating -= 50;
                
                case 9:
                    rating-=100;
                case 10:
                    rating -=200;
                default:
                    break;
            }
        }
        return rating;
    }

    @Override
    public int rateImportantSubjectPlacement(byte[][] schedule) {
        int rating = 100;
        byte[] importantSubjects = {
            0b00000000,
            0b00000001,
            0b00000100,
            0b00001010,
            0b00000010,
            0b00001000
        };
        
        for (int i = 0; i < schedule.length; i++) {
            for (byte b : importantSubjects) {
                if(schedule[i][0] == b) rating-=10;   
            }

            for(int j = 0; j < schedule[i].length;i++){
                for (byte b : importantSubjects) {
                    if(schedule[i][j] == 0b11111111 && schedule[i][j+1] == b) rating -=10; 
                }
              
            }
        }


        return rating;
    }

    @Override
    public int rateScheduleInterruptions(byte[][] schedule) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rateScheduleInterruptions'");
    }

    @Override
    public int rateMultipleLabs(byte[][] schedule) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rateMultipleLabs'");
    }

    @Override
    public int rateMyWellbeing(byte[][] schedule) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rateMyWellbeing'");
    }

    @Override
    public void rateSchedule(byte[][] schedule){
        
    }

    @Override
    public void run() {
        while (true) {
            if(scheduleQueue.size() >= 250_000){
                this.queueCopy.addAll(scheduleQueue);                                
                scheduleQueue.clear();
            }

            queueCopy.poll();
        }
    }

    /**
     * Helper method to print schedule information
     * by printing the Subject mapped for each byte of given array.
     * @param array
     */
    private void printArray(byte[] array){

        for (byte b : array) {
                    System.out.println(this.SUBJECT_BYTE_MAP.get(b));
                }

        System.out.println("==================================");
    }

}
