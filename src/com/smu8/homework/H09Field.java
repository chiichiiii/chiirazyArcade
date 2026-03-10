package com.smu8.homework;


class Student2 {
    String name;
    int score;
}


public class H09Field {
    public static void main(String[] args) {
        Student2 student = new Student2();
        student.name="태연";
        student.score=89;
        System.out.println(student.name);
        System.out.println(student.score);
    }

}
