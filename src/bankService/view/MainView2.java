package bankService.view;

import bankService.controller.AccountController;
import bankService.controller.UserController;
import bankService.model.dto.*;
import bankService.util.MoneyUtil;
import org.jline.reader.LineReader;

import java.util.List;
import java.util.Map;

// 메인 뷰 (OTP 제거, 세션 유지)
public class MainView2 { // class start

    // 싱글톤 생성
    private MainView2(){}
    private static final MainView2 instance = new MainView2();
    public static MainView2 getInstance(){ return instance; }

    // 싱글톤 가져오기
    public AccountController accountController = AccountController.getInstance();
    public UserController userController = UserController.getInstance();

    // 의존성
    private Object ioLock;
    private LineReader reader;

    /**
     * Router 에서 세션 정보 한 번에 주입.
     */
    public void wire(Object ctx) {
        this.ioLock = ctx; // 실제 사용 환경에 맞춰 수정 가능
        // reader 설정 필요 시 추가
    }

    /* 상태바 관련 */
    private volatile String statusBar = "";
    private String currentPrompt = "";

    public void setStatusBar(String msg) {
        if (msg != null && msg.equals(this.statusBar)) return;
        this.statusBar = msg;
    }

    private void printStatusBarIfPresent() {
        if (statusBar != null && !statusBar.isEmpty()) {
            synchronized (ioLock) {
                System.out.println(statusBar);
            }
        }
    }

    /* 입력 유틸 */
    private String readLine(String prompt) {
        printStatusBarIfPresent();
        System.out.print(prompt + " ");
        try {
            return new java.util.Scanner(System.in).nextLine().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private int readInt(String prompt) {
        while (true) {
            printStatusBarIfPresent();
            String s = readLine(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력하세요.");
            }
        }
    }

    // ================= 로그인 후 은행 메인 view ================================
    public boolean mainIndex(){
        while (true){
            System.out.println("===================================================================\n");
            System.out.println( " /$$$$$$$  /$$$$$$$        /$$$$$$$                      /$$      \n" +
                    "| $$__  $$| $$__  $$      | $$__  $$                    | $$      \n" +
                    "| $$  \\ $$| $$  \\ $$      | $$  \\ $$  /$$$$$$  /$$$$$$$ | $$   /$$\n" +
                    "| $$$$$$$ | $$$$$$$       | $$$$$$$  |____  $$| $$__  $$| $$  /$$/\n" +
                    "| $$__  $$| $$__  $$      | $$__  $$  /$$$$$$$| $$  \\ $$| $$$$$$/ \n" +
                    "| $$  \\ $$| $$  \\ $$      | $$  \\ $$ /$$__  $$| $$  | $$| $$_  $$ \n" +
                    "| $$$$$$$/| $$$$$$$/      | $$$$$$$/|  $$$$$$$| $$  | $$| $$ \\  $$\n" +
                    "|_______/ |_______/       |_______/  \\_______/|__/  |__/|__/  \\__/\n" +
                    "                                                                 ");
            System.out.println("===================================================================");
            System.out.println("[1] 계좌관리  [2] 입·출금  [3] 계좌이체");
            System.out.println("[4] 보안설정  [0] 로그아웃");
            int choose = readInt("선택 ➜ ");

            System.out.println("===================================================================");

            if(choose == 1){ boolean ok = account(); if (!ok) return false; }
            else if (choose == 2){  boolean ok = transation(); if (!ok) return false; }
            else if (choose == 3){ boolean ok = transferView(); if (!ok) return false; }
            else if (choose == 4){ boolean ok = securitySettingsView(); if (!ok) return false; }
            else if (choose == 0){ return false;}
            else {
                System.out.println("⚠\uFE0F 잘못된 입력입니다.");
            }
        }
    }

    // =============================== 계좌 관리 ========================================
    public boolean account(){
        System.out.println("===================================================================\n");
        System.out.println("< 계좌 관리 >");
        System.out.println("[1] 새 계좌 개설");
        System.out.println("[2] 계좌 해지");
        System.out.println("[3] 내 계좌 목록");
        System.out.println("[4] 뒤로");
        int choose = readInt("선택 ➜ ");
        System.out.println("===================================================================");

        if(choose ==1 ){ return accountAdd(); }
        else if (choose == 2 ){ return accountDel(); }
        else if (choose == 3){ printMyTransactions(); }
        else if (choose == 4){ return true; }
        else {
            System.out.println("⚠\uFE0F 잘못된 입력입니다.");
        }
        return true;
    }

    public boolean accountAdd() {
        System.out.println("< 새 계좌 개설 >");
        String account_pwd;
        while (true) {
            account_pwd = readLine("계좌 비밀번호 설정 (6자리 숫자를 입력하세요) : ");
            if (account_pwd.length() == 6 && account_pwd.matches("\\d{6}")) {
                break;
            }
            System.out.println("⚠\uFE0F 비밀번호는 6자리 숫자여야 합니다. 다시 입력해주세요.");
        }

        boolean result = accountController.accountAdd(account_pwd);

        if (result) {
            System.out.println("\uD83D\uDCB5 계좌가 개설되었습니다.");
        } else {
            System.out.println("⚠\uFE0F 계좌 개설 실패");
        }
        return true;
    }

    public boolean accountDel(){
        System.out.println("< 계좌 해지 >");
        String account_no = readLine("해지할 계좌 번호 : ");
        String account_pwd = readLine("계좌 비밀 번호 : ");

        boolean result = accountController.accountDel(account_no , account_pwd);

        if(result){
            System.out.println("\uD83D\uDCB5 해지 성공");
        }
        else {
            System.out.println("⚠\uFE0F 해지 실패");
        }
        return true;
    }

    public void printMyTransactions() {
        Map<String, List<AccountDto>> txMap = accountController.getTransactionsByCurrentUser();

        if (txMap.isEmpty()) {
            System.out.println("⚠\uFE0F 거래 내역이 없습니다.");
            return;
        }

        for (String accNo : txMap.keySet()) {
            System.out.println("\n계좌번호: " + accNo);
            System.out.printf("%-10s %-15s %-15s %-20s %-10s\n", "거래유형", "금액", "잔액", "거래일자", "메모");

            long balance = 0;
            for (AccountDto tx : txMap.get(accNo)) {
                switch (tx.getType()) {
                    case "입금", "이체_입금" -> balance += tx.getAmount();
                    case "출금", "이체_출금" -> balance -= tx.getAmount();
                }

                System.out.printf("%-10s %-15s %-15s %-20s %10s\n",
                        tx.getType(),
                        MoneyUtil.formatWon(tx.getAmount()),
                        MoneyUtil.formatWon((int)balance),
                        tx.getT_date(),
                        tx.getMemo() == null ? "null" : tx.getMemo());
            }
        }
    }

    // ================================ 입·출금 , 이체 ================================
    public boolean transation(){
        System.out.println("[1] 입금");
        System.out.println("[2] 출금");
        System.out.println("[3] 뒤로");
        int choose = readInt("선택 ➜ ");

        if(choose == 1){ return deposit(); }
        else if (choose == 2) { return withdraw(); }
        else if (choose == 3) { return true; }
        return true;
    }

    public boolean transferView(){
        System.out.println("[1] 이체");
        System.out.println("[2] 뒤로");
        int choose = readInt("선택 ➜ ");

        if(choose ==1 ){ return transfer(); }
        else if (choose ==2) { return true; }
        return true;
    }

    public boolean deposit(){
        System.out.println("< 입금 >");
        String account_no = readLine("입금할 계좌 : ");
        String account_pwd = readLine("계좌 비밀번호 : ");
        int amount = readInt("입금할 금액 : ");

        TransactionDto dto = new TransactionDto(account_no , account_pwd , amount);
        TransactionResultDto resultDto = accountController.deposit(dto);

        if(resultDto.isSuccess()){
            System.out.println("\uD83D\uDCB5 입금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        }else {
            System.out.println("⚠\uFE0F 입금 실패!");
            System.out.println("⚠\uFE0F 에러 메시지 : " + resultDto.getMessage());
        }
        return true;
    }

    public boolean withdraw(){
        System.out.println("< 출금 >");
        String account_no = readLine("출금할 계좌번호를 입력하세요 : ");
        String account_pwd = readLine("계좌 비밀번호 입력 : ");
        int amount = readInt("출금할 금액 : ");

        TransactionDto dto = new TransactionDto(account_no , account_pwd ,amount);
        TransactionResultDto resultDto = accountController.withdraw(dto);
        if(resultDto.isSuccess()){
            System.out.println("\uD83D\uDCB5 출금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        }else {
            System.out.println("⚠\uFE0F 출금 실패!");
        }
        return true;
    }

    public boolean transfer() {
        System.out.println("< 이체 >");
        String sender_no = readLine("이체할 계좌 : ");
        String receiver_no = readLine("이체받는 계좌 : ");
        String account_pwd = readLine("계좌 비밀번호 : ");
        int amount = readInt("이체할 금액 : ");
        String memo = readLine("이체 메모 : ");

        TransferDto dto = new TransferDto(sender_no, receiver_no, account_pwd, amount, memo);
        TransferResultDto resultDto = accountController.transfer(dto);

        if (resultDto.isSuccess()) {
            System.out.println("\uD83D\uDCB5 이체 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        } else {
            System.out.println("⚠\uFE0F 이체 실패!");
        }
        return true;
    }

    // ==================== 보안설정 view ====================
    public boolean securitySettingsView() {
        while (true) {
            System.out.println("< 보안 설정 >");
            System.out.println("[1] 비밀번호 변경");
            System.out.println("[2] 회원 탈퇴");
            System.out.println("[3] 뒤로");
            int choose = readInt("선택 ➜ ");

            switch (choose) {
                case 1: changePassword(); break;
                case 2: boolean deleted = deleteAccount(); if (!deleted) return false; break;
                case 3: return true;
                default: System.out.println("⚠\uFE0F 올바른 번호를 입력해주세요."); break;
            }
        }
    }

    public boolean changePassword() {
        String u_id = readLine("아이디 : ");
        String u_pwd = readLine("현재 비밀번호 : ");

        boolean check = userController.verifyPassword(u_id, u_pwd);

        if (check) {
            String newPwd = readLine("새 비밀번호 : ");
            boolean result = userController.update2Password(u_id, newPwd);
            if (result) System.out.println("\uD83D\uDCB5 비밀번호가 성공적으로 변경되었습니다.");
            else  System.out.println("⚠\uFE0F 비밀번호는 영어, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
        } else {
            System.out.println("⚠\uFE0F 비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    public boolean deleteAccount() {
        String u_id = readLine("아이디 : ");
        String u_pwd = readLine("비밀번호 : ");

        boolean result = userController.deleteAccount(u_id, u_pwd);

        if (result) {
            System.out.println("\uD83D\uDCB5 탈퇴 성공했습니다.");
            return false;
        } else {
            System.out.println("⚠\uFE0F 아이디,비밀번호 입력 오류, 혹은 계좌가 존재하는 경우 탈퇴가 불가합니다.");
            return true;
        }
    }
} // class end
