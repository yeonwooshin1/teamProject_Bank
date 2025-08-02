package bankService.view;

import bankService.controller.UserController;
import bankService.model.dto.IdResponseDto;
import bankService.model.dto.UserDto;

import java.util.Scanner;

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

            } else if (choose == 2) {

            } else if (choose == 3) {

            } else if (choose == 4) {

            } else if (choose == 0) {

            }
        }
    }


} // class end
