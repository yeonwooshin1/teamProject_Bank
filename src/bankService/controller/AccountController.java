package bankService.controller;

import bankService.model.dao.AccountDao;
import bankService.model.dto.*;
import bankService.service.OtpService;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AccountController {
    // 입금 , 출금 , 이체

    // 싱글톤 생성
    public AccountController(){}
    private static final AccountController instance = new AccountController();
    public static AccountController getInstance(){
        return instance;
    }

    // AccountDao 싱글톤 가져오기
    private final AccountDao accountDao = AccountDao.getInstance();

    // wire 멤버변수
    private int uno;
    private OtpService otpService;

    // wire 세션 연결
    public void wire (int uno ,OtpService otp){
        this.uno = uno;
        this.otpService = otp;
    }


    // ============================ 이겨레 입금 , 출금 , 이체 ================================ //

    // 입금 메소드
    public TransactionResultDto deposit(TransactionDto dto){
        String account_no= dto.getAccount_no();
        String pwd = dto.getAccount_pwd();
        int amount = dto.getAmount();

        // 계좌 유효성 검사
        boolean isVaild = accountDao.isAccount( account_no , pwd );
        if(!isVaild){
            return new TransactionResultDto(false , "계좌번호 또는 비밀번호가 일치하지 않습니다." , -1);
        } // if e

        // 계좌 로그 번호 조회
        int to_acno = accountDao.getAcnoByAccountNo(account_no);
        if( to_acno== -1 ){  // 계좌로그번호 조회 시 없으면
            return new TransactionResultDto(false , "계좌번호로 계좌 정보를 찾을 수 없습니다." , -1);
        } // if e

        // 거래내역 저장
        boolean isSaved = accountDao.saveTransaction(1001 , to_acno , amount , null , "입금");
        if(!isSaved){
            return new TransactionResultDto(false , "입금 실패 : 거래내역 저장 중 오류 발생" , -1);
        } // if e

        // 잔액 확인 및 잔액 업데이트
        int balance = accountDao.isBalance(to_acno);

        // 성공/실패 여부
        return new TransactionResultDto(true, "입금이 완료되었습니다." , balance );

    } // func e


    // 출금 메소드
    public TransactionResultDto withdraw(TransactionDto dto){
        String account_no = dto.getAccount_no();
        String pwd = dto.getAccount_pwd();
        int amount = dto.getAmount();

        // 계좌 유효성 검사
        boolean isVaild = accountDao.isAccount(account_no , pwd);
        if (!isVaild){
            return new TransactionResultDto(false , "계좌번호 또는 비밀번호가 일치하지 않습니다." , -1);
        } // if e

        // 계좌 로그 번호 조회
        int from_acno = accountDao.getAcnoByAccountNo(account_no);
        if (from_acno == -1){ // 계좌로그번호 조회시 없으면
            return new TransactionResultDto(false , "계좌번호로 계좌 정보를 찾을 수 없습니다." , -1);
        } // if e

        // 잔액 확인 및 잔액 업데이트
        int balance = accountDao.isBalance(from_acno);
        if (balance < amount ){
            return new TransactionResultDto(false , "잔액이 부족합니다." , balance);
        } // if e

        // 거래내역 저장
        boolean isSaved = accountDao.saveTransaction(from_acno , 1001 , amount , null , "출금");
        if (!isSaved){
            return new TransactionResultDto(false , "출금 실패 : 거래 내역 저장 중 오류 발생 " , balance);
        } // if e

        // 성공/실패 여부
        int updateBalance = accountDao.isBalance(from_acno);
        return new TransactionResultDto(true , "출금이 완료되었습니다." , updateBalance);

    }

    // 이체 메소드
    public TransferResultDto transfer(TransferDto dto){
        String senderNo = dto.getSender_no();
        String senderPwd = dto.getAccount_pwd();
        String receiverNo = dto.getReceiver_no();
        int amount = dto.getAmount();
        String memo = dto.getT_text();

        // 이체할 계좌 확인
        boolean isVaildSender = accountDao.isAccount(senderNo , senderPwd);
        if(!isVaildSender){
            return new TransferResultDto(false , "이체할 계좌번호 또는 비밀번호가 일치하지 않습니다." , -1);
        } // if e

        // 이체 받을 계좌 확인
        boolean isVaildReceiver = accountDao.receiveAccount( receiverNo );
        if(!isVaildReceiver){
            return new TransferResultDto(false, "이체받을 계좌가 존재하지 않습니다." , -1);
        } // if e

        // 이체할 계좌 로그 번호 조회
        int from_acno = accountDao.getAcnoByAccountNo(senderNo);
        if (from_acno == -1){ // 계좌로그번호 조회시 없으면
            return new TransferResultDto(false , "계좌번호로 이체할 계좌 정보를 찾을 수 없습니다." , -1);
        } // if e

        // 이체 받을 계좌 로그 번호 조회
        int to_acno = accountDao.getAcnoByAccountNo(receiverNo);
        if(to_acno == -1){
            return new TransferResultDto(false , "계좌번호로 이체받을 계좌 정보를 찾을 수 없습니다." , -1);
        } // if e

        // 잔액 확인 및 잔액 업데이트
        int balance = accountDao.isBalance(from_acno);      // 이체할 계좌 잔액 계산 후 저장
        if(balance < amount){
            return new TransferResultDto(false , "잔액이 부족합니다." , balance);
        } // if e

        // 거래내역 저장
        boolean isSaved = accountDao.saveTransaction(from_acno , to_acno , amount , memo , "이체");
        if(!isSaved){
            return new TransferResultDto(false , "이체 실패 : 거래 내역 저장 중 오류 발생 " , -1);
        } // if e

        // 성공/실패 여부
        int updateBalance = accountDao.isBalance(from_acno);
        return new TransferResultDto(true ,"이체가 완료되었습니다." , updateBalance);

    } // func e


    // =========================== 지훈씨 계좌 관리 ================================== //

    // 계좌 등록
    public boolean accountAdd(String account_pwd){
        int uno = this.uno;
        if (uno < 1) {
            System.out.println("세션이 없어서 계좌 해지 불가");
            return false;
        }

        // 비밀번호 유효성 검사 추가
        if (account_pwd == null || !account_pwd.matches("\\d{6}")) {
            System.out.println("비밀번호는 6자리 숫자여야 합니다.");
            return false;
        }

        AccountDto dto = new AccountDto();
        dto.setAccount_pwd(account_pwd);
        dto.setUno(uno);

        return accountDao.accountAdd(dto);
    }

    // 계좌 해지
    public boolean accountDel( String account_no, String account_pwd) {
        // 계좌 번호 일치하지 않을시
        if (!accountDao.accountnoexists(account_no)) {
            System.out.println("존재하지 않는 계좌번호 입니다.");
            return false;
        }

        // 비밀번호 일치 x
        Integer uno = accountDao.getUnoByAccount(account_no, account_pwd);
        if (uno == null) {
            System.out.println("계좌 비밀번호가 일치하지 않습니다.");
            return false;
        }
        // 계좌 해지 시 잔액있음 불가
        int acno = accountDao.getAcnoByAccountNo(account_no);
        int balance = accountDao.isBalance(acno);
        if (balance > 0) {
            System.out.println("해지하시려는 계좌 잔액이 남아있습니다. 잔액 이동 후 해지해주세요.");
            return false;
        }
        return accountDao.accountDel(account_no, account_pwd, uno);
    }


    // 계좌번호 중복 검증
    public boolean accountCheck(String account_no) {
        return accountDao.accountCheck(account_no);
    }


    // 현재 로그인된 사용자 기준 계좌별 거래내역 Map 반환
    public Map<String, List<AccountDto>> getTransactionsByCurrentUser() {
        Map<String, List<AccountDto>> result = new LinkedHashMap<>();

        ArrayList<String> accList = accountDao.accountListUno(this.uno);

        for (String accNo : accList) {
            List<AccountDto> txList = accountDao.accountListByAccountNo(accNo);
            txList.sort(Comparator.comparing(AccountDto::getT_date)); // 정렬
            result.put(accNo, txList);
        }

        return result;
    }

} // class e
