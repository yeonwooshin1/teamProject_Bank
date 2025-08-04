package bankService.View;

import bankService.controller.AccountddController;
import bankService.model.dao.AccountddDao;
import bankService.model.dto.AccountDto;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class BankView {
    private BankView() { }
    private static final BankView instance = new BankView();
    public static BankView getInstance() {
        return instance;
    }


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
            else if (ch == 3) {int uno = 1; AccountList(uno); }
            else if (ch == 4) {
                System.out.println("4");
                break;
            }
        }
    }

    public void AccountAdd() {
        System.out.print("계좌 비밀번호 입력 : ");
        String account_pwd = sc.next();


        boolean result = ac.AccountAdd(account_pwd);

        if(result){
            System.out.println("계좌 등록 성공");
        }else {
            System.out.println("계좌 등록 실패");
        }

    }

    public void AccountDel() {
        int uno =  1;
        System.out.print("해지할 계좌번호 : ");
        String account_no = sc.next();
        System.out.print("계좌 비밀번호 입력 : ");
        String account_pwd = sc.next();

        boolean result = ac.AccountDel(uno , account_no, account_pwd);

        if (result) {
            System.out.println("계좌 해지 성공");
        } else {
            System.out.println("계좌 해지 실패 ");

        }
    }
    // 계좌 조회
    public void AccountList(int uno) {

        ArrayList<String> accountList = ac.AccountListUno(uno);

        if (accountList.isEmpty()){
            System.out.println("보유하신 계좌가 없습니다.");
            return;
        }

        for (String account : accountList) {
            System.out.println("계좌번호 : "+ account);

            ArrayList<AccountDto> tsa = ac.AccountList(account);

            if (tsa.isEmpty()) {
                System.out.println("거래 내역이 없습니다.");
            } else {
                for (AccountDto dto : tsa) {
                    System.out.printf("거래번호: %d, 유형: %s, 금액: %d, 메모: %s, 날짜: %s\n",
                            dto.getTno(), dto.getType(), dto.getAmount(), dto.getMemo(), dto.getT_date());
                }
            }
            System.out.println("---------------------------");
        }
    }
}
