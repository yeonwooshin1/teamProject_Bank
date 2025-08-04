package bankService.thread;

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

/**
 * [뷰 전용 스레드]
 * - 1초 간격으로 남은 신뢰 시간을 콘솔 "한 줄 상태바"로 갱신
 * - Scanner 사용/입력 금지(입력은 오직 View가 담당)
 */
public class OtpRemainingTimeViewThread extends Thread {
    private final OtpService otpService;
    private final ConsoleStatus status;

    public OtpRemainingTimeViewThread(OtpService otpService, ConsoleStatus status) {
        this.otpService = otpService;
        this.status = status;
        setName("OtpRemainingTimeViewThread");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Thread.sleep(1000);
                long sec = otpService.getRemainingTrustSeconds();
                if (sec > 0) {
                    status.show("[보안 ⏳] " + sec + "초 남음");
                } else if (otpService.checkValidUntil()) {
                    status.show("[보안 ⏳] 계산 중...");
                } else {
                    status.show("[보안 ❌] 재인증 필요");
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}