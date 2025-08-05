package bankService.app;

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

import java.util.Scanner;

// uno 및 세션 저장 클래스
public class ConsoleSession {  // class start

    // 멤버변수
    private final int uno;
    private final Scanner scan;         // 콘솔 입력
    private final Object ioLock;        // 콘솔 I/O 직렬화 락
    private final ConsoleStatus status; // 상태줄(남은시간) 표시 유틸
    private final OtpService otp;       // OTP 상태/검증 서비스(세션 중 1개)

    // 생성자
    public ConsoleSession(int userNo, Scanner scan, Object ioLock,
                          ConsoleStatus status, OtpService otp) {
        this.uno = userNo;
        this.scan = scan;
        this.ioLock = ioLock;
        this.status = status;
        this.otp = otp;
    }

    // getter
    public int userNo() { return uno; }
    public Scanner scan() { return scan; }
    public Object ioLock() { return ioLock; }
    public ConsoleStatus status() { return status; }
    public OtpService otp() { return otp; }

}   // class end