package bankService.app;  // package

import bankService.controller.AccountController;
import bankService.controller.OtpController;
import bankService.controller.UserController;
import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

import bankService.view.MainView;
import bankService.view.OtpView;
import bankService.view.UserView;
import com.sun.tools.javac.Main;

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

    //
    public void start() {

        // 앱 전역 콘솔 리소스(앱 수명 동안 1회 생성)
        final Scanner scan = new Scanner(System.in);
        final Object ioLock = new Object();
        final ConsoleStatus status = new ConsoleStatus();
        final OtpService otp = new OtpService();

        // 싱글톤 가져오기
        final UserView login = UserView.getInstance();
        final MainView main = MainView.getInstance();

        // 앱 실행
        while (true) {
            // ========== 1) 로그인 ==========
            // wire 연결
            login.wire(scan, ioLock , otp);
            OtpController.getInstance().wireOtp( otp);

            // 로그인 view 결과값 반환
            // 양수 = 성공 / uno 저장용으로 반환값 쓸 예정
            // 0 = 실패 혹은 로그인 외 다른 메소드 실행( 재시도 함 )
            // -1 = 종료 반환값

            // 로그인뷰 불러오기
            int result = login.index();

            // 결과가 -1 이면 종료
            if (result == -1) {
                System.out.println("안녕히 가세요.");
                break;
            }   // if end

            // 결과가 0이면 로그인뷰 재출력
            if (result == 0) continue;      // 재시도

            // result 값 양수면 그 값을 uno로 가정하고 저장
            int uno = result;

            // ========== 2) 메인서비스 시작 ==========

            // 로그인 직후 otp 2분 신뢰 시작
            otp.grantTrustNowForLogin();

            // context 값 할당
            ConsoleSession ctx = new ConsoleSession(uno, scan, ioLock, status, otp);

            // 유저넘버 , 락 객체 , 신뢰기간 , OTP서비스
            AccountController.getInstance().wire(ctx.userNo() ,ctx.otp());
            OtpController.getInstance().wireUno(ctx.userNo());
            UserController.getInstance().wire(ctx.userNo() , ctx.otp());


            // 재인증 뷰 연결(같은 otp/scan/status/ioLock)
            OtpView.getInstance().wire(ctx.otp(), ctx.scan(), ctx.status(), ctx.ioLock());

            // wire 주입
            main.wire(ctx);

            // ========== 3) 메인 세션  ==========
            while (true) {
                // 메인 세션 입장(스레드 시작)
                boolean mainResult = main.mainIndex();
                if (!mainResult) break;

            }   // while end
        } // while end
    }   // func end
}   // class end