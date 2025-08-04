package bankService.view;

import bankService.controller.AccountController;
import bankService.model.dto.TransactionDto;
import bankService.model.dto.TransactionResultDto;
import bankService.model.dto.TransferDto;
import bankService.model.dto.TransferResultDto;

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
            else if (choose == 2){ transation(); }
            else if (choose == 3){ transferView(); }
            else if (choose == 4){ }
            else if (choose == 0){ }

        } // for e
    } // func e

    // 입·출금 view
    public void transation(){
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                 BB  BANK               ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println("[1] 입금");
        System.out.println("[2] 출금");
        System.out.println("[3] 뒤로");
        System.out.print("선택 ➜ ");
        int choose = scan.nextInt();
        System.out.println("==========================================");

        if(choose == 1){ deposit(); }
        else if (choose == 2) {withdraw();}
        else if (choose == 3) {mainIndex();}
    }

    // 계좌 이체 view
    public void transferView(){
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                 BB  BANK               ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println("[1] 이체");
        System.out.println("[2] 뒤로");
        System.out.print("선택 ➜ ");
        int choose = scan.nextInt();
        System.out.println("==========================================");

        if(choose ==1 ){transfer();}
        else if (choose ==2) { mainIndex(); }

    }

    // 입금 view
    public void deposit(){
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                 BB  BANK               ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println("< 입금 >");
        System.out.println("입금할 계좌 : ");    String account_no = scan.next();
        System.out.println("계좌 비밀번호 : ");  String account_pwd = scan.next();
        System.out.println("입금할 금액 : ");    int amount = scan.nextInt();


        TransactionDto dto = new TransactionDto(account_no , account_pwd , amount);
        TransactionResultDto resultDto = accountController.deposit(dto);

        if(resultDto.isSuccess()){
            System.out.println("✅ 입금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + resultDto.getBalance()+ "원");

        }else {
            System.out.println("❌ 입금 실패!");
            System.out.println("에러 메시지 : " + resultDto.getMessage());
        }

    } // func e

    // 출금 view
    public void withdraw(){
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                 BB  BANK               ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println("< 출금 >");
        System.out.println("출금할 계좌 : ");    String account_no = scan.next();
        System.out.println("계좌 비밀번호 : ");  String account_pwd = scan.next();
        System.out.println("출금할 금액 : ");    int amount = scan.nextInt();

        TransactionDto dto = new TransactionDto(account_no , account_pwd ,amount);
        TransactionResultDto resultDto = accountController.withdraw(dto);

        if(resultDto.isSuccess()){
            System.out.println("✅ 입금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + resultDto.getBalance()+ "원");
        }else {
            if ("잔액이 부족합니다.".equals(resultDto.getMessage())) {
                System.out.println("❌ 출금 실패!");
                System.out.println("잔액 부족");
                System.out.println("잔액 : " + resultDto.getBalance() + "원");
            } else {
                System.out.println("❌ 출금 실패!");
                System.out.println("에러 메시지 : " + resultDto.getMessage());
            }
        }
    } // func e

    // 계좌이체 view
    public void transfer() {
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                 BB  BANK               ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println("< 이체 >");
        System.out.println("이체할 계좌 : ");
        String sender_no = scan.next();
        System.out.println("이체받는 계좌 : ");
        String receiver_no = scan.next();
        System.out.println("계좌 비밀번호 : ");
        String account_pwd = scan.next();
        System.out.println("이체할 금액 : ");
        int amount = scan.nextInt();
        System.out.println("이체 메모 : ");
        String memo = scan.next();

        TransferDto dto = new TransferDto(sender_no, receiver_no, account_pwd, amount, memo);
        TransferResultDto resultDto = accountController.transfer(dto);

        if (resultDto.isSuccess()) {
            System.out.println("✅ 이체 성공!");

        } else {
            if ("잔액이 부족합니다.".equals(resultDto.getMessage())) {
                System.out.println("❌ 이체 실패!");
                System.out.println("잔액 부족");
                System.out.println("잔액 : " + resultDto.getBalance() + "원");
            }

        }


    } // func e

} // class e
