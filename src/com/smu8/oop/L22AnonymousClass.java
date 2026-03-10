package com.smu8.oop;

interface Payable{
    void pay();

}
class CardPay implements Payable{
    @Override
    public void pay() {
        System.out.println("카드결제");
    }
}
public class L22AnonymousClass {
/* new Payable(){ 이하 코드를 컴파일러가 컴파일할 때 자동 완성하는 익명 클래스
    class Anonymous1 implements Payable{
        @Override
        public void pay() {
            System.out.println("계좌이체를 진행합니다.");
        }
    }*/
    public static void main(String[] args) {
        //Payable payable=new Payable();
        Payable cardPay=new CardPay();
        cardPay.pay();
        //class 만들고 interface 구현하고 객체를 생성하는 것이 귀찮음
        //인스턴스 생성 : new 생성자();
        //=> 익명class : new 생성자(){};
        Payable bankPay=new Payable(){
            @Override
            public void pay() {
                System.out.println("계좌이체를 진행합니다.");
            }
        };
        bankPay.pay();
    }
}
