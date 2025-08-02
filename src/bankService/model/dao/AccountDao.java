package bankService.model.dao;

public class AccountDao {
    // 입금 , 출금 , 이체 , 거래내역 저장 담당 dao

    // 싱글톤 생성
    private AccountDao(){}
    private static final AccountDao instance = new AccountDao();
    public static AccountDao getInstance(){
        return instance;
    }



    // 입금 메소드

    // 출금 메소드

    // 이체 메소드

    // 거래내역 저장 메소드

}
