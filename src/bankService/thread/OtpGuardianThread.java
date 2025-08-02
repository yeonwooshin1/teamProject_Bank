package bankService.thread;

import bankService.service.OtpService;
import bankService.view.OtpView;

/** trust 만료 감시 → 만료 시 재인증 뷰 호출 */
public class OtpGuardianThread extends Thread {
    private final OtpService otp;
    private final OtpView view;
    public OtpGuardianThread(OtpService otp, OtpView view){
        this.otp=otp; this.view=view; setDaemon(true);
    }
    public void run(){
        try{
            while(!isInterrupted()){
                Thread.sleep(700);
                if(!otp.checkValidUntil()) view.forceReauth();
            }
        }catch(InterruptedException e){ interrupt(); }
    }
}
