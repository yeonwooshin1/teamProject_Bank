package bankService.thread;

import bankService.service.OtpService;

import java.util.concurrent.atomic.AtomicBoolean;



// 현재 안 쓰는중...!!!!!!!!!! 미사용. 나중에 쓸 일이 있겠지 열심히 스레드 공부함.

public class OtpGuardianThread extends Thread { // class start
//
//    // ✅ OTP 서비스 객체 (유효 시간 검사용)
//    private final OtpService otpService;
//    // ✅ 인증이 필요한지 여부를 다른 스레드와 공유하기 위한 AtomicBoolean
//    private final AtomicBoolean reauthNeeded; // MainView가 읽는 플래그
//    // ✅ 체크 주기 (밀리초) — 기본은 700ms
//    private final long pollMillis;
//
//    public OtpGuardianThread(OtpService otpService, AtomicBoolean reauthNeeded) {
//        this(otpService, reauthNeeded, 700);
//    }
//
//    public OtpGuardianThread(OtpService otpService, AtomicBoolean reauthNeeded, long pollMillis) {
//        this.otpService = otpService;
//        this.reauthNeeded = reauthNeeded;
//        // 주기 유효성 체크 --> 0 이하가 들어오면 기본값 700으로 설정
//        this.pollMillis = (pollMillis <= 0) ? 700 : pollMillis;
//        setName("OtpGuardianThread"); // 스레드 이름 지정 (디버깅에 도움)
//        setDaemon(true); // 메인 프로그램 종료 시 이 스레드도 자동 종료되도록 설정
//    }   // 생성자 end
//
//    @Override
//    public void run() { // run 스레드 실행 루프
//        try {
//            // 스레드가 중단되지 않는 한 무한 루프
//            while (!isInterrupted()) {
//                Thread.sleep(pollMillis); // 지정된 주기만큼 대기
//                if (!otpService.checkValidUntil()) {    // OTP가 더 이상 유효하지 않다면...
//                    // reauthNeeded가 false일 경우에만 true로 바꿈 (true면 그대로 유지)
//                    reauthNeeded.compareAndSet(false, true); // 이미 true면 유지
//                }
//            }
//        } catch (InterruptedException e) {
//            interrupt(); // 정상 종료
//        }   // catch end
//    }   // run end
}   // class end