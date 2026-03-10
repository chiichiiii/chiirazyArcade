package com.smu8.oop;

import java.util.Objects;

class Cal{
    int a;
    int b;
    public Cal(int a,int b){
        this.a=a;
        this.b=b;
    }

    @Override
    public String toString() {
        return "Cal{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {//변수는 부모의 타입을 참조할 수 있다 (타입의 다형성)
        //받아온 데이터가 Cal가 아니면 false
        //받아온 데이터가 Cal가 맞으면 변수 cal가 o 데이터를 참조할 것임

        if (!(o instanceof Cal cal)) return false;
        return this.a == cal.a && this.b == cal.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}

public class L16EqualsOverride {
    public static void main(String[] args) {
        Cal c=new Cal(10,20);
        Cal c2=new Cal(10,20);
        System.out.println(c);
        System.out.println(c2);
        String str="안녕";
        System.out.println(c==c2); //false
        System.out.println(c.equals(c2));
    }
}
