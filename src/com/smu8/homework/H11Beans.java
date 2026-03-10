package com.smu8.homework;

//쇼핑몰 => 고객관리용 자료형
//데이터(필드)만 관리하는 자료형 => Beans,Dto(데이터 전송),Entity(데이터베이스의 테이블과 유사한 구조)
class CustomerBean{
    private String id;
    private String name;
    private int age;
    //getter setter : 캡슐화

    public void setAge(int age) {
        if (age >= 0 && age <= 130) {
            this.age = age;
        } else {
            System.out.println("나이는 0~130까지만 입력 가능합니다.");
        }
    }
    public int getAge(){
        return this.age;
    }

    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }

    public void setId(String id){
        //id는 4자 이상
        if(id.length()>3) {
            this.id = id;
        } else {
            throw new IllegalStateException("id는 4자 이상"); //고의로 오류 생성
        }
        }
    public String getId(){
        return this.id;
    }
}
//고객관리 어플
public class H11Beans {
    public static void main(String[] args) {
        CustomerBean c=new CustomerBean();
        //c.setId("TY");
        c.setId("KINGTY");
        System.out.println(c.getId());
        //c.setAge(-100);
        c.setAge(40);
        System.out.println(c.getAge());
        c.setName("김태연");
        System.out.println(c.getName());
        //c.id="TY";
        //c.name="김태연";
        //c.age=-40;
        //고객관리 어플에서 고객의 정보(필드)를 마음대로 접근가능해서 데이터에 문자가 생김
        //=> private 하게 관리
    }
}
