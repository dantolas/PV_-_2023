package com.kuta.objects;

public class Classroom {

    private String id;
    private int floor;


    public Classroom(String id, int floorNumber) {
        this.id = id;
        this.floor = floorNumber;
    }


    @Override
    public String toString() {
        return id+" | Patro:"+floor;
    }

    
    
}
