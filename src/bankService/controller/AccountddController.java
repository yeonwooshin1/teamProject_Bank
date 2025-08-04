package bankService.controller;

import bankService.model.dao.AccountddDao;
import bankService.model.dto.AccountDto;

public class AccountddController {

    // 싱글톤 생성
    private AccountddController(){}
    private static final AccountddController instance = new AccountddController();
    public static AccountddController getInstance(){
        return instance;
    }

    // AccountDao 싱글톤 가져오기
    public AccountddDao accountDao = AccountddDao.getInstance();


    // 계좌 중복 여부

    // 계좌 등록
    public boolean AccountAdd(String account_pwd){
        AccountDto dto = new AccountDto();
        dto.setAccount_pwd(account_pwd);
        dto.setUno(uno);
        return accountDao.AccountAdd(dto);
    }
    // 계좌 해지
    public boolean AccountDel(String account_no, String account_pwd){
        AccountDto dto = new AccountDto();
        dto.setAccount_no(account_no);
        dto.setAccount_pwd(account_pwd);
        return accountDao.AccountDel(dto);
    }


/*
    // 계좌번호 중복 검증
    public boolean AccountCheck(){

    }
    // 계좌 생성

    // 계좌 해지

    // 계좌 조회
*/

}
