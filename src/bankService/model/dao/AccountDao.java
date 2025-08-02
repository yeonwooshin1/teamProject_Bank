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


    // 계좌 조회 메소드


    // 잔액 계산 메소드
    public int isBalance(int acno){
        int balance = 0;    // 잔액 초기화

        try {
            String sql = "select from_acno , to_acno , amount from transaction where from_acno = ? or to_acno = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1 , acno);
            ps.setInt(2, acno);
            ResultSet rs = ps.executeQuery();

            while (rs.next() ){
                int from = rs.getInt("from_acno");
                int to = rs.getInt("to_acno");
                int amount = rs.getInt("amount");

                if( acno == to )balance += amount;   // 조회하는 계좌로그번호가 to_acno에 있으면 금액만큼 누적 더하기
                if( acno == from )balance -= amount; // 조회하는 계좌로그번호가 from_acno에 있으면 금액만큼 누적 빼기
            }

        }catch (Exception e){
            System.out.println("잔액 조회 실패" + e );
        }
        return balance;
    } // func e


    // 입금 메소드
    public boolean deposit(){

        return true;
    }

    // 출금 메소드

    // 이체 메소드

    // 거래내역 저장 메소드
    public boolean saveTransaction(int from , int to , int amount  , String memo , String type  ){
        try {

            String sql = "insert into transaction (from_acno , to_acno , amount , type , memo , t_date )" +
                    "values ( ? , ? , ? , ? . ? now())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, from);
            ps.setInt(2, to);
            ps.setInt(3, amount);
            ps.setString(4, memo);
            ps.setString(5, type);
            ps.executeUpdate();
            return true;
        }catch (Exception e ){
            System.out.println("테이블 저장 실패 " +e);
            return false;
        }
    } // func e



} // class e
