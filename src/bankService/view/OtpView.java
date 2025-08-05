package bankService.view;

import bankService.controller.OtpController;
import bankService.service.OtpService;
import org.jline.reader.LineReader;

import java.util.Scanner;


public class OtpView {  // class start
    // 싱글톤
    private OtpView(){}
    private static final OtpView INST = new OtpView();
    public static OtpView getInstance(){ return INST; }

    // 싱글톤 가져오기
    OtpController otpController = OtpController.getInstance();

    // 공유 세션
    private OtpService otpService;    // 세션 범위
    private Scanner scanner;          // 앱 전역 공유
    private Object ioLock;            // 앱 전역 공유
    private LineReader reader;


    // wire
    public void wire(OtpService otpService, Scanner scanner, Object ioLock , LineReader reader) {
        this.otpService = otpService;
        this.scanner    = scanner;
        this.ioLock     = ioLock;
        this.reader = reader;
    }   // wire end


    public void forceReauth() {
        if (otpService.checkValidUntil()) return; // 이미 신뢰 중이면 패스

        say("\n[보안] OTP를 입력해 주세요.");

        // 학습용: 발급된 OTP를 콘솔에 노출(실서비스는 메일/SMS 전송)
        String otpDev = otpController.getIssue();
        say("▶ [보안] OTP: " + otpDev + " (2분 내 입력)");

        while (true) {
            String input = ask("📨 OTP 입력 > ");
            int code = otpController.verifyOtp(input);
            if (code == 5) { say("✅ 재인증 성공! 2분간 기능 사용 가능."); return; }

            switch (code) {
                case 4 -> say("❌ OTP 불일치. 다시 시도해 주세요.");
                case 3 -> {
                    say("⛔ 실패 횟수 초과. 새 OTP 발급.");
                    otpDev = otpController.getIssue();
                    say("▶ [DEV] 새 OTP: " + otpDev + " (2분 내 입력)");
                }
                case 1, 2 -> {
                    say("⏰ 세션 없음 또는 만료. 새 OTP 발급.");
                    otpDev = otpController.getIssue();
                    say("▶ [DEV] 새 OTP: " + otpDev + " (2분 내 입력)");
                }
                default -> {
                    say("⚠️ 알 수 없는 코드. 새 OTP 발급.");
                    otpDev = otpController.getIssue();
                    say("▶ [DEV] 새 OTP: " + otpDev + " (2분 내 입력)");
                }   // default end
            }   // switch end
        }   // while end
    }   // func end

    // 입력
    private void say(String msg) {
        synchronized (ioLock) {
            reader.printAbove(msg);
        }   // syn end
    }   // func end

    // 출력
    private String ask(String prompt) {
        synchronized (ioLock) {
            return reader.readLine(prompt).trim();
        }   // syn end
    }   // func end
}   // class end