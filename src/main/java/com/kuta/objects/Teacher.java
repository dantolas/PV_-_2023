package com.kuta.objects;

public class Teacher {
   private String name;
   private String shortcut;
   private String title;

   
public Teacher(String name, String shortcut, String title) {
    this.name = name;
    this.shortcut = shortcut;
    this.title = title;
}


@Override
public String toString() {
    return title+name+" | Zkratka:"+shortcut;
} 


}
