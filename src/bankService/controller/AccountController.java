package bankService.controller;

import bankService.model.dao.AccountDao;
import bankService.model.dto.AccountDto;

public class AccountController {

    // 싱글톤 생성
    private AccountController(){}
    private static final AccountController instance = new AccountController();
    public static AccountController getInstance(){
        return instance;
    }

    // AccountDao 싱글톤 가져오기
    public AccountDao accountDao = AccountDao.getInstance();


    // 계좌 중복 여부

    // 계좌 등록
    public boolean AccountAdd(String account_pwd){
        AccountDto accountDto = new AccountDto(account_pwd);
        boolean result = accountDao.AccountAdd(accountDto);
    }
    // 계좌 해지





}
