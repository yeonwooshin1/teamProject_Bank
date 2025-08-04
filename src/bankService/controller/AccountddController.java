package bankService.controller;

import bankService.model.dao.AccountddDao;
import bankService.model.dto.AccountDto;

import java.util.ArrayList;

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
        int uno = 1;
        AccountDto dto = new AccountDto();
        dto.setAccount_pwd(account_pwd);
        dto.setUno(uno);
        return accountDao.AccountAdd(dto);
    }
    // 계좌 해지
    public boolean AccountDel(int uno, String account_no, String account_pwd) {
        AccountDto dto = new AccountDto();
        dto.setUno(uno);  // 이 부분 추가!
        dto.setAccount_no(account_no);
        dto.setAccount_pwd(account_pwd);
        return accountDao.AccountDel(dto);
    }


    // 계좌번호 중복 검증
    public boolean AccountCheck(String account_no) {
        return accountDao.AccountCheck(account_no);
    }

    // 계좌 조회
    // uno로 계좌번호 리스트 받기
    public ArrayList<String> AccountListUno(int uno) {
        return accountDao.AccountListUno(uno);
    }

    // 계좌번호로 거래내역 조회
    public ArrayList<AccountDto> AccountList(String account_no) {
        return accountDao.AccountList(account_no);
    }

}
