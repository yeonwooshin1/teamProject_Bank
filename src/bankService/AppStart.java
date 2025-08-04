package bankService;

import bankService.service.EmailService;
import bankService.service.OtpService;

public class AppStart {
    public static void main(String[] args) {

        // 이메일 테스트!!!! 성공!!!


        try {
            EmailService emailService = new EmailService();
            OtpService otpService = new OtpService();

            // OTP 발급
            String otp = otpService.issue();

            System.out.println("[DEV] Issued OTP = " + otp);

            // HTML 메일 전송
            String to = "tlswnsgur456@gmail.com"; // 테스트 수신자
            emailService.sendOtpHtml(to, otp);

            System.out.println("메일 전송 성공");

            // 간단 검증 테스트
            int result = otpService.verify(otp);
            System.out.println("verify result = " + result); // 기대값: 5(성공)

        } catch (IllegalStateException e) {
            // 설정/환경 문제: config.properties 누락, 키 누락, 포트 형식 오류 등
            System.err.println("[설정 오류] " + e.getMessage());
            e.printStackTrace();

        } catch (javax.mail.MessagingException e) {
            // SMTP 연결/인증/주소 등의 메일 전송 문제
            System.err.println("[메일 전송 오류] " + e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("원인: " + cause.getClass().getSimpleName() + " - " + cause.getMessage());
            }
            e.printStackTrace();

        } catch (Exception e) {
            // 기타 예외
            System.err.println("[예상치 못한 오류] " + e.getMessage());
            e.printStackTrace();
        }

    } // main e

} // class e
