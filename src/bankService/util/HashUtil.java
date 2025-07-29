package bankService.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class HashUtil {   // 문자열 입력을 SHA‑256으로 해시하여 16진수(hex) 문자열로 반환 해주는 class

    // 인스턴스화 방지로 인한 private 생성자
    private HashUtil() {}

    // 입력 문자열을 SHA‑256으로 해시하여 16진수로 반환 해주는 메소드
    public static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] out = md.digest(input.getBytes(StandardCharsets.UTF_8));     // UTF-8 바이트로 변환 후 해시 계산
            return toHex(out);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }   // catch end
    }   // 문자열을 해시하여 16진수 byte[] 로 반환하는 func end


    // 바이트 배열 → 16진수 문자열로 바꿔주는 헬퍼 메소드
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }   // for end
        return sb.toString();                                               // StringBuilder 타입을 String 타입으로 만들겠다.
    }   //  바이트 배열을 16진수로 바꿔주는 헬퍼 메소드 func end


    // 공부용 주석

    /*
        MessageDigest 클래스 : 자바에서 해시 함수 값을 구할 때 쓰는 메소드
        "SHA-256"            : 암호화 해시 함수

        .getInstance( 알고리즘 ) :  입력한 해시 알고리즘을 수행하는 MessageDigest 객체를 생성.
        ** 매개변수로 받는 알고리즘은 NoSuchAlgorithmException 때문에 try / catch로 감싸줘야 한다. **
        ** NoSuchAlgorithmException : 자바에서 특정 암호화 알고리즘을 찾을 수 없을 때 발생하는 예외  **

        md.digest() : () 안의 입력 전체의 해시를 한 번에 계산.
        input.getBytes(UTF_8) 매개변수로 받은 input을 문자열 → (UTF_8)바이트 로 바꿔준다. 값 명시를 확실히 해야 어디에서든 동일한 값이 나옴
        StandardCharsets.UTF_8 : UTF_8 이라는 바이트 값으로 인코딩(컴퓨터가 이해하는 언어로 바꿈) 해주겠다는 클래스
        UTF_8 : 32바이트(256비트) 형식

     */

    /*
        StringBuilder 클래스 : 문지열을 단계적으로 연결 하는 메소드 제공하는 클래스
        %02x% -> 2글자 (00~ff)16진수로 변환하는데 0은 비어있으면 0을 채우겠다. 2는 2글자만 받겠다, x는 소문자영어로 받겠다.
        바이트를 16진수로 찍을 땐 %02x 를 많이 쓴대 그냥 쓸 때 한 번 씩 더 보자.

        String h1 = HashUtil.sha256Hex("hello"); 예시로 넣으면
        "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824" 이런 해쉬화 된 값이 뜬다.
     */


}   // class end
