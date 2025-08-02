package bankService.controller;

import bankService.model.dao.AccountDao;
import bankService.model.dto.TransactionDto;
import bankService.model.dto.TransactionResultDto;

public class AccountController {

    // 싱글톤 생성
    private AccountController(){}
    private static final AccountController instance = new AccountController();
    public static AccountController getInstance(){
        return instance;
    }

    // AccountDao 싱글톤 가져오기
    public AccountDao accountDao = AccountDao.getInstance();


    // 입금 메소드
    public TransactionResultDto deposit(TransactionDto dto){

        // 계좌 유효성 검사

        // 거래내역 저장

        // 잔액 계산

        // 성공/실패 여부

    }


    // 출금 메소드

    // 이체 메소드




}
