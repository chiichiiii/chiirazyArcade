package com.smu8.homework;

class Calcu{
    int a;
    int b;
    public Calcu(int a, int b){
        this.a=a;
        this.b=b;
    }
}


public class H12Ddd {
    public static void main(String[] args) {
        Calcu calcu=new Calcu(10,20);
        Calcu calcu1=new Calcu(10,20);
        System.out.println(calcu.a+calcu1.b);
        System.out.println(calcu.a==calcu1.a);
    }
}
