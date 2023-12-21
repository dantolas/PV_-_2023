package com.kuta.objects;

public class Classroom {

    private String id;
    private int floor;


    public Classroom(String id, int floorNumber) {
        this.id = id;
        this.floor = floorNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + floor;
        return result;
    }




    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Classroom other = (Classroom) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (floor != other.floor)
            return false;
        return true;
    }




    @Override
    public String toString() {
        return id+" | Patro:"+floor;
    }




    public String getId() {
        return id;
    }




    public int getFloor() {
        return floor;
    }

    
    
}
