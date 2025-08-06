package bankService.thread;

import bankService.service.OtpService;
import bankService.view.MainView;


/**
 * [뷰 전용 스레드]
 * - 1초 간격으로 남은 신뢰 시간을 콘솔 "한 줄 상태바"로 갱신
 * - JLine LineReader의 printAbove()로 띄움
 * - 입력은 오직 View가 담당
 */
public class OtpRemainingTimeViewThread extends Thread {
    private final OtpService otpService;
    private final MainView mainView;
    // 알림 한 번만 띄우기 위한 플래그
    private boolean shown120 = false;
    private boolean shown30  = false;
    private boolean shown5   = false;

    public OtpRemainingTimeViewThread(OtpService otpService, MainView mainView) {
        this.otpService = otpService;
        this.mainView = mainView;
        setName("OtpRemainingTimeViewThread");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                long sec = otpService.getRemainingTrustSeconds();

                // a안: 중요한 구간만 printAbove (한 번만!)
                if (sec == 120 && !shown120) {
                    mainView.getReader().printAbove("[보안 ⏳] 120초 남음");
                    shown120 = true;
                }
                if (sec == 30 && !shown30)   {
                    mainView.getReader().printAbove("[보안 ⏳] 30초 남음");
                    shown30 = true;
                }
                if (sec == 5  && !shown5)    {
                    mainView.getReader().printAbove("[보안 ⏳] 5초 남음");
                    shown5 = true;
                }

                // b안: 아래쪽 상태바(실시간)
                String msg;
                if (sec > 0)      msg = String.format("[보안 ⏳] %d초 남음", sec);
                else if (otpService.checkValidUntil()) msg = "[보안 ⏳] 계산 중...";
                else              msg = "[보안 ❌] 재인증 필요";
                mainView.setStatusBar(msg);

                // 0초가 되면 모든 플래그 리셋 (다음 인증 주기 위해)
                if (sec == 0) {
                    shown120 = shown30 = shown5 = false;
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}