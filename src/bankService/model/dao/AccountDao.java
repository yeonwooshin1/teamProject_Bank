package bankService.model.dao;

import java.sql.*;
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


    // 계좌 유효성 검사 (입금 , 출금 , 이체할 계좌 조회 시)
    public boolean isAccount(String account_no , String account_pwd ){
        try {
            String sql = "select * from account where account_no = ? and account_pwd = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ps.setString(2, account_pwd);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // 계좌 존재하면 true
        }catch (Exception e ){
            System.out.println("계좌 조회 실패" + e);
        }
        return false;
    } // func e

    // 비밀번호 없이 계좌번호 만으로 유효성 검사 (이체 받는 계좌 조회 시)
    public boolean receiveAccount(String account_no){
        try {
            String sql = "select * from account where account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (Exception e ){
            System.out.println(e);
        }
        return false;
    } // func e

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
            System.out.println("잔액 계산 실패" + e );
        }
        return balance;
    } // func e


    // 거래내역 저장 메소드
    public boolean saveTransaction(int from , int to , int amount  , String memo , String type  ){
        try {
            conn.setAutoCommit(false);  // 자동 커밋 끄는 명령어
            // 트랜잭션 시작! 쓰는 이유 : 여러 쿼리를 하나의 '트랜잭션' 으로 묶어서 쓰고 싶어서
            // 각 sql문은 실행 후 바로 자동 커밋 됨 , 트랜잭션 쓰면 자동 커밋 끄고 , 수동으로 제어 필요

            if("이체".equals(type)){

                // 출금 내역 저장 (from_acno -> 은행계좌)
                String withdrawSql = "insert into transaction (from_acno , to_acno , amount , type , memo , t_date )" +
                        "values ( ? , ? , ? , ? , ? , now())";
                try (PreparedStatement ps = conn.prepareStatement(withdrawSql)){

                    ps.setInt(1 , from);
                    ps.setInt(2 , 1001);    // 은행계좌
                    ps.setInt(3, amount);
                    ps.setString(4,"출금");
                    ps.setString(5, memo);
                    ps.executeUpdate();
                } // try e

                // 입금 내역 저장 (은행계좌 -> to_acno)
                String depositSql =  "insert into transaction (from_acno , to_acno , amount , type , memo , t_date )" +
                        "values ( ? , ? , ? , ? , ? , now())";
                try (PreparedStatement ps = conn.prepareStatement(depositSql)){

                    ps.setInt(1, 1001);
                    ps.setInt(2, to);
                    ps.setInt(3, amount);
                    ps.setString(4, "입금");
                    ps.setString(5,memo);
                    ps.executeUpdate();
                } // try e

            } // if e
            else {
                // 입금 or 출금
                String sql = "insert into transaction (from_acno , to_acno , amount , type , memo , t_date )" +
                        "values ( ? , ? , ? , ? , ? , now())";
                try (PreparedStatement ps = conn.prepareStatement(sql)){
                    ps.setInt(1, from);
                    ps.setInt(2, to);
                    ps.setInt(3, amount);
                    ps.setString(4, type);
                    ps.setString(5, memo);
                    ps.executeUpdate();
                } // try e
            } // else e
            conn.commit(); // 여러 쿼리 실행 후 , 모두 성공하면 커밋 시작하라는 명령어
            return true;

        }catch (Exception e ){
            System.out.println(e);
            try { conn.rollback(); // 커밋 실패시 원래 상태로 되돌리는 명령어
            }catch (SQLException ex){System.out.println(ex);}
            return false;
        } // catch e
        finally {
            try {conn.setAutoCommit(true); // 트랜잭션 처리 끝나고 다시 자동 커밋 모드로 돌려놓는 명령어
            }catch (SQLException e){System.out.println(e);}
        } // finally e
    } // func e

    // account_no 로 acno(계좌 로그번호) 가져오기
    public int getAcnoByAccountNo(String account_no){
        try {

            String sql = "select acno from account where account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString( 1 , account_no ); // 매개변수 받은 account_no 열로 있는 행 가져올 준비
            ResultSet rs = ps.executeQuery();
            if(rs.next() ) {                            // 행 하나씩 조회하면서 이동
                return rs.getInt("acno");    // 있으면 행의 acno 값 int로 가져와서 반환
            }

        }catch (Exception e){
            System.out.println("계좌 로그번호 조회 실패 :"+ account_no +e);
        }
        return -1; // 조회 실패시 -1반환
    } // func e

    // 계좌

} // class e
