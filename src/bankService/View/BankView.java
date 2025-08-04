package bankService.View;

import bankService.controller.AccountddController;
import bankService.model.dao.AccountddDao;
import bankService.model.dto.AccountDto;

import java.sql.SQLOutput;
import java.util.Scanner;

public class BankView {
    private BankView() { }
    private static final BankView instance = new BankView();
    public static BankView getInstance() {
        return instance;
    }
    int uno = 1;


    private AccountddController ac = AccountddController.getInstance();
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

    public void AccountAdd() {
        System.out.print("계좌 비밀번호 입력 : "); String account_pwd = sc.next();

        boolean result = ac.AccountAdd(account_pwd);

        if(result){
            System.out.println("계좌 등록 성공");
        }else {
            System.out.println("계좌 등록 실패");
        }

    }

    public void AccountDel() {
        System.out.print("해지할 계좌번호 : ");
        String account_no = sc.next();
        System.out.print("계좌 비밀번호 입력 : ");
        String account_pwd = sc.next();

        boolean result = ac.AccountDel(account_no, account_pwd);

        if (result) {
            System.out.println("계좌 해지 성공");
        } else {
            System.out.println("계좌 해지 실패 ");

        }
    }

    public void AccountList() {

    }

}
