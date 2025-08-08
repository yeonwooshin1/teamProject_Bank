package bankService.app;  // package

import bankService.controller.AccountController;
import bankService.controller.OtpController;
import bankService.controller.UserController;
import bankService.service.OtpService;


import bankService.thread.OtpRemainingTimeViewThread;
import bankService.view.MainView;
import bankService.view.OtpView;
import bankService.view.UserView;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Scanner;

// view 들을 관리
public final class AppRouter {  // class start

    // 싱글톤 만들기
    private AppRouter() {
    }
    private static final AppRouter INST = new AppRouter();
    public static AppRouter getInstance() {
        return INST;
    }

    // bb bank 서비스 원동력 start
    public void start() {
        try {
            // 앱 전역 콘솔 리소스(앱 수명 동안 1회 생성)
            final Object ioLock = new Object();
            final OtpService otp = new OtpService();
            final Scanner scan = new Scanner(System.in);

            // JLine3 터미널/리더 준비

            final Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();

            final LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            /* ★ Bash-식 이벤트 확장(!!, !123 등) 완전히 비활성화 */
            reader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);

            // 싱글톤 가져오기
            final UserView login = UserView.getInstance();
            final MainView main = MainView.getInstance();

            // 앱 실행
            while (true) {
                // ========== 1) 로그인 ==========
                // wire 연결 otp 랑 login
                login.wire( scan ,reader, ioLock, otp);
                OtpController.getInstance().wireOtp(otp);

                // 로그인 view 결과값 반환
                // 양수 = 성공 / uno 저장용으로 반환값 쓸 예정
                // 0 = 실패 혹은 로그인 외 다른 메소드 실행( 재시도 함 )

                // 로그인뷰 불러오기
                int result = login.index();

                // 결과가 0이면 로그인뷰 재출력
                if (result == 0) continue;      // 재시도

                // result 값 양수면 그 값을 uno로 가정하고 저장
                int uno = result;

                // ========== 2) 메인서비스 시작 ==========

                // 로그인 직후 otp 2분 신뢰 시작
                otp.grantTrustNowForLogin();

                // context 값 할당
                ConsoleSession ctx = new ConsoleSession(uno, scan ,reader, ioLock, otp);

                // 유저넘버 , OTP 서비스 wire 연결
                AccountController.getInstance().wire(ctx.userNo(), ctx.otp());
                OtpController.getInstance().wireUno(ctx.userNo());
                UserController.getInstance().wire(ctx.userNo(), ctx.otp());

                // otp 재인증 뷰 연결(같은 otp/reader/scan/ioLock)
                OtpView.getInstance().wire(ctx.otp(), ctx.scan() , ctx.ioLock() ,ctx.reader());

                // wire 주입
                main.wire(ctx);

                // ========== 3) 메인 세션  ==========
                while (true) {
                    // 메인 세션 입장(스레드 시작)
                    boolean mainResult = main.mainIndex();

                    // 반환값 false면 로그인 창 부르고 스레드 종료 true면 mainView 재실행
                    if (!mainResult) {
                        main.stopOtpThread();
                        break;
                    }   // if end

                }   // while end
                OtpRemainingTimeViewThread.interrupted();
            } // while end
        } catch (IOException e) {
            System.out.println("JLine3 터미널 초기화 실패: " + e.getMessage());
        }   // catch end

    }   // func end
}   // class end