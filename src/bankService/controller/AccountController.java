package bankService.controller;

import bankService.model.dao.AccountDao;
import bankService.model.dao.ExistenceDao;

public class AccountController {
    // AccountDao , ExistenceDao  불러올  controller

    // 싱글톤 생성
    private AccountController(){}
    private static final AccountController instance = new AccountController();
    public static AccountController getInstance(){
        return instance;
    }

    // AccountDao , ExistenceDao 싱글톤 가져오기
    public AccountDao accountDao = AccountDao.getInstance();
    public ExistenceDao existenceDao = ExistenceDao.getInstance();


    //


}
