package com.smu8.oop;

//쇼핑몰 => 고객관리용 자료형
//데이터(필드)만 관리하는 자료형 => Beans,Dto(데이터전송),Entity(데이터베이스의 테이블과 유사한 구조)
class CustomerBean{
    private String id;
    private String name;
    private int age;
    //getter setter 캡슐화
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    //age 의 get set을 정의하고 set에서 유효성검사를 진행하세요(0~130), 만약 조건을 만족하지 않으면 set하지 않기
    public void setAge(int age){
        if (age>=0&&age<=130){
            this.age=age;
        } else {
            System.out.println("나이는 0~130까지만 입력 가능합니다.");
        }
    }
    public int getAge(){
        return this.age;
    }

    public void setId(String id){
        //id는 4자이상
        if(id.length()>3){
            this.id=id;
        }else {
            throw new IllegalArgumentException("id는 4자 이상"); //고의로 오류생성
        }
    }
    public String getId(){
        return this.id;
    }
}
//고객관리 어플
public class L08Beans {
    public static void main(String[] args) {
        CustomerBean c=new CustomerBean();
        //c.setId("코딩");
        c.setId("경민코딩");
        System.out.println(c.getId());
        //c.setAge(-100);
        c.setAge(40);
        System.out.println(c.getAge());
        c.setName("최경만");
        System.out.println(c.getName());
        //c.id="경민코딩";
        //c.name="최경민";
        //c.age=-40;
        //고객관리 어플에서 고객의 정보(필드)를 마음대로 접근가능해서 데이터에 문제가 생김
        //=>private 하게 관리
    }
}

