package bankService.app;

import bankService.service.OtpService;
import org.jline.reader.LineReader;

import java.util.Scanner;

// uno 및 세션 저장 클래스
public class ConsoleSession {  // class start

    // 멤버변수
    private final int uno;
    private final Scanner scan;         // 콘솔 입력
    private final Object ioLock;        // 콘솔 I/O 직렬화 락
    private final OtpService otp;       // OTP 상태/검증 서비스(세션 중 1개)
    private final LineReader reader;     // 콘솔 입력(JLine3)

    // 생성자
    public ConsoleSession(int userNo, Scanner scan , LineReader reader, Object ioLock,
                          OtpService otp) {
        this.uno = userNo;
        this.reader = reader;
        this.ioLock = ioLock;
        this.otp = otp;
        this.scan = scan;
    }

    // getter
    public int userNo() { return uno; }
    public Scanner scan() { return scan; }
    public Object ioLock() { return ioLock; }
    public OtpService otp() { return otp; }
    public LineReader reader() { return reader; }

}   // class end