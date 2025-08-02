package bankService.view;

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

import java.util.Scanner;

public class OtpView {
    private OtpView(){}
    private static final OtpView INST = new OtpView();
    public static OtpView getInstance(){ return INST; }

    private OtpService otp; private Scanner scan; private ConsoleStatus bar;
    void wire(OtpService otp, Scanner sc, ConsoleStatus bar){
        this.otp=otp; this.scan=sc; this.bar=bar;
    }

    /** 신뢰 만료 시 호출(차단형) */
    public void forceReauth(){
        if(otp.checkValidUntil()) return;            // 이미 인증돼 있으면 패스
        say("\n[보안] 세션 만료! 이메일 OTP를 입력해 주세요.");
        String otpDev = otp.issue();
        say("[DEV] 이메일 OTP: "+otpDev);

        while(true){
            String in = ask("OTP > ");
            switch(otp.verify(in)){
                case 5 -> { say("✅ 재인증 성공!"); return; }
                case 4 -> say("❌ 불일치. 다시 시도");
                case 3,2,1 -> {
                    say("⏰ 재발급…");
                    otpDev = otp.reissue();
                    say("[DEV] 새 OTP: "+otpDev);
                }
            }
        }
    }

    /* -------- I/O helpers -------- */
    private String ask(String p){ bar.pause(); System.out.print(p);
        String s=scan.nextLine().trim(); bar.resume(); return s; }
    private void   say(String m){ bar.pause(); System.out.println(m); bar.resume(); }
}