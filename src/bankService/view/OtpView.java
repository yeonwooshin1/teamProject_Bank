package bankService.view;

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

import java.util.Scanner;

/**
 * OTP 재인증 "입력" 전담 View
 * - trust가 없을 때 issue()→verify() 성공 시 trust 2분 부여
 * - 모든 콘솔 I/O는 ioLock으로 직렬화, 출력 전 status.pause(), 후 status.resume()
 */
public class OtpView {
    private OtpView(){}
    private static final OtpView INST = new OtpView();
    public static OtpView getInstance(){ return INST; }

    private OtpService otpService;    // 세션 범위
    private Scanner scanner;          // 앱 전역 공유
    private ConsoleStatus status;     // 앱 전역 공유
    private Object ioLock;            // 앱 전역 공유

    /* package-private */
    void wire(OtpService otpService, Scanner scanner, ConsoleStatus status, Object ioLock) {
        this.otpService = otpService;
        this.scanner    = scanner;
        this.status     = status;
        this.ioLock     = ioLock;
    }

    /** [차단형] 재인증 처리 */
    public void forceReauth() {
        if (otpService.checkValidUntil()) return; // 이미 신뢰 중이면 패스

        say("\n[보안] 세션이 만료되었습니다. 이메일로 받은 OTP를 입력해 주세요.");

        // 학습용: 발급된 OTP를 콘솔에 노출(실서비스는 메일/SMS 전송)
        String otpDev = otpService.issue();
        say("▶ [DEV] 이메일 OTP: " + otpDev + " (2분 내 입력)");

        while (true) {
            String input = ask("📨 OTP 입력 > ");
            int code = otpService.verify(input);
            if (code == 5) { say("✅ 재인증 성공! 2분간 기능 사용 가능."); return; }

            switch (code) {
                case 4 -> say("❌ OTP 불일치. 다시 시도해 주세요.");
                case 3 -> { say("⛔ 실패 횟수 초과. 새 OTP 발급.");
                    otpDev = otpService.reissue();
                    say("▶ [DEV] 새 OTP: " + otpDev + " (2분 내 입력)"); }
                case 2, 1, default -> { say("⏰/⚠️ 세션 없음/만료. 새 OTP 발급.");
                    otpDev = otpService.reissue();
                    say("▶ [DEV] 새 OTP: " + otpDev + " (2분 내 입력)"); }
            }
        }
    }

    // ---- I/O 유틸(상태바와 충돌 방지) ----
    private void say(String msg) {
        synchronized (ioLock) { status.pause(); System.out.println(msg); status.resume(); }
    }
    private String ask(String prompt) {
        synchronized (ioLock) {
            status.pause();
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            status.resume();
            return line;
        }
    }
}