package com.smu8.study;

public class L30StringFor {
    public static void main(String[] args) {
        String s="helloJava";
        char[]cArr={'h','e','l','l','o','J','a','v','a'};
        System.out.println(cArr[4]); // o
        //System.out.println(s[4]); //js는 가능
        System.out.println(s.charAt(4)); // == cArr[4]
        System.out.println(cArr.length); //길이
        System.out.println(s.length());

        System.out.println("\n배열 출력");
        for(int i=0;i<cArr.length;i++){
            System.out.print(cArr[i]+" ");
        }

        System.out.println("\n문자열 출력");
        for(int i=0;i<s.length();i++){
            System.out.print(s.charAt(i)+" ");
        }

        //Q.s에서 대문자가 있는지 검사하시오.
        boolean result=false;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)>='A'&&s.charAt(i)<='Z'){
                result=true;
                break; //대문자가 1개만 발견되어도 반복문은 더이상 실행될 필요가 없다.
            }
        }
        System.out.println("\n해당 문자열에 대문자가 있나? "+result);


        String[] emailArr={"a@gmail.com","b@naver.com","c","d@daum.com"};
        //Q.다음 문자열 중 Email 형식[@이 있으면 이메일]이 아닌 것이 있나요?
        result=false;
        for(int i=0;i<emailArr.length;i++){
            String email=emailArr[i];
            boolean contain=email.contains("@");
            if(!contain){
                result=true;
                break;
            }
//            if(!emailArr[i].contains("@")){
//                result=true;
//                break;
//            }
        }
        System.out.println("\nEmail 형식이 아닌 것이 있나요? "+result);
    }
}
