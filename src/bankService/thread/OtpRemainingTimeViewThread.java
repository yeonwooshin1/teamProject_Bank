package bankService.thread;

import bankService.service.OtpService;
import bankService.view.MainView;

public class OtpRemainingTimeViewThread extends Thread {
    private final OtpService otp;
    private final MainView  view;
    private boolean s120, s30, s5;

    public OtpRemainingTimeViewThread(OtpService otp, MainView view) {
        this.otp  = otp;
        this.view = view;
        setName("OtpRemainingTimeViewThread");
        setDaemon(true);
    }

    private void notice(String m) { view.showNoticeAndClearBuffer(m); }

    @Override public void run() {
        try {
            while (!isInterrupted()) {
                long sec = otp.getRemainingTrustSeconds();

                if (sec == 120 && !s120) { notice("[보안 ⏳] 120초 남음"); s120 = true; }
                if (sec ==  30 && !s30 ) { notice("[보안 ⏳] 30초 남음");  s30  = true; }
                if (sec ==   5 && !s5  ) { notice("[보안 ⏳] 5초 남음");   s5   = true; }

                String bar = otp.checkValidUntil()
                        ? (sec > 0 ? String.format("[보안 ⏳] %d초 남음", sec) : "[보안 ✅] 인증됨")
                        : "";
                view.setStatusBar(bar);

                if (sec == 0) s120 = s30 = s5 = false;
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) { }
    }
}
