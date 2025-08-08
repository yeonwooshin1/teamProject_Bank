package bankService.util;


// 이메일 라이브러리 쓸 때 보내는 입력 폼 UTIL

public class EmailSendFormat {
    // 해더 생성 메소드
    public static String subjectPassWord() {
        return "[BB Bank] 비밀번호 재설정 인증 코드 안내";
    }   // func end
    public static String subjectLogin() {
        return "[BB Bank] 최초 로그인 인증 코드 안내";
    }   // func end
    public static String subjectLockLogin()  {
        return "[BB Bank] 계정 잠금해제 전용 인증 코드 안내";
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

    // HTML 버전 본문 생성 메소드 - 비밀번호 찾기시 보내는 거
    public static String bodyHtmlPassWord(String otp) {
        return new StringBuilder()
                .append("<!doctype html><html><body>")
                .append("<p>안녕하세요, BB Bank 입니다.</p>")
                .append("<p>비밀번호 재설정을 위한 인증 코드입니다:</p>")
                .append("<p style='font-size:18px; font-weight:bold;'>▶ 인증 코드: ")
                .append(otp)
                .append("</p>")
                .append("<ul>")
                .append("<li>타인 절대 공유 금지.</li>")
                .append("<li>유효시간: 발급 시점부터 2분입니다.</li>")
                .append("<li>실패 최대 횟수: 3회입니다.</li>")
                .append("</ul>")
                .append("<p>항상 저희 BB Bank를 이용해주셔서 감사합니다.</p>")
                .append("</body></html>")
                .toString();
    }   // func end

    // HTML 버전 본문 생성 메소드 - 로그인시 보내는 거
    public static String bodyHtmlLogin(String otp) {
        return new StringBuilder()
                .append("<!doctype html><html><body>")
                .append("<p>안녕하세요, BB Bank 입니다.</p>")
                .append("<p>로그인시 보안을 위한 인증 코드입니다:</p>")
                .append("<p style='font-size:18px; font-weight:bold;'>▶ 인증 코드: ")
                .append(otp)
                .append("</p>")
                .append("<ul>")
                .append("<li>타인 절대 공유 금지.</li>")
                .append("<li>유효시간: 발급 시점부터 2분입니다.</li>")
                .append("<li>실패 최대 횟수: 3회입니다.</li>")
                .append("</ul>")
                .append("<p>항상 저희 BB Bank를 이용해주셔서 감사합니다.</p>")
                .append("</body></html>")
                .toString();
    }   // func end
    // HTML 버전 본문 생성 메소드 - 5회 로그인 실패
    public static String bodyHtmlLockLogin(String otp) {
        return new StringBuilder()
                .append("<!doctype html><html><body>")
                .append("<p>안녕하세요, BB Bank 입니다.</p>")
                .append("<p>로그인 5회 실패 시 계정 잠금 해제를 위한 인증코드입니다.</p>")
                .append("<p style='font-size:18px; font-weight:bold;'>▶ 인증 코드: ")
                .append(otp)
                .append("</p>")
                .append("<ul>")
                .append("<li>타인 절대 공유 금지.</li>")
                .append("<li>유효시간: 발급 시점부터 2분입니다.</li>")
                .append("<li>실패 최대 횟수: 3회입니다.</li>")
                .append("</ul>")
                .append("<p>항상 저희 BB Bank를 이용해주셔서 감사합니다.</p>")
                .append("</body></html>")
                .toString();
    }   // func end
}   // class end
