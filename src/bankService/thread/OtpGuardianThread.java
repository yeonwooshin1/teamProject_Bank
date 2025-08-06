package bankService.thread;

import bankService.service.OtpService;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * [감시 스레드]
 * - 주기적으로 OtpService.checkValidUntil() 확인
 * - 신뢰가 끊어지면 reauthNeeded(true)로 "신호만" 올림
 * - 입력/뷰 호출 책임은 MainView가 가짐(책임 분리)
 */

public class OtpGuardianThread extends Thread { // class start

    private final OtpService otpService;
    private final AtomicBoolean reauthNeeded; // MainView가 읽는 플래그
    private final long pollMillis;

    public OtpGuardianThread(OtpService otpService, AtomicBoolean reauthNeeded) {
        this(otpService, reauthNeeded, 700);
    }

    public OtpGuardianThread(OtpService otpService, AtomicBoolean reauthNeeded, long pollMillis) {
        this.otpService = otpService;
        this.reauthNeeded = reauthNeeded;
        this.pollMillis = (pollMillis <= 0) ? 700 : pollMillis;
        setName("OtpGuardianThread"); // 스레드 이름 지정 (디버깅에 도움)
        setDaemon(true); // 메인 프로그램 종료 시 이 스레드도 자동 종료되도록 설정
    }   // 생성자 end

    @Override
    public void run() { // run 스레드 실행 루프
        try {
            // 스레드가 중단되지 않는 한 무한 루프
            while (!isInterrupted()) {
                Thread.sleep(pollMillis); // 지정된 주기만큼 대기
                if (!otpService.checkValidUntil()) {
                    reauthNeeded.compareAndSet(false, true); // 이미 true면 유지
                }
            }
        } catch (InterruptedException e) {
            interrupt(); // 정상 종료
        }   // catch end
    }   // run end
}   // class end