package com.smu8.oop;
//Car class 만드세용(name,move()=>움직인다 출력)
//Car class는 생성하자마자 이름을 초기화 함

//ElectricCar class(int bettery=100)를 만들고 Car를 상속받으세용
//car.move를 재정의 할건데 car.move()+(bettery--)를 구현


class Car{
    String name;
    public void move(){
        System.out.println(this.name+" 움직인다~");
    }
    public Car(String name){
        this.name=name;
    }
}
class ElectricCar extends Car{
    private int battery =100;
    public ElectricCar(String name) {
        super(name);
    }
    public int getBattery(){
        return this.battery;
    }

    @Override
    public void move() {
        battery--;
        super.move();
    }
}


public class L14ExtendsEx {
    public static void main(String[] args) {
        Car car=new Car("붕붕카");
        car.move();
        ElectricCar electricCar=new ElectricCar("테슬라x");
        electricCar.move();
        electricCar.move();
        electricCar.move();
        electricCar.move();
        electricCar.move();
        electricCar.move();
        System.out.println(electricCar.getBattery());
    }
}
