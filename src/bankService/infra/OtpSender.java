package bankService.infra;

/**
 * OTP 코드를 실제 전송하는 역할을 추상화한 인터페이스
 *  – 콘솔/SMS/이메일 등 구현체를 갈아끼워도 Service 코드는 변하지 않음
 */
public interface OtpSender {
    void send(String destination, String code);   // dest: 이메일·폰번호, code: 6자리 원코드(평문)
}

