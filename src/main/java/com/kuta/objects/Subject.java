package com.kuta.objects;

public class Subject {
    private String name;
    private String shortcut;
    private Teacher teacher;
    private Classroom[] classroom;
    private boolean lab;
    
    public Subject(String name, String shortcut, Teacher teacher, Classroom[] classroom, boolean lab) {
        this.name = name;
        this.shortcut = shortcut;
        this.teacher = teacher;
        this.classroom = classroom;
        this.lab = lab;
    }

    public Subject(){

    }



}
