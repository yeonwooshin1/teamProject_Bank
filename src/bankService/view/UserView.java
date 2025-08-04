package bankService.view;

import bankService.controller.UserController;
import bankService.model.dto.IdResponseDto;

import java.util.Scanner;


// 유저와 직접적으로 소통하는 콘솔/화면 담당
public class UserView { // class start

    // 싱글톤
    private UserView(){}
    private static final UserView instance = new UserView();
    public static UserView getInstance(){
        return instance;
    }


    // 싱글톤 가져오기
    UserController userController = UserController.getInstance();

    Scanner scan = new Scanner(System.in);

    public void index() {
        for (; ; ) {
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
                login();
            } else if (choose == 2) {
                register();
            } else if (choose == 3) {
                findId();
            } else if (choose == 4) {
                findPassword();
            } else if (choose == 0) {
                return;
            }
        }
    }

    // 1. 로그인
    public void login() {
        System.out.print("아이디: "); String u_id = scan.next();
        System.out.print("비밀번호: "); String u_pwd = scan.next();
        int result = userController.login(u_id, u_pwd);
        if (result == -1)      System.out.println("로그인 5회 시도했습니다.");
        else if (result == 0)  System.out.println("로그인 실패했습니다.");
        else                   System.out.println("로그인 성공했습니다.");
    }

    // 2. 회원가입
    public void register() {
        System.out.print("아이디: ");   String u_id = scan.next();
        System.out.print("비밀번호: "); String u_pwd1 = scan.next();
        System.out.print("비밀번호 확인: "); String u_pwd2 = scan.next();
        System.out.print("이름: ");     String u_name = scan.next();
        System.out.print("전화번호: "); String phone = scan.next();
        System.out.print("이메일: ");   String email = scan.next();
        System.out.print("생년월일(yyyy-MM-dd): "); String u_date = scan.next();
        int result = userController.registerMember(u_id, u_pwd1, u_pwd2, u_name, phone, email, u_date);  // ← dto와 비번확인 함께 전달 // 비밀번호 확인까지 같이 전달
        switch (result) {
            case 1  -> System.out.println("회원가입 성공했습니다.");
            case -1 -> System.out.println("중복된 아이디가 존재합니다.");
            case -2 -> System.out.println("입력하신 두 비밀번호가 일치하지 않습니다.");
            case -3 -> System.out.println("형식 오류가 발생했습니다.");
        }
    }

    // 3. 아이디 찾기
    public void findId() {
        System.out.print("이름: ");   String u_name = scan. next();
        System.out.print("전화번호: "); String u_phone = scan.next();
        IdResponseDto result = userController.findId(u_name, u_phone);
        if (result != null)
            System.out.println("당신의 아이디는: " + result.getU_id());
        else
            System.out.println("일치하는 회원 정보가 없습니다.");
    }

    // 4. 비밀번호 찾기
    public void findPassword() {
        System.out.print("아이디: ");   String u_id = scan.next();
        System.out.print("전화번호: "); String u_phone = scan.next();
        int check = userController.verifyAccount(u_id, u_phone);
        if (check == 1) {
            System.out.print("새 비밀번호: "); String newPwd = scan.next();
            int result = userController.updatePassword(u_id, newPwd);
            if (result == 1) System.out.println("비밀번호 변경이 완료되었습니다.");
            else             System.out.println("비밀번호 변경에 실패했습니다.");
        } else {
            System.out.println("입력 정보에 맞는 계정을 찾을 수 없습니다.");
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

    // 6. 회원 탈퇴
    public void deleteAccount() {
        // 로그인된 사용자의 id가 있다고 가정, 여기는 직접 입력 받음
        System.out.print("아이디: ");   String u_id = scan.next();
        System.out.print("비밀번호: "); String u_pwd = scan.next();
        boolean result = userController.deleteAccount(u_id, u_pwd);
        if (result) System.out.println("탈퇴 성공했습니다.");
        else        System.out.println("탈퇴 실패했습니다.");
    }


} // class end
