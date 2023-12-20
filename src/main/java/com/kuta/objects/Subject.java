package com.kuta.objects;

import java.util.Arrays;

public class Subject {
    private String name;
    private String shortcut;
    private Teacher teacher;
    private Classroom classroom;
    private boolean lab;
    
    public Subject(String name, String shortcut, Teacher teacher, Classroom classroom, boolean lab) {
        this.name = name;
        this.shortcut = shortcut;
        this.teacher = teacher;
        this.classroom = classroom;
        this.lab = lab;
    }

    public Subject(){

    }


    

    @Override
    public int hashCode() {
        final int prime = 71;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((shortcut == null) ? 0 : shortcut.hashCode());
        result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
        result = prime * result + ((classroom == null) ? 0 : classroom.hashCode());
        result = prime * result + (lab ? 1231 : 1237);
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
        Subject other = (Subject) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (shortcut == null) {
            if (other.shortcut != null)
                return false;
        } else if (!shortcut.equals(other.shortcut))
            return false;
        if (teacher == null) {
            if (other.teacher != null)
                return false;
        } else if (!teacher.equals(other.teacher))
            return false;
        if (classroom == null) {
            if (other.classroom != null)
                return false;
        } else if (!classroom.equals(other.classroom))
            return false;
        if (lab != other.lab)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String[] variables = {
            this.name,
            this.shortcut,
            this.teacher.toString(),
            this.classroom.toString(),
        };

        int[] spaceBuffer = {
            40,
            5,
            40,
            12
        };

        for (int i = 0; i < spaceBuffer.length; i++) {
            builder.append("| ");
            builder.append(variables[i]);
            for (int j =0 ; j < spaceBuffer[i]-variables[i].length(); j++) {
                builder.append(" ");
            }
            builder.append("|");

        }
        return builder.toString();
    }

    

    

}
