package bankService.view;

import bankService.app.ConsoleSession;
import bankService.controller.AccountController;
import bankService.controller.OtpController;
import bankService.controller.UserController;
import bankService.model.dto.*;

import bankService.thread.OtpRemainingTimeViewThread;
import bankService.util.MoneyUtil;
import org.jline.reader.LineReader;

import bankService.model.dto.AccountDto;
import java.util.List;

import java.util.Map;


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
        statusBar = "";          // ★ 이전 세션 메시지 초기화
    }   // wire end




    /* 현재 readLine() 에서 사용된 프롬프트 문자열을 저장 → 알림 후 재그리기 용도 */
    private String currentPrompt = "";

    /* 1) 상태바 문자열 (멀티스레드 안전) */
    private volatile String statusBar = "";

    // ───────────────── 상태바 관련 ─────────────────
    public void setStatusBar(String msg) {
        if (msg != null && msg.equals(this.statusBar)) return;
        this.statusBar = msg;
    }

    public LineReader getReader() { return reader; }

    // (메뉴 출력이 끝난 뒤 호출하면 상태바 1줄 표시)
    private void printStatusBarIfPresent() {
        if (statusBar != null && !statusBar.isEmpty()) {
            synchronized (ioLock) {
                System.out.println(statusBar);
            }
        }
    }

    /* MainView.java */
    public void showNoticeAndClearBuffer(String msg) {
        synchronized (ioLock) {

            /* 1) 현재 입력 줄 백업 */
            String bufLine = reader.getBuffer().toString();
            String prompt  = currentPrompt;          // 우리가 마지막에 넣어둔 프롬프트

            /* 2) 줄 삭제 */
            var term = reader.getTerminal();
            term.writer().print("\033[2K\r");        // ESC[2K + CR
            term.flush();
            reader.getBuffer().clear();

            /* 3) 알림 출력 (printAbove 가 프롬프트•입력 자동 재그리기) */
            reader.printAbove(msg);

            /* 4) 입력 내용만 다시 덮어씀 (프롬프트는 이미 있음) */
            term.writer().print(bufLine);
            term.flush();

            /* 5) 버퍼 복원 → 편집 계속 가능 */
            reader.getBuffer().write(bufLine);
        }
    }

    // ───────────────── 입력 유틸 ─────────────────

    // MainView.java  (클래스 맨 위 필드 부분)
    private volatile boolean inputInProgress = false;   // ★ 추가: 입력 중 여부

    public void setInputInProgress(boolean v) { inputInProgress = v; }  // ★ 추가
    public boolean isInputInProgress()       { return inputInProgress; } // ★ 추가

    /* 공통 헬퍼: 한 줄 입력 후 버퍼 클리어 */
    private String readOneLine(String prompt) {
        try {
            currentPrompt = prompt + " ";
            return reader.readLine(currentPrompt).trim();   // ① 사용자 입력
        } finally {
            reader.getBuffer().clear();                     // ② 입력 완료 직후 버퍼 삭제
        }
    }

    /* ─ readLine() ─ */
    private String readLine(String prompt) {
        printStatusBarIfPresent();
        setInputInProgress(true);
        try {
            return readOneLine(prompt);                     // 캐시 → 버퍼 복원 끊김
        } finally {
            setInputInProgress(false);
        }
    }

    /* ─ readInt() ─ */
    private int readInt(String prompt) {
        while (true) {
            printStatusBarIfPresent();
            setInputInProgress(true);
            try {
                String s = readOneLine(prompt);
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                synchronized (ioLock) { System.out.println("숫자를 입력하세요."); }
            } finally {
                setInputInProgress(false);
            }
        }
    }

    // 로그아웃시 스레드 종료
    public void stopOtpThread() {
        if (otpTimerThread != null && otpTimerThread.isAlive()) {
            otpTimerThread.interrupt();
        }   // if end
        statusBar = "";          // ★ 이전 세션 메시지 초기화
    }   // func end

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
                System.out.println("⚠\uFE0F 잘못된 입력입니다.");
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
            System.out.println("⚠\uFE0F 잘못된 입력입니다.");
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
            System.out.println("⚠\uFE0F 비밀번호는 6자리 숫자여야 합니다. 다시 입력해주세요.");
        }   // while end

        boolean result = accountController.accountAdd(account_pwd);

        if (result) {
            System.out.println("\uD83D\uDCB5 계좌가 개설되었습니다.");
        } else {
            System.out.println("⚠\uFE0F 계좌 개설 실패");
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
            System.out.println("\uD83D\uDCB5 해지 성공");
        }
        else {
            System.out.println("⚠\uFE0F 해지 실패");
        }
        return true;
    }



    // 계좌 목록 view
    public void printMyTransactions() {
        Map<String, List<AccountDto>> txMap = accountController.getTransactionsByCurrentUser();

        if (txMap.isEmpty()) {
            System.out.println("⚠\uFE0F 거래 내역이 없습니다.");
            return;
        }   // if end

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
            String answer = readLine("⚠\uFE0F 입금금액이 100만원이 넘습니다. 정말 이체하시겠습니까? (Y/N) : ");
            if(answer.equals("n")){
                System.out.println("⚠\uFE0F 입금 취소!");
                return false;
            }
        } // if e

        TransactionDto dto = new TransactionDto(account_no , account_pwd , amount);
        TransactionResultDto resultDto = accountController.deposit(dto);

        if(resultDto.isSuccess()){
            System.out.println("\uD83D\uDCB5 입금 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        }else {
            System.out.println("⚠\uFE0F 입금 실패!");
            System.out.println("⚠\uFE0F 에러 메시지 : " + resultDto.getMessage());
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
                System.out.println("⚠\uFE0F 출금 취소!");
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
            if ("⚠\uFE0F 잔액이 부족합니다.".equals(resultDto.getMessage())) {
                System.out.println("⚠\uFE0F 출금 실패!");
                System.out.println("⚠\uFE0F 잔액 부족");
                System.out.println("잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
            } else {
                System.out.println("⚠\uFE0F 출금 실패!");
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
            String answer = readLine("⚠\uFE0F 거래금액이 100만원이 넘습니다. 정말 이체하시겠습니까? (Y/N) : ");
            if( answer.equals("n")){
                System.out.println("⚠\uFE0F 이체 취소!");
               return false;
            }
        } // if e

        TransferDto dto = new TransferDto(sender_no, receiver_no, account_pwd, amount, memo);
        TransferResultDto resultDto = accountController.transfer(dto);

        if (resultDto.isSuccess()) {
            System.out.println("\uD83D\uDCB5 이체 성공!");
            System.out.println("메시지 : " + resultDto.getMessage());
            System.out.println("현재 잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
        } else {
            if ("⚠\uFE0F 잔액이 부족합니다.".equals(resultDto.getMessage())) {
                System.out.println("⚠\uFE0F 이체 실패!");
                System.out.println("⚠\uFE0F 잔액 부족");
                System.out.println("잔액 : " + MoneyUtil.formatWon(resultDto.getBalance()));
            }
            if("⚠\uFE0F 같은 계좌로 이체할 수 없습니다.".equals(resultDto.getMessage())){
                System.out.println("⚠\uFE0F 이체 실패!");
                System.out.println("⚠\uFE0F같은 계좌로 이체할 수 없습니다.");
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
                default: System.out.println("⚠\uFE0F 올바른 번호를 입력해주세요."); break;
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
            if (result) System.out.println("\uD83D\uDCB5 비밀번호가 성공적으로 변경되었습니다.");
            else  System.out.println("⚠\uFE0F 비밀번호는 영어, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
        } else {
            System.out.println("⚠\uFE0F 비밀번호가 일치하지 않습니다.");
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
            System.out.println("\uD83D\uDCB5 탈퇴 성공했습니다.");
            return false; // 바로 로그아웃(메인뷰 빠져나감)
        }

        else {
            System.out.println("⚠\uFE0F 아이디,비밀번호 입력 오류, 혹은 계좌가 존재하는 경우 탈퇴가 불가합니다.");
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
            String ans = readLine("⚠\uFE0F 보안 세션이 만료되었습니다. 인증하시겠습니까? ( Y / N ): ").toLowerCase();

            if (ans.equals("y")) {
                OtpView.getInstance().forceReauth();
                return otpController.trustOtp();
            }
            else if (ans.equals("n")) {
                String ansRe = readLine("⚠\uFE0F 미인증시 로그아웃 됩니다. 인증 하시겠습니까? ( Y / N ): ").toLowerCase();

                if (ansRe.equals("y")) {
                    OtpView.getInstance().forceReauth();
                    return otpController.trustOtp();
                } else if (ansRe.equals("n")) {
                    System.out.println("해당 계정에서 로그아웃 합니다.");
                    return false;
                }   // if end
            }   // if end
            else System.out.println("⚠\uFE0F y , n 중 하나만 입력하세요.");
        }   // while end
    }   // func end
}   // class end
