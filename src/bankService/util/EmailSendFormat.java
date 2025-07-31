package bankService.util;


// 이메일 라이브러리 쓸 때 보내는 입력 폼 UTIL

public class EmailSendFormat {
    // 해더 생성 메소드
    public static String subject() {
        return "[은행] 비밀번호 재설정 인증 코드 안내";
    }   // func end

    // 본문 생성 메소드
    public static String body(String otp) {
        return new StringBuilder()
            .append("안녕하세요, 은행서비스 입니다.\n\n")
            .append("비밀번호 재설정을 위한 인증 코드입니다:\n\n")
            .append("   ▶ 인증 코드: ").append(otp).append("\n\n")
            .append("• 유효시간: 발급 시점부터 2분입니다.\n")
            .append("• 실패 최대 횟수 : 3회\n\n")
            .append("항상 저희 은행을 이용해주셔서 감사합니다.")
            .toString();

    }   // func end
}   // class end
