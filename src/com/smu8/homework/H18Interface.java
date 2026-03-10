package com.smu8.homework;

//결제시스템을 만들건데 초안을 작성
interface Payable{
    void pay(int amount);
}
class CardPay implements Payable{
    @Override
    public void pay(int amount) {
        System.out.println("카드 결제 완료: "+amount+"원");
    }
}
class BankPay implements Payable{
    @Override
    public void pay(int amount) {
        System.out.println("계좌 이체 완료: "+amount+"원");
    }
}
class Main{
    //void process(CardPay pay,int amount){} //CardPay,BankPay 둘 다 결제
    //void process(BankPay pay,int amount){}
    public void process(Payable pay,int amount){
        System.out.println("결제 시스템에 접속합니다.");
        System.out.println(amount+"원을 결제하시겠습니까?");
        pay.pay(amount);
    }
}
public class H18Interface {
    public static void main(String[] args) {
        Main main=new Main();
        main.process(new CardPay(),50000);
        main.process(new BankPay(),30000);
    }
}
