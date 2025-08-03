package bankService.view;

import bankService.util.ConsoleStatus;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 로그인 View (학습용)
 * - 자격증명 입력 후 "성공/실패만 boolean"으로 반환
 * - MainView 호출은 Router가 담당(여기서 직접 호출 금지)
 */
public class UserView {
    private UserView(){}
    private static final UserView INST = new UserView();
    public static UserView getInstance(){ return INST; }

    // 공용 리소스(라우터에서 1회 주입)
    private Scanner scan; private ConsoleStatus status; private Object ioLock;

    public void shared(Scanner scan, ConsoleStatus status, Object ioLock) {
        this.scan = scan; this.status = status; this.ioLock = ioLock;
    }

    /** 로그인 시도 → 성공(true) / 종료(false) */
    public boolean index() {
        synchronized (ioLock) {
            try {
                System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
                System.out.println("┃                 BB  BANK               ┃");
                System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                System.out.println("[1] 로그인");
                System.out.println("[2] 회원가입");
                System.out.println("[3] 아이디 찾기");
                System.out.println("[4] 비밀번호 찾기");
                System.out.println("[0] 종료");
                System.out.print("선택 ➜ ");
                int choose = scan.nextInt();
                System.out.println("==========================================");

                if (choose == 1) {
                } else if (choose == 2) {
                } else if (choose == 3) {
                } else if (choose == 4) {
                } else if (choose == 0) {
                }
            } catch (InputMismatchException e) {
                System.out.println("[경고] 숫자만 입력하세요.");
                scan.nextLine();
            }   // catch end
        }
        return true; // 성공 가정
    }
}