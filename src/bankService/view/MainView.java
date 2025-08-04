package bankService.view;

import bankService.controller.UserController;

import java.util.Scanner;

public class MainView { // class start

    Scanner scan = new Scanner( System.in );

    // 싱글톤 가져오기
    UserController userController = UserController.getInstance();

    // 메인 보안설정 메뉴
    public void showSecurityMenu() {
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                 BB  BANK               ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println();
        System.out.println("< 보안 설정 >");
        System.out.println();
        System.out.println("[1] 비밀번호 변경");
        System.out.println("[2] 회원 탈퇴");
        System.out.println("[3] 뒤로");
        System.out.print("선택 ➜ ");
        int choose = scan.nextInt();
        System.out.println("==========================================");

        if (choose == 1) {
            changePassword();
        } else if (choose == 2) {
            deleteAccount();
        } else if (choose == 3) {
            return;
        }
    }


    // 5. 비밀번호 변경
    public void changePassword() {
        System.out.print("아이디: ");   String u_id = scan.next();
        System.out.print("현재 비밀번호: "); String u_pwd = scan.next();
        boolean check = userController.verifyPassword(u_id, u_pwd);
        if (check) {
            System.out.print("새 비밀번호: "); String newPwd = scan.next();
            boolean result = userController.update2Password(u_id, newPwd);
            if (result) System.out.println("비밀번호가 성공적으로 변경되었습니다.");
            else        System.out.println("비밀번호 변경에 실패했습니다.");
        } else {
            System.out.println("비밀번호가 일치하지 않습니다.");
        }
    }


    //----------------------------------------------------------------------------------------------------//

    // 6. 회원 탈퇴
    public void deleteAccount() {
        // 로그인된 사용자의 id가 있다고 가정, 여기는 직접 입력 받음
        System.out.print("아이디: ");   String u_id = scan.next();
        System.out.print("비밀번호: "); String u_pwd = scan.next();
        boolean result = userController.deleteAccount(u_id, u_pwd);
        if (result) System.out.println("탈퇴 성공했습니다.");
        else        System.out.println("탈퇴 실패했습니다.");
    }


    //----------------------------------------------------------------------------------------------------//

} // class end
