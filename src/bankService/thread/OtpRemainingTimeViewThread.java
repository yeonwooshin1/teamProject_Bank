package bankService.thread;

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

/** 1초마다 상태바를 갱신 */
public class OtpRemainingTimeViewThread extends Thread {
    private final OtpService otp;
    private final ConsoleStatus bar;
    public OtpRemainingTimeViewThread(OtpService otp, ConsoleStatus bar){
        this.otp=otp; this.bar=bar; setDaemon(true);
    }
    public void run(){
        try{
            while(!isInterrupted()){
                Thread.sleep(1000);
                long s = otp.getRemainingTrustSeconds();
                bar.show( s>0 ? "[보안 ⏳] "+s+"초 남음"
                        : (otp.checkValidUntil()? "[보안 ⏳] 계산 중..."
                        : "[보안 ❌] 재인증 필요"));
            }
        }catch(InterruptedException e){ interrupt(); }
    }
}
