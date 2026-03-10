package com.smu8.homework;
abstract class Shape{
    abstract public double area();
}

class Rectangle extends Shape{
    private double width;
    private double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width*height;
    }
}

class Circle extends Shape{
    private double radius;
    public static final double PI=3.141592653589793;
    public Circle(double radius){
        this.radius=radius;
    }
    @Override
    public double area() {
        return this.radius*this.radius*Circle.PI;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
public class H17AbstractClass {
    public static void main(String[] args) {
//        Shape circle=new Circle(7);
//        System.out.println(circle.area());
//        Circle circle2=(Circle) circle;
//        circle2.setRadius(9);
//        System.out.println(circle2.area());
        Circle circle=new Circle(10);
        Circle circle2=new Circle(12);
        Circle circle3=new Circle(14);
        Rectangle rectangle=new Rectangle(7,7);
        Rectangle rectangle2=new Rectangle(9,7);
        Rectangle rectangle3=new Rectangle(9,9);
        Rectangle rectangle4=new Rectangle(11,11);
        //유연성(타입의 다형성)-> 확장성
        Shape [] shapeArr={circle,circle2,circle3,rectangle,rectangle2,rectangle3,rectangle4};

        for (int i=0; i<shapeArr.length; i++){
            System.out.println(shapeArr[i].getClass()+":"+shapeArr[i].area());
        }


    }
}