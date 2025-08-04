package bankService.view;

//import bankService.app.ConsoleSession;
//import bankService.service.OtpService;
//import bankService.thread.OtpGuardianThread;
//import bankService.thread.OtpRemainingTimeViewThread;
//import bankService.util.ConsoleStatus;
//
//import java.util.Scanner;
//import java.util.concurrent.atomic.AtomicBoolean;
//
///**
// * MainView
// * - beginSession(ctx): 세션 진입 시 스레드 시작(상태바/가디언)
// * - menuOnce(): 메뉴 1건 처리 후 결과코드 반환 (-1 종료 / 0 로그아웃 / 1 계속)
// * - endSession(): 스레드 정리
// */
//public final class MainView {
//    public static final int EXIT_APP = -1;
//    public static final int LOGOUT   =  0;
//    public static final int CONTINUE =  1;
//    // 싱글톤
//    private static final MainView INST = new MainView();
//    public static MainView getInstance() { return INST; }
//    private MainView() {}
//
//    private ConsoleSession ctx;
//    private AtomicBoolean reauthNeeded; // 가디언이 만료 감지 시 true
//    private Thread tRemain, tGuardian;
//
//    /** 세션 입장: ctx 보관 + 상태바/가디언 시작 */
//    public void beginSession(ConsoleSession ctx) {
//        this.ctx = ctx;
//        this.reauthNeeded = new AtomicBoolean(false);
//
//        // 상태바(1초마다 남은 시간 표시 — 상태줄 전용, ioLock은 안 잡음)
//        tRemain = new OtpRemainingTimeViewThread(ctx.otp(), ctx.status());
//        tRemain.setDaemon(true);
//        tRemain.start();
//
//        // 가디언(주기적으로 만료 감지하여 플래그만 세움 — 콘솔 I/O 없음)
//        tGuardian = new OtpGuardianThread(ctx.otp(), reauthNeeded);
//        tGuardian.setDaemon(true);
//        tGuardian.start();
//
//        // 환영 메세지
//        synchronized (ctx.ioLock()) {
//            ctx.status().pause();
//            System.out.println("\n👋 사용자 #" + ctx.userNo + "님 환영합니다. (OTP 보호가 활성화됨)");
//            ctx.status().resume();
//        }
//    }
//
//    /** 세션 종료: 시작한 스레드 정리 */
//    public void endSession() {
//        stopThread(tGuardian);
//        stopThread(tRemain);
//        tGuardian = tRemain = null;
//        reauthNeeded = null;
//        ctx = null;
//    }
//    private void stopThread(Thread t) {
//        if (t == null) return;
//        t.interrupt();
//        try { t.join(500); } catch (InterruptedException ignored) {}
//    }
//
//    /** 메뉴 1건 처리 후 결과코드 반환 */
//    public int menuOnce() {
//        // 1) 메뉴 진입 전: 만료 시 재인증 유도(가디언 플래그/실시간 체크)
//        if (reauthNeeded != null && reauthNeeded.get() || !ctx.otp.checkValidUntil()) {
//            if (!askReauthOrLogout()) return LOGOUT; // 로그아웃 선택 시
//            reauthNeeded.set(false); // 재인증 성공 → 플래그 내림
//        }
//
//        // 2) 메뉴 출력/입력 (I/O는 ioLock)
//        int sel;
//        synchronized (ctx.ioLock()) {
//            ctx.status().pause();
//            System.out.println("\n================ Console Bank =================");
//            System.out.println("1.입금  2.출금  9.로그아웃  0.종료");
//            System.out.print("선택 > ");
//            while (!ctx.scan().hasNextInt()) ctx.scan.next();
//            sel = ctx.scan().nextInt(); ctx.scan.nextLine();
//            ctx.status().resume();
//        }
//
//        if (sel == 0) return EXIT_APP;
//        if (sel == 9) return LOGOUT;
//
//        // 3) 거래 전 게이트(입력 전 체크)
//        if (!ctx.otp().checkValidUntil()) {
//            synchronized (ctx.ioLock()) {
//                ctx.status().pause();
//                System.out.println("❌ 보안 세션 만료. 재인증이 필요합니다.");
//                ctx.status().resume();
//            }
//            OtpView.getInstance().forceReauth();
//            return CONTINUE;
//        }
//
//        // 4) 예시: 출금
//        if (sel == 2) {
//            String amtStr;
//            synchronized (ctx.ioLock()) {
//                ctx.status().pause();
//                System.out.print("출금 금액 > ");
//                amtStr = ctx.scan().nextLine().trim();
//                ctx.status().resume();
//            }
//
//            // 처리 직전 게이트(입력 중 만료 대응)
//            if (!ctx.otp().checkValidUntil()) {
//                synchronized (ctx.ioLock()) {
//                    ctx.status().pause();
//                    System.out.println("⌛ 입력 중 만료되어 거래가 취소되었습니다.");
//                    ctx.status().resume();
//                }
//                OtpView.getInstance().forceReauth();
//                return CONTINUE;
//            }
//
//            try {
//                long amt = Long.parseLong(amtStr);
//                // 컨트롤러에서 '최종 재검증'을 한 번 더 수행
//                BankController.getInstance().withdraw(ctx.userNo, amt);
//                synchronized (ctx.ioLock) {
//                    ctx.status.pause();
//                    System.out.println("✅ " + amt + "원 출금 완료");
//                    ctx.status.resume();
//                }
//            } catch (SecurityException se) {
//                synchronized (ctx.ioLock) {
//                    ctx.status.pause();
//                    System.out.println("❌ 보안 세션 만료로 출금이 거절되었습니다. 재인증 후 다시 시도하세요.");
//                    ctx.status.resume();
//                }
//                OtpView.getInstance().forceReauth();
//            } catch (Exception e) {
//                synchronized (ctx.ioLock) {
//                    ctx.status.pause();
//                    System.out.println("❌ 오류: " + e.getMessage());
//                    ctx.status.resume();
//                }
//            }
//            return CONTINUE;
//        }
//
//        // 5) 예시: 입금(동일 패턴)
//        if (sel == 1) {
//            synchronized (ctx.ioLock) {
//                ctx.status.pause();
//                System.out.println("💰 입금 처리(샘플) 완료");
//                ctx.status.resume();
//            }
//            return CONTINUE;
//        }
//
//        // 기타
//        synchronized (ctx.ioLock) {
//            ctx.status.pause();
//            System.out.println("⚠️ 알 수 없는 메뉴입니다.");
//            ctx.status.resume();
//        }
//        return CONTINUE;
//    }
//
//    /** 재인증 질문/분기: true=세션 유지, false=로그아웃 */
//    private boolean askReauthOrLogout() {
//        String ans;
//        synchronized (ctx.ioLock) {
//            ctx.status.pause();
//            System.out.print("\n[보안] OTP 신뢰시간이 만료되었습니다. 인증 받으시겠습니까? (Y/N): ");
//            ans = ctx.scan.nextLine().trim().toLowerCase();
//            ctx.status.resume();
//        }
//        if ("y".equals(ans)) {
//            OtpView.getInstance().forceReauth(); // 재인증 화면
//            return ctx.otp.checkValidUntil();    // 성공했는지 확인
//        }
//        String lg;
//        synchronized (ctx.ioLock) {
//            ctx.status.pause();
//            System.out.print("로그아웃 하시겠습니까? (Y/N): ");
//            lg = ctx.scan.nextLine().trim().toLowerCase();
//            ctx.status.resume();
//        }
//        return !"y".equals(lg);
//    }
//}
