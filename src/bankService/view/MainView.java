package bankService.view;

import bankService.app.ConsoleSession;
import bankService.controller.AccountController;
import bankService.controller.OtpController;
import bankService.controller.UserController;
import bankService.model.dto.*;
import bankService.service.OtpService;
import bankService.thread.OtpRemainingTimeViewThread;
import bankService.util.MoneyUtil;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.reader.EndOfFileException;
import bankService.model.dto.AccountDto;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;


import java.awt.*;
import java.util.ArrayList;

// 메인 뷰
public class MainView { // class start

    // 싱글톤 생성
    private MainView(){}
    private static final MainView instance = new MainView();
    public static MainView getInstance(){ return instance; }

    // 싱글톤 가져오기
    public AccountController accountController = AccountController.getInstance();
    public UserController userController = UserController.getInstance();

    // 의존성
    private ConsoleSession ctx;
    private Object ioLock;
    private final OtpController otpController = OtpController.getInstance();
    private LineReader reader;

    private OtpRemainingTimeViewThread otpTimerThread = null;

    /**
     * Router 에서 세션 정보 한 번에 주입.
     * 이후 모든 메서드는 매개변수 없이 내부 필드를 바로 사용.
     */
    // 섹션 연결
    public void wire(ConsoleSession ctx) {
        this.ctx  = ctx;
        this.ioLock = ctx.ioLock();
        this.reader = ctx.reader(); // LineReader로 변경
    }   // wire end



    public LineReader getReader() {
        return reader;
    }   // getter

    // 1. 상태바 문자열(volatile: 멀티스레드 안전)
    private volatile String statusBar = "";   // [상태바] 현재 남은 OTP 신뢰 시간 등 출력

    /**
     * [상태바 메세지 갱신용] OtpRemainingTimeViewThread 등에서 호출
     * - 이 메서드는 단순히 문자열만 갱신한다! (println에서 직접 출력)
     */
    public void setStatusBar(String msg) {
        if (this.statusBar != null && this.statusBar.equals(msg)) return; // 중복 방지
        this.statusBar = msg;   // 메뉴 출력 이후 하단에 출력 용도
    }

    /**
     * [상태바 출력] 메뉴/화면 출력 끝나고 마지막 줄에 호출!
     * - 이걸 각 메뉴/목록 출력 후 호출하면, printAbove 없이도 아래에 상태처럼 보임.
     */
    private void printStatusBarIfPresent() {
        if (statusBar != null && !statusBar.isEmpty()) {
            System.out.println(statusBar);
        }
    }


    // ================== LineReader + 상태바 입력 유틸 ==================

    private int readInt(String prompt) {
        while (true) {
            printStatusBarIfPresent(); // 입력 전 상태바 출력
            synchronized (ioLock) {
                try {
                    String line = reader.readLine(prompt).trim();
                    return Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    System.out.println("숫자를 입력하세요.");
                    // 계속 반복해서 다시 입력받음
                } catch (UserInterruptException | EndOfFileException e) {
                    System.out.println();
                    return -1; // 입력 중단
                }
            }
        }
    }

    private String readLine(String prompt) {
        printStatusBarIfPresent(); // 입력 전 상태바 출력
        synchronized (ioLock) {
            return reader.readLine(prompt).trim();
        }
    }   // readLine end

    /** [남은 인증시간 프롬프트 꾸미기 */
    private String fullPromptWithTime(String prompt) {
        long sec = ctx.otp().getRemainingTrustSeconds();
        String msg = (sec > 0) ? String.format(" [보안⏳ %d초]", sec) : " [보안 ⚠\uFE0F 재인증]";
        return prompt + msg + " ";
    }
    // 로그아웃시 스레드 종료
    public void stopOtpThread() {
        if (otpTimerThread != null && otpTimerThread.isAlive()) {
            otpTimerThread.interrupt();
        }
    }

    // ========== 이하 모든 readInt/readLine을 위 유틸로만 사용! ==========



    // ================= 로그인 후 은행 메인 view ================================
    public boolean mainIndex(){
        // mainIndex() 시작부
        if (otpTimerThread == null || !otpTimerThread.isAlive()) {
            otpTimerThread = new OtpRemainingTimeViewThread(ctx.otp(), this);
            otpTimerThread.start();
        }

        while (true){
            // 1) 보안 세션 확인/재인증
            if (!ensureAuthenticated()) {
                return false;
            }   // if end

            // 2) 메뉴 출력
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
                System.out.println(" ⚠\uFE0F 잘못된 입력입니다.");
            }   // if end
        }   // while end
    }   // func end

    // =============================== 계좌 관리 ======================================== //

    // 계좌 통합 view
    public boolean account(){
        if (!ensureAuthenticated()) return false;
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
            System.out.println(" ⚠\uFE0F 잘못된 입력입니다.");
        }   // if end
        return true;
    }   // func end

    // 계좌 생성 view
    public boolean accountAdd() {
        if (!ensureAuthenticated()) return false;
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
        System.out.println("< 새 계좌 개설 >");

        String account_pwd;
        while (true) {
            account_pwd = readLine("계좌 비밀번호 설정 (6자리 숫자를 입력하세요) : ");
            if (account_pwd.length() == 6 && account_pwd.matches("\\d{6}")) {
                break; // 조건 만족하면 탈출
            }
            System.out.println("비밀번호는 6자리 숫자여야 합니다. 다시 입력해주세요.");
        }   // while end

        boolean result = accountController.accountAdd(account_pwd);

        if (result) {
            System.out.println("\uD83C\uDFE6➕계좌가 개설되었습니다.");
        } else {
            System.out.println("계좌 개설 실패");
        }   // if end

        return true;
    }   // func end

    // 계좌 해지 view
    public boolean accountDel(){
        if (!ensureAuthenticated()) return false;
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
        System.out.println("< 계좌 해지 >");
        String account_no = readLine("해지할 계좌 번호 : ");
        String account_pwd = readLine("계좌 비밀 번호 : ");

        if (!ensureAuthenticated()) return false;

        boolean result = accountController.accountDel(account_no , account_pwd);

        if(result){
            System.out.println("\uD83C\uDFE6✂\uFE0F해지 성공");
        }
        else {
            System.out.println(" ⚠\uFE0F 해지 실패");
        }
        return true;
    }



    // 계좌 목록 view
    public void printMyTransactions() {
        Map<String, List<AccountDto>> txMap = accountController.getTransactionsByCurrentUser();

        if (txMap.isEmpty()) {
            System.out.println(" ⚠\uFE0F 거래 내역이 없습니다.");
            return;
        }   // if end

        for (String accNo : txMap.keySet()) {
            System.out.println("\n계좌번호: " + accNo);
            System.out.printf("%-10s %-15s %-15s %-20s %-10s\n", "📊"+ "거래유형", "금액", "잔액", "거래일자", "메모");

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
            }   // for end
        }   // for end
    }   // func end

    // ================================ 입·출금 , 이체 ================================ //

    // 입출금 view
    public boolean transation(){
        if (!ensureAuthenticated()) return false;
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
        System.out.println("[1] 입금");
        System.out.println("[2] 출금");
        System.out.println("[3] 뒤로");
        int choose = readInt("선택 ➜ ");
        System.out.println("===================================================================");

        if(choose == 1){ return deposit(); }
        else if (choose == 2) { return withdraw(); }
        else if (choose == 3) { return true; }
        return true;
    }   // func end

    // 이체 view
    public boolean transferView(){
        if (!ensureAuthenticated()) return false;
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
        System.out.println("[1] 이체");
        System.out.println("[2] 뒤로");
        int choose = readInt("선택 ➜ ");
        System.out.println("===================================================================");

        if(choose ==1 ){ return transfer(); }
        else if (choose ==2) { return true; }
        return true;
    }   // func end

    // 입금 view
    public boolean deposit(){
        if (!ensureAuthenticated()) return false;
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
        System.out.println("< 입금 >");
        String account_no = readLine("입금할 계좌 : ");
        String account_pwd = readLine("계좌 비밀번호 : ");
        int amount = readInt("입금할 금액 : ");

        if (!ensureAuthenticated()) return false;

        // 거래 금액 100만원 이상일 시 응답받기
        if(amount >= 1000000 ){
            String answer = readLine("입금금액이 100만원이 넘습니다. 정말 이체하시겠습니까? (Y/N) : ");
            if(answer.equals("n")){
                System.out.println("❌ 입금 취소!");
                return false;
            }
        } // if e

        TransactionDto dto = new TransactionDto(account_no , account_pwd , amount);
        TransactionResultDto resultDto = accountController.deposit(dto);

        if(resultDto.isSuccess()){
            System.out.println(" \uD83D\uDCB0 입금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        }else {
            System.out.println(" ⚠\uFE0F 입금 실패!");
            System.out.println(" ⚠\uFE0F 에러 메시지 : " + resultDto.getMessage());
        }   // if end
        return true;
    }   // func end

    // 출금 view
    public boolean withdraw(){
        if (!ensureAuthenticated()) return false;
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
        System.out.println("< 출금 >");
        String account_no = readLine("출금할 계좌번호를 입력하세요 : ");
        String account_pwd = readLine("계좌 비밀번호 입력 : ");
        int amount = readInt("출금할 금액 : ");

        if (!ensureAuthenticated()) return false;

        // 거래 금액 100만원 이상일 시 응답받기
        if(amount >= 1000000 ){
            String answer = readLine("출금금액이 100만원이 넘습니다. 정말 이체하시겠습니까? (Y/N) : ");
            if(answer.equals("n")){
                System.out.println("❌ 출금 취소!");
                return false;
            }
        } // if e

        TransactionDto dto = new TransactionDto(account_no , account_pwd ,amount);
        TransactionResultDto resultDto = accountController.withdraw(dto);
        if(resultDto.isSuccess()){
            System.out.println("\uD83D\uDCB5 출금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        }else {
            if ("잔액이 부족합니다.".equals(resultDto.getMessage())) {
                System.out.println(" ⚠\uFE0F 출금 실패!");
                System.out.println(" ⚠\uFE0F 잔액 부족");
                System.out.println("잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
            } else {
                System.out.println(" ⚠\uFE0F 출금 실패!");
                System.out.println("에러 메시지 : " + resultDto.getMessage());
            }   // if end
        }   // if end
        return true;
    }   // func end

    // 이체 view
    public boolean transfer() {
        if (!ensureAuthenticated()) return false;
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
        System.out.println("< 이체 >");
        String sender_no = readLine("이체할 계좌 : ");
        String receiver_no = readLine("이체받는 계좌 : ");
        String account_pwd = readLine("계좌 비밀번호 : ");
        int amount = readInt("이체할 금액 : ");
        String memo = readLine("이체 메모 : ");

        if (!ensureAuthenticated()) return false;

        // 거래 금액 100만원 이상일 시 응답받기
        if(amount >= 1000000 ){
            String answer = readLine("거래금액이 100만원이 넘습니다. 정말 이체하시겠습니까? (Y/N) : ");
            if( answer.equals("n")){
                System.out.println("❌ 이체 취소!");
               return false;
            }
        } // if e

        TransferDto dto = new TransferDto(sender_no, receiver_no, account_pwd, amount, memo);
        TransferResultDto resultDto = accountController.transfer(dto);

        if (resultDto.isSuccess()) {
            System.out.println("\uD83D\uDCB8 이체 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        } else {
            if ("잔액이 부족합니다.".equals(resultDto.getMessage())) {
                System.out.println(" ⚠\uFE0F 이체 실패!");
                System.out.println("잔액 부족");
                System.out.println("잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
            }
            if("같은 계좌로 이체할 수 없습니다.".equals(resultDto.getMessage())){
                System.out.println("❌ 이체 실패!");
                System.out.println("같은 계좌로 이체할 수 없습니다.");
            }



        }   // if end
        return true;
    }   // func end


    // ==================== 보안설정 view ====================

    public boolean securitySettingsView() {
        while (true) {
            if (!ensureAuthenticated()) return false;
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
            System.out.println("< 보안 설정 >");
            System.out.println("[1] 비밀번호 변경");
            System.out.println("[2] 회원 탈퇴");
            System.out.println("[3] 뒤로");
            int choose = readInt("선택 ➜ ");
            System.out.println("===================================================================");

            switch (choose) {
                case 1: changePassword(); break;
                case 2: boolean deleted = deleteAccount(); if (!deleted) return false; break;
                case 3: return true;
                default: System.out.println(" ⚠\uFE0F 올바른 번호를 입력해주세요."); break;
            }   // switch end
        }   // while end
    }   // func end

    // 5. 비밀번호 변경
    public boolean changePassword() {
        if (!ensureAuthenticated()) return false;

        String u_id = readLine("아이디 : ");
        String u_pwd = readLine("현재 비밀번호 : ");

        if (!ensureAuthenticated()) return false;

        boolean check = userController.verifyPassword(u_id, u_pwd);

        if (check) {
            String newPwd = readLine("새 비밀번호 : ");

            if (!ensureAuthenticated()) return false;

            boolean result = userController.update2Password(u_id, newPwd);
            if (result) System.out.println("비밀번호가 성공적으로 변경되었습니다.");
            else  System.out.println("비밀번호는 영어, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
        } else {
            System.out.println("비밀번호가 일치하지 않습니다.");
        }   // if end
        return true;
    }   // func end

    // 6. 회원 탈퇴
    public boolean deleteAccount() {
        if (!ensureAuthenticated()) return false;

        String u_id = readLine("아이디 : ");
        String u_pwd = readLine("비밀번호 : ");

        if (!ensureAuthenticated()) return false;

        boolean result = userController.deleteAccount(u_id, u_pwd);

        if (result) {
            System.out.println("탈퇴 성공했습니다.");
            return false; // 바로 로그아웃(메인뷰 빠져나감)
        }
        else {
            System.out.println(" ⚠\uFE0F 탈퇴 실패했습니다.");
            return true; // 계속 남음
        }   // if end
    }   // func end

    /**
     * 1) 신뢰 유효하면 true 리턴
     * 2) 만료 시 사용자에게 묻고,
     *    Y → OtpView.forceReauth() 실행 후 유효 여부 리턴
     *    N → false 리턴
     */
    public boolean ensureAuthenticated() {
        // 1) 아직 유효하면 바로 통과
        if (otpController.trustOtp()) return true;

        while (true) {
            String ans = readLine(" ⚠\uFE0F 보안 세션이 만료되었습니다. 인증하시겠습니까? ( Y / N ): ").toLowerCase();

            if (ans.equals("y")) {
                OtpView.getInstance().forceReauth();
                return otpController.trustOtp();
            }
            else if (ans.equals("n")) {
                String ansRe = readLine(" ⚠\uFE0F 미인증시 로그아웃 됩니다. 인증 하시겠습니까? ( Y / N ): ").toLowerCase();

                if (ansRe.equals("y")) {
                    OtpView.getInstance().forceReauth();
                    return otpController.trustOtp();
                } else if (ansRe.equals("n")) {
                    System.out.println("해당 계정에서 로그아웃 합니다.");
                    return false;
                }   // if end
            }   // if end
            else System.out.println("y , n 중 하나만 입력하세요.");
        }   // while end
    }   // func end
}   // class end
