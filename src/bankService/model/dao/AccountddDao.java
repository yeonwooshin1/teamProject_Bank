package bankService.model.dao;

import bankService.model.dto.AccountDto;
import bankService.util.AccountUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class AccountddDao {
    // 입금 , 출금 , 이체 , 거래내역 저장 담당 dao

    // 싱글톤 생성
    private AccountddDao(){
        connect();
    }
    private static final AccountddDao instance = new AccountddDao();
    public static AccountddDao getInstance(){
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
            System.out.println("mysql 드라이버 연결 성공");
            conn = DriverManager.getConnection(db_url , db_user ,db_password);
            System.out.println("db연결 성공");
        }catch (Exception e){
            System.out.println("db연결 실패"+ e);
        }
    } // func e

    // 계좌번호 중복 검증
    public boolean AccountCheck(String account_no) {
        try {
            String sql = "SELECT COUNT(*) FROM account WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;  // 0이면 사용가능, 1 이상이면 중복됨
            }

        } catch (Exception e) {
            System.out.println("계좌 중복 검증 실패: " + e);
        }
        return false; // 중간에 문제 생기면 false
    }


    // 계좌 생성
    public boolean AccountAdd(AccountDto accountDto) {
        try {
            String account_no = AccountUtil.generateAccountNumber();

            // 중복되지 않는 계좌번호 생성
            while (!AccountCheck(account_no)) {  // 중복이면 다시 생성
                account_no = AccountUtil.generateAccountNumber();
            }

            String sql = "INSERT INTO account ( uno , account_no, account_pwd) VALUES (? , ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountDto.getUno());
            ps.setString(2, account_no);
            ps.setString(3, accountDto.getAccount_pwd());

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
    public boolean AccountDel(AccountDto dto) { // 계좌번호 , 패스워드
        try {
            String sql = "DELETE FROM account WHERE account_no = ? AND account_pwd = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ps.setString(2, account_pwd);

            int result = ps.executeUpdate();
            if (result == 1) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            System.out.println("계좌 해지 실패: " + e);
            return false;
        }
    }

/*
    // 계좌 조회 메소드
    // 계좌 조회
    public ArrayList<AccountDto> AccountList(String account_no) {
        ArrayList<AccountDto> list = new ArrayList<>();

        try {
            // 거래 내역 조회 (특정 계좌 기준, 최신순)
            String sql = "SELECT t.tno, a.account_no, t.from_acno, t.to_acno, t.type, t.amount, t.memo, t.t_date " +
                    "FROM transaction t " +
                    "JOIN account a ON t.from_acno = a.acno OR t.to_acno = a.acno " +
                    "WHERE a.account_no = ? " +
                    "ORDER BY t.t_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AccountDto dto = new AccountDto(
                        rs.getInt("tno"),              // 거래 번호
                        rs.getString("account_no"),    // 계좌번호
                        rs.getInt("from_acno"),        // 출금 계좌
                        rs.getInt("to_acno"),          // 입금 계좌
                        rs.getString("type"),          // 거래 유형
                        rs.getInt("amount"),           // 거래 금액
                        rs.getString("memo"),          // 메모
                        rs.getString("t_date")         // 거래 일자
                );

                list.add(dto); // 리스트 추가
            }

        } catch (Exception e) {
            System.out.println("거래내역 조회 실패: " + e);
        }

        return list;
    }

    public boolean AccountAdd(AccountDto accountDto) {
        return false;
    }

 */
} // class e
