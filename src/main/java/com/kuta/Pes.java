package com.kuta;

import com.kuta.interfaces.Evaluator;
import com.kuta.objects.Classroom;
import com.kuta.objects.Subject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * This class functions as my Evaluator
 * Perfect Evaluator of Schedules (PES)
 */
public class Pes implements Runnable,Evaluator{

    private final Subject[] SUBJECTS = Config.SUBJECTS;
    private final Map<Byte,Subject> SUBJECT_BYTE_MAP = Config.SUBJECT_BYTE_MAP;
    private final byte EMPTY_LESSON = Config.EMPTY_LESSON;

    private ReentrantLock lock;
    private ConcurrentLinkedQueue<byte[][]> scheduleQueue;
    private final int TAKE_FROM_QUEUE_THRESHOLD = 50_000;
    private final AtomicBoolean THREAD_STOP_ORDER;
    private AtomicBoolean KEEP_GENERATING;

    private Queue<byte[][]> queueCopy = new LinkedList<>();


    private byte[][] bestScheduleShared;
    private int[] bestScheduleScoreShared;

    private byte[][] bestSheduleTemp;
    private int[] bestScheduleScore = new int[11];

    private final int INITIAL_SCHEDULE_SCORE;

    private AtomicInteger beterThanInitialShared;

    private int betterThanInitialLocal;

    private AtomicInteger schedulesProcessedShared;
    private int schedulesProcessedLocal;

    private final int PROCESSED_THRESHOLD = 10_000;



    public Pes(
        ConcurrentLinkedQueue<byte[][]> scheduleQueue, ReentrantLock lock,
        AtomicBoolean THREAD_STOP_ORDER,AtomicBoolean KEEP_GENERATING,AtomicInteger betterThanInitialShared,
        AtomicInteger schedulesProcessedShared,byte[][] bestScheduleShared,int[] bestScheduleScoreShared
        ){
        this.bestSheduleTemp = Config.INITIAL_SCHEDULE;
        this.INITIAL_SCHEDULE_SCORE = rateSchedule(bestSheduleTemp)[10];
        bestScheduleScore[10] = INITIAL_SCHEDULE_SCORE;
        this.schedulesProcessedLocal = 0;
        this.betterThanInitialLocal = 0;
        
        
        this.scheduleQueue = scheduleQueue;
        this.lock = lock;
        this.THREAD_STOP_ORDER = THREAD_STOP_ORDER;
        this.KEEP_GENERATING = KEEP_GENERATING;
        this.beterThanInitialShared = betterThanInitialShared;
        this.schedulesProcessedShared = schedulesProcessedShared;
        this.bestScheduleShared = bestScheduleShared;
        this.bestScheduleScoreShared = bestScheduleScoreShared;
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
            for(int j = 0; j < schedule[i].length; j++){
                if((schedule[i][j] & EMPTY_LESSON) == EMPTY_LESSON) continue;

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

        for(int i = 0; i < schedule.length;i++){
            for (byte b : SUBJECT_BYTE_MAP.keySet()) {
                count = 0;
                for(int j = 0; j < schedule[i].length; j++){
                    if(schedule[i][j] == b) count++;
                }
                frequency.put(b, count);
            }

            for (byte b : frequency.keySet()) {
            
                if(frequency.get(b) > 1){
                    if(SUBJECT_BYTE_MAP.get(b).isLab()){
                        rating += 20;
                        continue;
                    };
                    rating -= 20;
                }

                rating += 20;

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

            for(int j = 0; j < schedule[i].length;j++){

                if(j == schedule[i].length-1) continue;

                if(schedule[i][j] ==  EMPTY_LESSON || schedule[i][j+1] == EMPTY_LESSON) continue;

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
        int rating = -20;
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
                if(schedule[i][j] != EMPTY_LESSON) lastHourPosition[i] =j+1;
            }
        }

        for (int i = 0; i < schedule.length; i++) {

            switch (lastHourPosition[i]) {
                
                case 5:
                    rating += 10000;
                    break;
                case 6:
                    rating+= 50;
                
                case 8:
                    rating -= 100;
                
                case 9:
                    rating-=-20000;
                case 10:
                    rating -=300;
                default:
                    break;
            }
        }
        return rating;
    }

    @Override
    public int rateLabsTogether(byte[][] schedule) {
        int rating = 100;
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {

                if(schedule[i][j] == EMPTY_LESSON) continue;

                Subject currentSubject = SUBJECT_BYTE_MAP.get(schedule[i][j]);
                
                             
                if(!currentSubject.isLab()) continue;


                if(j == schedule[i].length-1) {
                    Subject previousSubject = SUBJECT_BYTE_MAP.get(schedule[i][j-1]);
                    if(previousSubject == null){
                        rating -=20;
                        continue;
                    }
                    if(!previousSubject.isLab() || !previousSubject.getShortcut().equals(currentSubject.getShortcut())) {
                        rating -=20;
                    }
                    continue;
                };

                if(j == 0){
                    Subject next = SUBJECT_BYTE_MAP.get(schedule[i][j+1]);
                    if(next == null){
                        rating -=20;
                        continue;
                    }
                    if(!next.isLab() || !next.getShortcut().equals(currentSubject.getShortcut())) {
                        rating -=20;
                    }
                    continue;
                }

                Subject nextSubject = SUBJECT_BYTE_MAP.get(schedule[i][j+1]);
                Subject previousSubject = SUBJECT_BYTE_MAP.get(schedule[i][j-1]);
                boolean matchesPreviousSubject = false;
                boolean matchesNextSubject = false;

                if(nextSubject == null && previousSubject == null){
                    rating -= 20;
                    continue;
                }

                if(nextSubject == null){
                    matchesNextSubject = false;
                }
                else if( previousSubject == null){
                    matchesPreviousSubject = false;
                }
                else{

                matchesNextSubject = nextSubject.isLab() && nextSubject.getShortcut().equals(currentSubject.getShortcut());
                matchesPreviousSubject = previousSubject.isLab() && previousSubject.getShortcut().equals(currentSubject.getShortcut());
                }



                if(!matchesNextSubject && !matchesPreviousSubject) rating-=20;
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

            for(int j = 0; j < schedule[i].length;j++){
                for (byte b : importantSubjects) {
                    if(schedule[i].length -1 == j) continue;

                    if(schedule[i][j] == EMPTY_LESSON && schedule[i][j+1] == b && j>=4) rating -=10; 
                }
              
            }
        }


        return rating;
    }

    @Override
    public int rateScheduleInterruptions(byte[][] schedule) {
        int rating = 100;
        int interruptionCount =0;
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length -1; j++) {
                if(schedule[i].length -1 == j) continue;
                if(schedule[i][j] != EMPTY_LESSON && schedule[i][j+1] == EMPTY_LESSON) interruptionCount++;

            }
        }

        rating -= interruptionCount * (15 * interruptionCount);
        return rating;
    }

    @Override
    public int rateMultipleLabs(byte[][] schedule) {
        int rating = 125;
        HashSet<Byte> set = new HashSet<>();
        
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                if(schedule[i][j] == EMPTY_LESSON) continue;

                if(SUBJECT_BYTE_MAP.get(schedule[i][j]).isLab() && !SUBJECT_BYTE_MAP.get(schedule[i][j]).getShortcut().equals("TV")) set.add(schedule[i][j]);
            }
            rating -= 25 * set.size();
            set.clear();
        }
        return rating;
    }

    @Override
    public int rateMyWellbeing(byte[][] schedule) {

        int rating = 100;

        String[] likedTeachers = {
            SUBJECTS[0].getTeacher().getShortcut(),
            SUBJECTS[1].getTeacher().getShortcut(),
            SUBJECTS[10].getTeacher().getShortcut(),
            

            
        };

        String[] dislikedTeachers = {
            SUBJECTS[15].getTeacher().getShortcut(),
            SUBJECTS[7].getTeacher().getShortcut(),
            SUBJECTS[5].getTeacher().getShortcut()
            
        };

        int daysIMetTheTeacher = 0;
        for (String teacher : dislikedTeachers) {
            daysIMetTheTeacher = 0;
            for(int i = 0; i < schedule.length; i++){
                for(int j = 0; j < schedule[i].length; j++){
                    if(schedule[i][j] == EMPTY_LESSON) continue;                    

                    String currentTeacher = SUBJECT_BYTE_MAP.get(schedule[i][j]).getTeacher().getShortcut();
                    if(currentTeacher.equals(teacher)){
                        daysIMetTheTeacher++;
                        break;
                    }
                }
        }
        rating -= daysIMetTheTeacher * 20;
        }
        
        for (String teacher : likedTeachers) {
            daysIMetTheTeacher = 0;
            for(int i = 0; i < schedule.length; i++){
                for(int j = 0; j < schedule[i].length; j++){
                    if(schedule[i][j] == EMPTY_LESSON) continue;                    
                    
                    String currentTeacher = SUBJECT_BYTE_MAP.get(schedule[i][j]).getTeacher().getShortcut();
                    if(currentTeacher.equals(teacher)){
                        daysIMetTheTeacher++;
                        break;
                    }
                }
        }
        rating += daysIMetTheTeacher * 20;
        }
        return rating;
    }

    @Override
    public int[] rateSchedule(byte[][] schedule){
        
        int[] scheduleScores = new int[11];

        scheduleScores[0] = rateEveryCell(schedule);
        scheduleScores[1] = rateSubjectFrequency(schedule);
        scheduleScores[2] = rateMovingBetweenClassroomsAndFloors(schedule);
        scheduleScores[3] = rateLunchBreak(schedule);
        scheduleScores[4] = rateLessonAmount(schedule);
        scheduleScores[5] = rateLabsTogether(schedule);
        scheduleScores[6] = rateImportantSubjectPlacement(schedule);
        scheduleScores[7] = rateScheduleInterruptions(schedule);
        scheduleScores[8] = rateMultipleLabs(schedule);
        scheduleScores[9] = rateMyWellbeing(schedule);

        int finalScore = 0;
        for (int score : scheduleScores) {
            finalScore+= score;
        }

        scheduleScores[10] = finalScore;

        return scheduleScores;
        
    }
    @Override
    public void handleSchedule(byte[][] schedule){


        int[] scores = rateSchedule(schedule);
        
        if (scores[10] > this.INITIAL_SCHEDULE_SCORE){
            betterThanInitialLocal++;
        }
        if(scores[10] > bestScheduleScore[10]){
            bestSheduleTemp = schedule;
            bestScheduleScore = scores;
        }

        this.schedulesProcessedLocal ++;
    }

    @Override
    public void run() {

        while (!THREAD_STOP_ORDER.get()) {

            if(scheduleQueue.size() >= TAKE_FROM_QUEUE_THRESHOLD){
                KEEP_GENERATING.set(false);
                for (byte[][] schedule : scheduleQueue) {
                    byte[][] newSchedule = clone2DArray(schedule);
                     
                    this.queueCopy.add(newSchedule);
                }
                scheduleQueue.clear();
            }
            if(queueCopy.peek() == null){
                KEEP_GENERATING.set(true);
                continue;
            } 
            handleSchedule(queueCopy.poll());

            if(this.schedulesProcessedLocal % PROCESSED_THRESHOLD == 0){
                addToProcessedCount(schedulesProcessedLocal);
                schedulesProcessedLocal = 0;
                addToBetterThanInitialCount(betterThanInitialLocal);
                betterThanInitialLocal = 0;
            }
        }

        addToProcessedCount(schedulesProcessedLocal);
        addToBetterThanInitialCount(betterThanInitialLocal);

        updateSharedbestSchedule();

    }

    private void updateSharedbestSchedule(){
        try {
            if(lock.tryLock(10, TimeUnit.SECONDS)){

                if(bestScheduleScoreShared[10] > bestScheduleScore[10]){
                    bestScheduleShared = bestSheduleTemp;
                    bestScheduleScoreShared = bestScheduleScore;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            //release lock
            if(lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    private void addToProcessedCount(int amount){
        schedulesProcessedShared.addAndGet(amount);
            }

    private void addToBetterThanInitialCount(int amount){
        beterThanInitialShared.addAndGet(amount);
    }

    /**Creates an independent copy(clone) of the boolean array.
     * @param array The array to be cloned.
     * @return An independent 'deep' structure clone of the array.
     */
    public byte[][] clone2DArray(byte[][] array) {
        int rows=array.length ;
        //int rowIs=array[0].length ;

        //clone the 'shallow' structure of array
        byte[][] newArray =(byte[][]) array.clone();
        //clone the 'deep' structure of array
        for(int row=0;row<rows;row++){
            newArray[row]=(byte[]) array[row].clone();
        }

        return newArray;
    }

}
