package com.smu8.study;

public class L22Array {
    public static void main(String[] args) {
        // ex) 선생님이 학생의 성적 관리
        int s=99;
        int s2=80;
        int s3=88;
        // ...
        int s100=78;
        // 복수의 데이터를 효과적으로 관리하는 타입 => Array
        // 순서(index 0부터 시작)와 길이가 있으면 더 좋다
        int[]scores={99,80,88,78,65,100,97};
        scores[3]=90; // 초기값이 바뀔 때
        System.out.println(scores[0]);
        System.out.println(scores[3]); // 순서는 0부터 시작 (존재하지 않는 index는 참조 불가)
        scores[0]=98;
        System.out.println(scores[0]);
    }
}
