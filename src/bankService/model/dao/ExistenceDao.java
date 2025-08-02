package bankService.model.dao;

public class ExistenceDao {
    // 계좌 정보 확인 , 잔액 조회 담당 dao

    // 싱글톤 생성
    private ExistenceDao(){}
    private static final ExistenceDao instance = new ExistenceDao();
    public static ExistenceDao getInstance(){
        return instance;
    }



    // 계좌 조회 메소드

    // 잔액 조회 메소드



}
