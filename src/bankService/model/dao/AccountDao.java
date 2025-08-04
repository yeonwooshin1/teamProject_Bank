package bankService.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountDao {
    // 입금 , 출금 , 이체 , 거래내역 저장 담당 dao

    // 싱글톤 생성
    private AccountDao(){
        connect();
    }
    private static final AccountDao instance = new AccountDao();
    public static AccountDao getInstance(){
        return instance;
    }

    // DB 연동
    private String db_url = "jdbc:mysql://localhost:3306/bank";
    private String db_user = "root";
    private String db_password = "1234";
    private Connection conn;


    // DB 연동 함수
    private void connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(db_url , db_user ,db_password);
        }catch (Exception e){
            System.out.println(e);
        }
    } // func e

    // 계좌번호 중복 검증
    public boolean AccountCheck(String account_no) {

            String sql = "SELECT COUNT(*) FROM account WHERE account_no = ?";
            ps.setString(1, account_no);
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;  // 0이면 사용가능, 1 이상이면 중복됨
        } catch (Exception e) {
            System.out.println("계좌 중복 검증 실패: " + e);
        return false; // 중간에 문제 생기면 false
    }


    // 계좌 생성
    public boolean AccountAdd(String account_pwd , int uno) {
        try {
            String account_no = AccountUtil.generateAccountNumber();
            // 중복되지 않는 계좌번호 생성
            while (!AccountCheck(account_no)) {  // 중복이면 다시 생성
                account_no = AccountUtil.generateAccountNumber();
            }
            String sql = "INSERT INTO account (uno, account_no, account_pwd) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, uno);
            ps.setString(2, account_no);
            ps.setString(3, account_pwd);
            int result = ps.executeUpdate();
            if (result == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("계좌 등록 실패: " + e);
            return false;
        }
    }
    // 계좌 해지
    public boolean AccountDel(String account_no, String account_pwd) { // 계좌번호 , 패스워드
    } // func e



} // class e
