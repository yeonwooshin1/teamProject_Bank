package bankService.app;  // package

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;
import bankService.view.MainView;
import bankService.view.OtpView;
import bankService.view.UserView;

import java.util.Scanner;

/**
 * AppRouter (Composition Root)
 * - 로그인 화면 호출 → userNo(양수) 받으면 세션 시작
 * - 같은 OtpService 인스턴스를 세션 전역으로 주입
 * - MainView beginSession에서 상태바/가디언 스레드 시작, endSession에서 정리
 * - 메뉴는 '한 번의 상호작용'만 처리(B 패턴): 결과코드로 흐름 제어
 */
public final class AppRouter {
    private AppRouter() {}
    private static final AppRouter INST = new AppRouter();
    public static AppRouter getInstance() { return INST; }

    public void start() {
        // 앱 전역 콘솔 리소스(앱 수명 동안 1회 생성)
        final Scanner scan = new Scanner(System.in);
        final Object ioLock = new Object();
        final ConsoleStatus status = new ConsoleStatus();

        final UserView login = UserView.getInstance();
        final MainView main  = MainView.getInstance();

        while (true) {
            // ========== 1) 로그인 ==========
            int userNo = login.index(scan, ioLock); // 양수=성공 / 0=실패(재시도)
            if (userNo <= 0) {
                synchronized (ioLock) { System.out.println("❌ 로그인 실패. 다시 시도하세요."); }
                continue; // 로그인 재시도
            }

            // ========== 2) 세션 시작 ==========
            OtpService otp = new OtpService();
            otp.grantTrustNowForLogin(); // 로그인 직후 2분 신뢰 시작

            ConsoleSession ctx = new ConsoleSession(userNo, scan, ioLock, status, otp);

            // 재인증 뷰 연결(같은 otp/scan/status/ioLock)
            OtpView.getInstance().wire(ctx.otp(), ctx.scan(), ctx.status(), ctx.ioLock());
            // 컨트롤러(출금/입금)에도 같은 otp 주입(DAO는 필요 시 추가)
            // BankController.getInstance().wire(/* dao */ null, ctx.otp);

            // 메인 세션 입장(스레드 시작)
            main.beginSession(ctx);

            // ========== 3) 메인 세션 루프(B 패턴) ==========
            while (true) {
                int r = main.menuOnce(); // -1: 종료, 0: 로그아웃, 1: 계속
                if (r == MainView.EXIT_APP) {
                    main.endSession(); // 스레드 정리
                    synchronized (ioLock) {
                        status.pause();
                        System.out.println("안녕히 가세요.");
                        status.resume();
                    }
                    return; // 앱 완전 종료
                }
                if (r == MainView.LOGOUT) {
                    main.endSession(); // 스레드 정리
                    break;             // 바깥 루프로 복귀 → 로그인 화면
                }
                // CONTINUE(1) → 다음 메뉴 1건 계속 처리
            }
        }
    }
}