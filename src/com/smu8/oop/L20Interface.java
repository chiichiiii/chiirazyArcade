package com.smu8.oop;


//동물을 만들건데 동물은 걷고 말한다(기능을 상상할 수 없다)
interface Animalable{
    public static final int a=10;
    int b=20;
    //public static final: 어디서든 사용 가능한 정적멤버(Animalable 객체와 관련 없음=>상속과 관련 없음)
    //interface 의 모든 함수는 자동으로 public abstract 작성됨
    abstract public void walk();
    void sound();
    //void a(){};
    //Animalable(){}
    //interface는 객체가 될 수 없는 완전 추상화된 타입 => 필드, 함수, 생성자를 작성할 수 없다
}
class Tiger implements Animalable{
    int i=0; //객체 포함 => 필드
    static int j=0; //객체와는 별개로 존재(공유자원) 1번만 만들어짐
    @Override
    public void walk() {
        System.out.println("어슬렁어슬렁");
    }

    @Override
    public void sound() {
        System.out.println("어흥");
    }
}

public class L20Interface {
    public static void main(String[] args) {
        //Animalable animalable=new Animalable();
        Animalable tiger=new Tiger();
        tiger.walk();
        tiger.sound();

        //tiger.a++; final은 상수로 바뀌지 않음
        //a는 new Tiger()의 필드
        //b는 Tiger.b로 객체와 관련없는 정적 멤버
        //Animalable tiger2=new Tiger();

        Tiger t=new Tiger();
        t.i++;
        //Tiger.j++;
        t.j++; //j필드가 아니다
        System.out.println(t.i);
        Tiger t2=new Tiger();
        t2.i++;
        t2.j++;
        System.out.println(t.i);

    }
}
