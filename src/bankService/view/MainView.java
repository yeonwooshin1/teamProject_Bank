package bankService.view;

import bankService.app.ConsoleSession;
import bankService.controller.AccountController;
import bankService.controller.OtpController;
import bankService.controller.UserController;
import bankService.model.dto.*;
import bankService.service.OtpService;
import bankService.thread.OtpRemainingTimeViewThread;
import bankService.util.ConsoleStatus;

import java.util.ArrayList;
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
    public UserController userController = UserController.getInstance();



    // ───────── 주입될 의존성 ─────────
    private  ConsoleSession ctx;                // 한 번에 묶어서 보관
    private Scanner scan;             // 콘솔 입력
    private Object  ioLock;           // I/O 직렬화 락
    private ConsoleStatus    status;           // 상태바 제어
    private final OtpController otpController = OtpController.getInstance();

    /**
     * Router 에서 세션 정보 한 번에 주입.
     * 이후 모든 메서드는 매개변수 없이 내부 필드를 바로 사용.
     */
    public void wire(ConsoleSession ctx) {
        this.ctx  = ctx;
        this.scan = ctx.scan();
        this.ioLock = ctx.ioLock();
        this.status = ctx.status();
    }
    // otp 신뢰시간 초마다 알려주는 thread
    private OtpRemainingTimeViewThread otpTimerThread = null;

    // 로그인 후 은행 메인 view
    public boolean mainIndex(){
        // 1) 세션 시작 시 스레드 실행
        if (otpTimerThread == null || !otpTimerThread.isAlive()) {
            otpTimerThread = new OtpRemainingTimeViewThread(ctx.otp(), status);
            otpTimerThread.start();
        }
        try {
            while (true){
                // 1) 보안 세션 확인/재인증
                if (!ensureAuthenticated()) {
                    return false;
                }   // if end

                // 2) 메뉴 출력
                synchronized (ioLock) {
                    status.pause(); // 상태줄 깨끗이
                    System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
                    System.out.println("┃                 BB  BANK               ┃");
                    System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                    System.out.println("[1] 계좌관리  [2] 입·출금  [3] 계좌이체");
                    System.out.println("[4] 보안설정  [0] 로그아웃");
                    System.out.print("선택 ➜ ");
                    status.resume();
                }   // syn end
                int choose = scan.nextInt();
                System.out.println("==========================================");

                if(choose == 1){ boolean ok = account();
                    if (!ok) return false; }
                else if (choose == 2){  boolean ok = transation();
                    if (!ok) return false; }
                else if (choose == 3){ boolean ok = transferView();
                    if (!ok) return false; }
                else if (choose == 4){ boolean ok = securitySettingsView();
                    if (!ok) return false; }// ← 회원탈퇴 성공 등으로 false면 바로 return false
                else if (choose == 0){ return false;}
                else {
                    synchronized (ioLock) {
                        status.pause();
                        System.out.println("잘못된 입력입니다.");
                        status.resume();
                    }   // syn end
                }   // else end
            }   // while end
        } finally {
            // 2) 세션이 끝나면 스레드 정지
            if (otpTimerThread != null && otpTimerThread.isAlive()) {
                otpTimerThread.interrupt();
                otpTimerThread = null;
            }
        }
    } // func e

    // =============================== 계좌 관리 ======================================== //

    // 계좌 관리 view
    public boolean account(){

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("[1] 새 계좌 개설");
            System.out.println("[2] 계좌 해지");
            System.out.println("[3] 내 계좌 목록");
            System.out.println("[4] 뒤로");
            System.out.print("선택 ➜ ");
            status.resume();
        }   // syn end
        int choose = scan.nextInt();
        System.out.println("==========================================");

        if(choose ==1 ){ accountAdd(); }
        else if (choose == 2 ){ accountDel(); }
        else if (choose == 3){ accountList(); }
        else if (choose == 4){ return true; }
        else {
            synchronized (ioLock) {
                status.pause();
                System.out.println("잘못된 입력입니다.");
                status.resume();
            }   // syn end
        }   // else end
        return true;
    } // func end

    // 계좌 등록 view
    public boolean accountAdd(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock){
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("< 새 계좌 개설 >");
            System.out.print("계좌 비밀번호 설정 : ");
            status.resume();
        }   // syn end

        String account_pwd = scan.next();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        boolean result = accountController.accountAdd(account_pwd);

        synchronized (ioLock) {
            status.pause();
            if(result){
                System.out.println("계좌가 개설되었습니다.");
            }else {
                System.out.println("계좌 개설 실패 ");
            }   // if end
            status.resume();
        }   // syn end

        return true;
    }   // func end

    // 계좌 해지 view
    public boolean accountDel(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock){
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("< 계좌 해지 >");
            System.out.print("해지할 계좌 번호");
            status.resume();
        }   // syn end

        String account_no = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.println("계좌 비밀 번호 :");
            status.resume();
        }   // syn end
        String account_pwd = scan.next();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        boolean result = accountController.accountDel(account_no , account_pwd);

        synchronized (ioLock) {
            status.pause();
            if(result){
                System.out.println("해지 성공");
            }
            else {
                System.out.println("해지 실패");
            }   // if end
            status.resume();
        }   // syn end

        return true;
    }   // func end


    // 계좌 목록 view
    public boolean accountList(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("< 내 계좌 목록 >");
            System.out.print("조회할 회원 이름을 입력하세요: ");
            status.resume();
        }   // syn end
        scan.nextLine();
        String u_name = scan.nextLine();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        ArrayList<AccountDto> list = accountController.accountList(u_name);

        synchronized (ioLock) {
            status.pause();
            if (list.isEmpty()) {
                System.out.println("해당 이름의 거래내역이 없습니다.");
            } else {
                for (AccountDto dto : list) {
                    System.out.printf("[거래번호: %d] 계좌: %s | 출금: %d | 입금: %d | 유형: %s | 금액: %d | 메모: %s | 날짜: %s\n",
                            dto.getTno(),
                            dto.getAccount_no(),
                            dto.getFrom_acno(),
                            dto.getTo_acno(),
                            dto.getType(),
                            dto.getAmount(),
                            dto.getMemo(),
                            dto.getT_date()
                    );
                }   // for end
            }   // if end
            status.resume();
        }   // syn end
        return true;
    }   // func end

    // ================================ 겨레 입금 , 출금 , 이체 ================================ //

    // 입·출금 view
    public boolean transation(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("[1] 입금");
            System.out.println("[2] 출금");
            System.out.println("[3] 뒤로");
            System.out.print("선택 ➜ ");
            status.resume();
        }   // syn end
        int choose = scan.nextInt();

        synchronized (ioLock) {
            status.pause();
            System.out.println("==========================================");
            status.resume();
        }   // syn end

        if(choose == 1){ deposit(); }
        else if (choose == 2) { withdraw();}
        else if (choose == 3) { return true; }
        return true;
    }   // func end

    // 계좌 이체 view
    public boolean transferView(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("[1] 이체");
            System.out.println("[2] 뒤로");
            System.out.print("선택 ➜ ");
            status.resume();
        }   // syn end
        int choose = scan.nextInt();

        synchronized (ioLock) {
            status.pause();
            System.out.println("==========================================");
            status.resume();
        }   // syn end

        if(choose ==1 ){ transfer();}
        else if (choose ==2) { return true; }
        return true;
    }   // func end

    // 입금 view
    public boolean deposit(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("< 입금 >");
            System.out.println("입금할 계좌 : ");
            status.resume();
        }   // syn end
        String account_no = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.println("계좌 비밀번호 : ");
            status.resume();
        }   // syn end
        String account_pwd = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.println("입금할 금액 : ");
            status.resume();
        }
        int amount = scan.nextInt();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        TransactionDto dto = new TransactionDto(account_no , account_pwd , amount);
        TransactionResultDto resultDto = accountController.deposit(dto);

        synchronized (ioLock) {
            status.pause();
            if(resultDto.isSuccess()){
                System.out.println("✅ 입금 성공!");
                System.out.println("메시지 : " + resultDto.getMessage());
                System.out.println("현재 잔액 : " + resultDto.getBalance()+ "원");

            }else {
                System.out.println("❌ 입금 실패!");
                System.out.println("에러 메시지 : " + resultDto.getMessage());
            }   // if end
            status.resume();
        }   // syn end
        return true;
    } // func e

    // 출금 view
    public boolean withdraw(){
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("< 출금 >");
            System.out.print("출금할 계좌번호를 입력하세요. : ");
            status.resume();
        }   // syn end
        String account_no = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.print("계좌 비밀번호 입력 : ");
            status.resume();
        }
        String account_pwd = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.print("출금할 금액 : ");
            status.resume();
        }   // syn end
        int amount = scan.nextInt();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        TransactionDto dto = new TransactionDto(account_no , account_pwd ,amount);
        TransactionResultDto resultDto = accountController.withdraw(dto);
        synchronized (ioLock) {
            status.pause();
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
                }   // else2 end
            }   // else1 end
            status.resume();
        }   // syn end
        return true;
    } // func e

    // 계좌이체 view
    public boolean transfer() {
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃                 BB  BANK               ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            System.out.println("< 이체 >");
            System.out.print("이체할 계좌 : ");
            status.resume();
        }
        String sender_no = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.println("이체받는 계좌 : ");
            status.resume();
        }   // syn end
        String receiver_no = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.println("계좌 비밀번호 : ");
            status.resume();
        }   // syn end
        String account_pwd = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.println("이체할 금액 : ");
            status.resume();
        }   // syn end
        int amount = scan.nextInt();

        synchronized (ioLock) {
            status.pause();
            System.out.println("이체 메모 : ");
            status.resume();
        }   // syn end
        String memo = scan.next();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        TransferDto dto = new TransferDto(sender_no, receiver_no, account_pwd, amount, memo);
        TransferResultDto resultDto = accountController.transfer(dto);

        synchronized (ioLock) {
            status.pause();
            if (resultDto.isSuccess()) {
                System.out.println("✅ 이체 성공!");

            } else {
                if ("잔액이 부족합니다.".equals(resultDto.getMessage())) {
                    System.out.println("❌ 이체 실패!");
                    System.out.println("잔액 부족");
                    System.out.println("잔액 : " + resultDto.getBalance() + "원");
                }   // if end
            }   // else end
            status.resume();
        }   // syn end
        return true;
    } // func e

    // 보안설정 view
    public boolean securitySettingsView() {
        while (true) {
            // 1) 보안 확인
            if (!ensureAuthenticated()) {
                return false;   // 재인증 거부 시 뒤로(혹은 로그아웃) 처리
            }   // if end

            // 2) 메뉴 화면 출력
            synchronized (ioLock) {
                status.pause();
                System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
                System.out.println("┃                 BB  BANK               ┃");
                System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
                System.out.println("< 보안 설정 >");
                System.out.println("[1] 비밀번호 변경");
                System.out.println("[2] 회원 탈퇴");
                System.out.println("[3] 뒤로");
                System.out.print("선택 ➜ ");
                status.resume();
            }   // syn end

            // 3) 사용자 선택 대기
            int choose = scan.nextInt();
            scan.nextLine(); // 입력 버퍼 클리어

            // 4) 구분선 출력
            synchronized (ioLock) {
                status.pause();
                System.out.println("==========================================");
                status.resume();
            }   // syn end

            // 5) 분기 처리
            switch (choose) {
                case 1:
                    boolean result1 = changePassword();    // 비밀번호 변경 로직
                    break;
                case 2:
                    boolean deleted = deleteAccount();
                    if (!deleted) return false;
                    break;
                case 3:
                    return true;
                default:
                    synchronized (ioLock) {
                        status.pause();
                        System.out.println("❌ 올바른 번호를 입력해주세요.");
                        status.resume();
                    }   // syn end
                    break;
                }
               // switch end
        }   // while end
    }   // func end

    // 5. 비밀번호 변경
    public boolean changePassword() {
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        synchronized (ioLock) {
            status.pause();
            System.out.print("아이디: ");
            status.resume();
        }   // syn end
        String u_id = scan.next();
        synchronized (ioLock) {
            status.pause();
            System.out.print("현재 비밀번호: ");
            status.resume();
        }   // syn end
        String u_pwd = scan.next();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        boolean check = userController.verifyPassword(u_id, u_pwd);

        if (check) {
            synchronized (ioLock){
                status.pause();
                System.out.print("새 비밀번호: ");
                status.resume();
            }   // syn end
            String newPwd = scan.next();

            // 보안 확인
            if (!ensureAuthenticated()) {
                return false;
            }   // if end

            boolean result = userController.update2Password(u_id, newPwd);
            synchronized (ioLock) {
                status.pause();
                if (result) System.out.println("비밀번호가 성공적으로 변경되었습니다.");
                else        System.out.println("비밀번호 변경에 실패했습니다.");
                status.resume();
            }   // syn end
        } else {
            synchronized (ioLock) {
                status.pause();
                System.out.println("비밀번호가 일치하지 않습니다.");
                status.resume();
            }   // syn end
        }   // else end
        return true;
    }   // func end

    // 6. 회원 탈퇴
    public boolean deleteAccount() {
        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        // 로그인된 사용자의 id가 있다고 가정, 여기는 직접 입력 받음
        synchronized (ioLock) {
            status.pause();
            System.out.print("아이디: ");
            status.resume();
        }   // syn end
        String u_id = scan.next();

        synchronized (ioLock) {
            status.pause();
            System.out.print("비밀번호: ");
            status.resume();
        }   // syn end
        String u_pwd = scan.next();

        // 보안 확인
        if (!ensureAuthenticated()) {
            return false;
        }   // if end

        boolean result = userController.deleteAccount(u_id, u_pwd);

        synchronized (ioLock) {
            status.pause();
            if (result) {
                System.out.println("탈퇴 성공했습니다.");
                status.resume();
                return false; // 바로 로그아웃(메인뷰 빠져나감)
            }
            else {
                System.out.println("탈퇴 실패했습니다.");
                status.resume();
                return true; // 계속 남음
            }
        }   // syn end

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
            // 화면 깨끗하게: 상태줄 숨기기
            synchronized (ioLock) {
                ctx.status().pause();
                System.out.print("⚠️ 보안 세션이 만료되었습니다. 인증하시겠습니까? (Y/N): ");
            }   // if end
            String ans = scan.nextLine().trim().toLowerCase();

            if (ans.equals("y")) {
                // 사용자 동의 → OtpView로 재인증 화면 전환
                OtpView.getInstance().forceReauth();
                // 재인증 후 유효하면 true, 아니면 false
                return otpController.trustOtp();
            }   // if end
            else if (ans.equals("n")) {
                synchronized (ioLock) {
                    System.out.println("⚠️ 미인증시 로그아웃 됩니다. 인증 하시겠습니까? (Y/N): ");
                }   // syn end
                String ansRe = scan.nextLine().trim().toLowerCase();

                if (ansRe.equals("y")) {
                    // 사용자 동의 → OtpView로 재인증 화면 전환
                    OtpView.getInstance().forceReauth();
                    // 재인증 후 유효하면 true, 아니면 false
                    return otpController.trustOtp();
                } else if (ansRe.equals("n")) {
                    synchronized (ioLock){
                        System.out.println("해당 계정에서 로그아웃 합니다.");
                    }   // syn end
                    return false;
                }   // if end
            }   // if end
            else System.out.println("y , n 중 하나만 입력하세요.");
        }   // while end
    }   // func end

} // class e
