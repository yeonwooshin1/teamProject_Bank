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
        // 무한반복은 취합 시 빠질 예정
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


            if (choose == 1) { // 1번 선택하면 로그인
                login();
            } else if (choose == 2) { // 2번 선택하면 회원가입
                register();
            } else if (choose == 3) { // 3번 선택하면 아이디 찾기
                findId();
            } else if (choose == 4) { // 4번 선택하면 비밀번호 찾기
                findPassword();
            } else if (choose == 0) { // 0번 누르면 종료됨
                return;
            } // if end
        } // 무한반복 func end
    } // func end


    //----------------------------------------------------------------------------------------------------//

    // 1. 로그인
    public void login() {
        System.out.print("아이디: ");
        String u_id = scan.next();
        System.out.print("비밀번호: ");
        String u_pwd = scan.next();
        int result = userController.login(u_id, u_pwd);

        // 괄호 안써도 출력됨
        if (result == -1)
            System.out.println("로그인 5회 시도했습니다.");
        else if (result == 0)
            System.out.println("로그인 실패했습니다.");
        else
            System.out.println("로그인 성공했습니다.");
    } // func end


    //----------------------------------------------------------------------------------------------------//

    // 2. 회원가입
    public void register() {
        System.out.print("아이디: ");
        String u_id = scan.next();
        System.out.print("비밀번호: ");
        String u_pwd1 = scan.next();
        System.out.print("비밀번호 확인: ");
        String u_pwd2 = scan.next();
        System.out.print("이름: ");
        String u_name = scan.next();
        System.out.print("전화번호: ");
        String phone = scan.next();
        System.out.print("이메일: ");
        String email = scan.next();
        // 형식 맞춰서 입력 받음
        System.out.print("생년월일(yyyy-MM-dd): ");
        String u_date = scan.next();
        int result = userController.registerMember(u_id, u_pwd1, u_pwd2, u_name, phone, email, u_date);  // ← dto와 비번확인 함께 전달 // 비밀번호 확인까지 같이 전달
        // switch 문을 사용하면 result 값에 따라서 출력하는 메세지가 달라짐
        switch (result) {
            case 1  -> System.out.println("회원가입 성공했습니다.");
            case -1 -> System.out.println("중복된 아이디가 존재합니다.");
            case -2 -> System.out.println("입력하신 두 비밀번호가 일치하지 않습니다.");
            case -3 -> System.out.println("형식 오류가 발생했습니다.");
        }
    } // func end

    //----------------------------------------------------------------------------------------------------//

    // 3. 아이디 찾기
    public void findId() {
        System.out.print("이름: ");
        String u_name = scan. next();
        System.out.print("전화번호: ");
        String u_phone = scan.next();
        IdResponseDto result = userController.findId(u_name, u_phone);
        if (result != null)
            // DB에서 확인받아서 줘야 하니까
            System.out.println("당신의 아이디는: " + result.getU_id() + "입니다.");
        else
            System.out.println("일치하는 회원 정보가 없습니다.");
    } // func end


    //----------------------------------------------------------------------------------------------------//

    // 4. 비밀번호 찾기
    public void findPassword() {
        System.out.print("아이디: ");
        String u_id = scan.next();
        System.out.print("전화번호: ");
        String u_phone = scan.next();
        int check = userController.verifyAccount(u_id, u_phone);
        if (check == 1) {
            System.out.print("새 비밀번호: ");
            String newPwd = scan.next();
            int result = userController.updatePassword(u_id, newPwd);
            // if 안에 if
            if (result == 1)
                System.out.println("비밀번호 변경이 완료되었습니다.");
            else
                System.out.println("비밀번호 변경에 실패했습니다.");
        } else {
            System.out.println("입력 정보에 맞는 계정을 찾을 수 없습니다.");
        } // if end
    } // func end


    //----------------------------------------------------------------------------------------------------//



} // class end
