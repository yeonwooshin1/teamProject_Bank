package bankService.controller;

import bankService.model.dao.AccountDao;
import bankService.model.dto.*;

import java.util.ArrayList;

public class AccountController {
    // 입금 , 출금 , 이체

    // 싱글톤 생성
    private AccountController(){}
    private static final AccountController instance = new AccountController();
    public static AccountController getInstance(){
        return instance;
    }

    // AccountDao 싱글톤 가져오기
    private final AccountDao accountDao = AccountDao.getInstance();


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
        int uno = 1;
        AccountDto dto = new AccountDto();
        dto.setAccount_pwd(account_pwd);
        dto.setUno(uno);
        return accountDao.accountAdd(dto);
    }
    // 계좌 해지
    public boolean accountDel(int uno, String account_no, String account_pwd) {
        AccountDto dto = new AccountDto();
        dto.setUno(uno);  // 이 부분 추가!
        dto.setAccount_no(account_no);
        dto.setAccount_pwd(account_pwd);
        return accountDao.accountDel(dto);
    }


    // 계좌번호 중복 검증
    public boolean accountCheck(String account_no) {
        return accountDao.accountCheck(account_no);
    }

    // 계좌 조회
    // uno로 계좌번호 리스트 받기
    public ArrayList<String> accountListUno(int uno) {
        return accountDao.accountListUno(uno);
    }

    // 계좌번호로 거래내역 조회
    public ArrayList<AccountDto> accountList(String account_no) {
        return accountDao.accountList(account_no);
    }


} // class e
