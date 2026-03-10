package com.smu8.oop;
//결제시스템 카카오페이, 계좌이체, 카드...
//결제시스템 결제(결제하는 방식마다 다름),save,print
//Payment : 미완성 클래스 => 사용불가
abstract class Payment{
    abstract public void pay(int money); //{본문,바디 => 실행될 코드}

    public void save(int money){
        System.out.println(money+" 결제 저장");
    }
    public void print(int money){
        System.out.println(money+"원이 결제되었습니다. 감사합니다.");
    }
}
class NaverPay extends Payment{
    @Override
    public void pay(int money){
        System.out.println(money+"원이 네이버페이로 결제됩니다.");
    }
}
class KakaoPay extends Payment{
    @Override
    public void pay(int money) {
        System.out.println(money+"원이 카카오페이로 결제됩니다.");
    }
}

public class L19AbstractClass {
    public static void main(String[] args) {
        //Payment payment=new Payment();
        Payment naver=new NaverPay();
        naver.pay(30000);
        Payment kakao=new KakaoPay();
        kakao.pay(40000);
    }
}
