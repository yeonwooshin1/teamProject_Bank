package bankService.controller;

import bankService.model.dao.AccountDao;

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

    // 출금 메소드

    // 이체 메소드




}
