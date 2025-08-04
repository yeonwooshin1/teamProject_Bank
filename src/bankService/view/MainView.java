package bankService.view;

import bankService.controller.AccountController;

import java.util.Scanner;

public class MainView {

    // 싱글톤 생성
    private MainView(){}
    private static final MainView instance = new MainView();
    public static MainView getInstance(){
        return instance;
    }

    // 싱글톤 가져오기
    public AccountController accountController = AccountController.getInstance();

    // Scanner 생성
    Scanner scan = new Scanner(System.in);

    // 로그인 후 은행 메인 view
    public void mainIndex(){
        for( ; ; ){
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("[1] 계좌관리");
            System.out.println("[2] 입·출금");
            System.out.println("[3] 계좌이체");
            System.out.println("[4] 보안설정");
            System.out.println("[0] 로그아웃");
            System.out.print("선택 ➜ ");
            int choose = scan.nextInt();
            System.out.println("==========================================");

            if(choose == 1){ }
            else if (choose == 2){ }
            else if (choose == 3){ }
            else if (choose == 4){ }
            else if (choose == 0){ }

        } // for e
    } // func e

    // 입금 view

    // 출금 view

    // 계좌이체 view


} // class e
