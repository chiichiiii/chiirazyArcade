package com.smu8.oop;


class Student{
    int id;
    int birth;
    String name;
    String email;

    //toString(): class 생성 시 자동으로 존재, 객체를 설명하는 용도
    //대부분 개발툴에서 toString 자동완성을 지원


    public Student(){} //기본생성자 new Student()
    public Student(int id, int birth, String name){
        this.id=id;
        this.birth=birth;
        this.name=name;
    }

    public Student(int id, int birth, String name, String email){
        this.id=id;
        this.birth=birth;
        this.name=name;
        this.email=email;
    }



    @Override //기존에 존재하는 toString 을 새로 만든 거로 대체
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", birth=" + birth +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

public class L06Constructor {
    public static void main(String[] args) {
        //학생을 생성할 때 학생은 꼭 이름과 나이와 학번이 있어야 함
        Student s=new Student();
        s.name="태연";
        s.birth=1989;
        s.id=890309;
        System.out.println(s); //Student{id=0, birth=0, name='null', email='null'}
        //객체 설명의 기본값: 타입+저장된 메모리 위치 => 필드로 바꿀 수 있다.
        System.out.println(s.toString()); //Student{id=0, birth=0, name='null', email='null'}
        Student s2=new Student(1244,2001,"영희");
        System.out.println(s2);


        //1. 모든 필드를 초기화하는 생성자를 만들어보세요~
        //2. 1에서 id,birth,name을 초기화하는 생성자를 이용하세요

        Student s3=new Student(1234,941221,"민수","ms94@ms.com");
        System.out.println(s3);

    }
}
