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
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((shortcut == null) ? 0 : shortcut.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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
    Teacher other = (Teacher) obj;
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
    if (title == null) {
        if (other.title != null)
            return false;
    } else if (!title.equals(other.title))
        return false;
    return true;
}



@Override
public String toString() {
    return title+name+" | Zkratka:"+shortcut;
}



public String getName() {
    return name;
}



public String getShortcut() {
    return shortcut;
}



public String getTitle() {
    return title;
} 


}
