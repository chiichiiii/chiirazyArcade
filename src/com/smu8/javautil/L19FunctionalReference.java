package com.smu8.javautil;

import java.util.Optional;
import java.util.function.Consumer;

public class L19FunctionalReference {
    public static void main(String[] args) {
        //함수참조 :: 함수를 매개변수처럼 보이게 작성 (상상할 수 있는 만큼 생략)
        Consumer c=(Object s)->{System.out.println(s);};
        Consumer c2=System.out::println;

        Optional<Integer> opt=Optional.of(11);
        int num=opt.orElse(0);
        opt.ifPresent((n)->{
            System.out.println("숫자는: "+n);
        });

        opt.ifPresent(System.out::println);

        Optional<String> strOpt=Optional.of("777");
        int su=strOpt
                //.map((s)->Integer.parseInt(s))
                .map(Integer::parseInt)
                .orElse(0);
        System.out.println(su);

        strOpt=Optional.empty(); //없을 때는 빈객체 new String()
        String str= strOpt
                //.orElseGet(()->new String());
                //.orElse("");
                .orElseGet(String::new);
        System.out.println(str);
    }
}
