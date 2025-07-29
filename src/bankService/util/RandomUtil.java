package bankService.util;   // util package

import java.security.SecureRandom;

public final class RandomUtil { // 난수 생성 class

    // 인스턴스화 방지로 인한 private 생성자
    private RandomUtil(){}

    // SecureRandom 클래스 : 안전한 난수 생성 클래스
    // RANDOM_DIGITS : 랜덤으로 주는 숫자 변수명
    private static final SecureRandom RANDOM_DIGITS = new SecureRandom();




    // 난수 만들어주는 메소드

    // 매개변수 digits는 원하는 자리수임
    // 예시 : 매개변수(digits)에 6 입력시 → "000000" ~ "999999" 중 하나를 생성함

    public static String createRandomNum(int digits) {
        int bound = (int) Math.pow(10, digits);
        return String.format("%0" + digits + "d", RANDOM_DIGITS.nextInt(bound));
    }   // func end






    /*
        공부용 : Math.pow( 숫자1 , 숫자2 ) => 숫자1 값에 숫자2 값만큼 제곱
        예) Math.pow( 10 , 3 );은 10의 3제곱을 double 타입으로 반환
        (int)로 강제 타입변환하여 double 값을 int로 바꾼다. 이유는 .nextInt(bound) 가 int 타입만 가능하기 때문임.

        공부용 : String.format("%0" + digits + "d" , int 값 ) %0 + 변수값 + d => (변수값)자릿수 안에 정수표현 , 빈칸은 0 채움
        예시 : digits에 값이 6이면 6자리 문자열 값 만듦 "000000" , 만약 34라면 "000034"
        RANDOM_DIGITS.nextInt(bound) 는  0 ~ (bound값) 만큼 사이에서 난수 생성 후 그 값 리턴
     */


}   // class end

