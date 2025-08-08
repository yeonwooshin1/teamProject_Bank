package bankService.thread;

import bankService.service.OtpService;
import bankService.view.MainView;
import org.jline.reader.LineReader;


/**
 * [뷰 전용 스레드]
 * - 1초 간격으로 남은 신뢰 시간을 콘솔 "한 줄 상태바"로 갱신
 * - JLine LineReader의 printAbove()로 띄움
 * - 입력은 오직 View가 담당
 */
public class OtpRemainingTimeViewThread extends Thread {
    private final OtpService otp;
    private final MainView view;
    private boolean s180, s30, s5;

    public OtpRemainingTimeViewThread(OtpService otp, MainView view) {
        this.otp = otp;
        this.view = view;
        setName("OtpRemainingTimeViewThread");
        setDaemon(true);
    }

    private void notice(String m) {
        view.showNoticeAndClearBuffer(m);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                long sec = otp.getRemainingTrustSeconds();

                /* 입력 중이 아닐 때만 한-번-알림 */
                if (sec == 180 && !s180 ) {
                    notice("[보안 ⏳] 180초 남음");
                    s180 = true;
                }
                if (sec == 30 && !s30) {
                    notice("[보안 ⏳] 30초 남음");
                    s30 = true;
                }
                if (sec == 5 && !s5) {
                    notice("[보안 ⏳] 5초 남음");
                    s5 = true;
                }

                /* 상태바는 매 틱 업데이트 (입력 중 여부 무관) */
                String bar = otp.checkValidUntil()
                        ? (sec > 0 ? String.format("[보안 ⏳] %d초 남음", sec)
                        : "[보안 ✅] 인증됨")
                        : "";
                view.setStatusBar(bar);

                if (sec == 0) s180 = s30 = s5 = false;

                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) {
        }
    }
}