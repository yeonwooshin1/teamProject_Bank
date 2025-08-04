package bankService.app;  // package

import bankService.service.OtpService;
import bankService.util.ConsoleStatus;

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

    // 싱글톤 만들기
    private AppRouter() {}
    private static final AppRouter INST = new AppRouter();
    public static AppRouter getInstance() { return INST; }
    //
    public void start() {

        // 앱 전역 콘솔 리소스(앱 수명 동안 1회 생성)
        final Scanner scan = new Scanner(System.in);
        final Object ioLock = new Object();
        final ConsoleStatus status = new ConsoleStatus();

        // 싱글톤 가져오기
        final UserView login = UserView.getInstance();
        // final MainView main  = MainView.getInstance();

        // 앱 실행
        while (true) {
            // ========== 1) 로그인 ==========
            // wire 연결
            // login.wire(scan , ioLock);

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
            // otp 단 하나로 공유할 객체 생성
            OtpService otp = new OtpService();

            // 로그인 직후 otp 2분 신뢰 시작
            otp.grantTrustNowForLogin();

            // context 값 할당
            // 유저넘버 , 락 객체 , 신뢰기간 , OTP서비스
            ConsoleSession ctx = new ConsoleSession(uno, scan, ioLock, status, otp);

            // 재인증 뷰 연결(같은 otp/scan/status/ioLock)
            // OtpView.getInstance().wire(ctx.otp(), ctx.scan(), ctx.status(), ctx.ioLock());
            // 컨트롤러(출금/입금)에도 같은 otp 주입(DAO는 필요 시 추가)
            // BankController.getInstance().wire(/* dao */ null, ctx.otp);

//            // 메인 세션 입장(스레드 시작)
//            main.beginSession(ctx);
//
//            // ========== 3) 메인 세션 루프(B 패턴) ==========
//            while (true) {
//                int r = main.menuOnce(); // -1: 종료, 0: 로그아웃, 1: 계속
//                if (r == MainView.EXIT_APP) {
//                    main.endSession(); // 스레드 정리
//                    synchronized (ioLock) {
//                        status.pause();
//                        System.out.println("안녕히 가세요.");
//                        status.resume();
//                    }
//                    return; // 앱 완전 종료
//                }
//                if (r == MainView.LOGOUT) {
//                    main.endSession(); // 스레드 정리
//                    break;             // 바깥 루프로 복귀 → 로그인 화면
//                }
//                // CONTINUE(1) → 다음 메뉴 1건 계속 처리
            }
        }
    }
