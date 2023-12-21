package com.kuta;

import com.kuta.objects.Subject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;

/**
 * This class functions as my Evaluator
 * Perfect Evaluator of Schedules (PES)
 */
public class Pes implements Runnable{


    // private Queue<HashMap<String,ArrayList<Subject>>> scheduleQueue;
    // private Queue<HashMap<String,ArrayList<Subject>>> queueCopy = new LinkedList<>();
    ConcurrentLinkedQueue<byte[][]> scheduleQueue;
    Queue<byte[][]> queueCopy = new LinkedList<>();

    // public Pes(ConcurrentLinkedQueue<HashMap<String, java.util.ArrayList<Subject>>> queue){
    //     this.scheduleQueue = queue;
    // }

    public Pes(ConcurrentLinkedQueue<byte[][]> queue){
        this.scheduleQueue = queue;
    }


    @Override
    public void run() {
        while (true) {
            if(scheduleQueue.size() >= 250_000){
                this.queueCopy = scheduleQueue;                                
                scheduleQueue.clear();
            }

            queueCopy.poll();
        }
    }

}
