package com.smu8.homework;

public class H14PrintAll {
    //static 독립적으로 존재하는 데이터(정적멤버)
    public static void printAll(Object o){
        String str=o.toString();
        System.out.println("무엇이든 출력!!: "+str);
    }


    public static void main(String[] args) {
        Dog d=new Dog();
        Cat c=new Cat();
        H14PrintAll h11=new H14PrintAll();


        printAll(d);
        printAll(c);
        printAll(h11);
    }
}
