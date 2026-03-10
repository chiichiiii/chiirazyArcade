package com.smu8.homework;


class Student {
        String name;
        int score;

        public Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

    }

public class H08Constructor {
        public static void main(String[] args) {
            Student s1 = new Student("경민", 99);
            Student s2 = new Student("철수", 80);

            System.out.println(s1.name);
            System.out.println(s1.score);

            System.out.println(s2.name);
            System.out.println(s2.score);
        }
    }


