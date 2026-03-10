package com.smu8.javautil;

import java.time.LocalDate;
import java.util.Scanner;

public class L04ExceptionEx {
    public static int parseAge(String birth) throws Exception{
        //일반적인 경우: thows NumberFormatException,IllegalArgumentException
        int age=0;
        LocalDate now=LocalDate.now();
        int year=now.getYear();

        try {
            int birthInt = Integer.parseInt(birth);
            if(birthInt<1900||birthInt>year) throw new IllegalArgumentException("태어난 해는 1900~"+year);
            age=year-birthInt;
        } catch (NumberFormatException e){
            throw new Exception("태어난 해는 숫자만 입력");
        }catch (IllegalArgumentException e){
            throw new Exception(e.getMessage());
        }
        //오류가 없이 실행된다면...
        return age;
    }
    public static void main(String[] args) {
        //콘솔에서 (태어난 해) 입력 => 나이를 알려주는 어플
            Scanner scanner=new Scanner(System.in);
            System.out.println("나이를 알려주는 어플");
        while (true){
            System.out.print("당신의 태어난 년도: ");
            String birthStr=scanner.next();

            try {
                int age=parseAge(birthStr);
                System.out.println("당신의 나이는 "+age);
            } catch (Exception e) {
                System.out.println("오류"+e.getMessage());
            }

            // int year= LocalDate.now().getYear();
            /*try {
                int birth = Integer.parseInt(birthStr);
                if (birth<1900||birth>year) throw new IllegalArgumentException("태어난 해는 1900~"+year);
                System.out.println("당신의 나이는: " + (year - birth));
            }catch (NumberFormatException e){
                System.out.println("수만 입력하세요");
            }catch (IllegalArgumentException e){
                System.out.println("입력 오류: "+e.getMessage());
            }
             */
        }
    }
}
