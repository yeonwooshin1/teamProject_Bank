package View;

import bankService.controller.AccountController;

import java.util.Scanner;

public class BankView {
    private BankView() { }
    private static final BankView instance = new BankView();
    public static BankView getInstance() {
        return instance;
    }

    private AccountController ac = AccountController.getInstance();
    Scanner sc = new Scanner(System.in);
    public void index(){
        for ( ;; ){
            System.out.println("[1]계좌 등록");
            System.out.println("[2]계좌 해지");
            System.out.println("[3]내 계좌 목록");
            System.out.println("[4]뒤로가기");
            System.out.println("선택 : ");
            int ch = sc.nextInt();
            if (ch == 1){ AccountAdd();  }
            else if (ch == 2) { AccountDel(); }
            else if (ch == 3) { AccountList(); }
            else if (ch == 4) { }
        }
    }
    public void AccountAdd(){

    }

    public void AccountDel(){

    }

    public void AccountList() {

    }

}
