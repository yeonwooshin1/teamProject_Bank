package bankService.model.dao;

import bankService.model.dto.AccountDto;
import bankService.util.AccountUtil;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class AccountDao {
    // 계좌 유효성 검사 , 잔액 계산  , 거래내역 저장 , 계좌 번호로 계좌 로그번호 가져오는 dao

    // 싱글톤 생성
    public AccountDao(){
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

    // =========================== 이겨레 입금 , 출금 , 이체 ====================== //


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
            rs.close();
            ps.close();

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




    // ====================== 지훈씨 계좌 관리 =================================== //

    // 계좌번호 중복 검증
    public boolean accountCheck(String account_no) {
        try {
            String sql = "SELECT COUNT(*) FROM account WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;  // 0이면 가능, 1 이상이면 중복
            }

        } catch (Exception e) {
            System.out.println("계좌 중복 검증 실패: " + e);
        }
        return false; // 중간에 문제 생기면 false
    }


    // 계좌 생성
    public boolean accountAdd(AccountDto accountDto) {
        try {
            String account_no = AccountUtil.generateAccountNumber();

            // 중복되지 않는 계좌번호 생성
            while (!accountCheck(account_no)) {  // 중복이면 다시 생성
                account_no = AccountUtil.generateAccountNumber();
            }   // while end

            String sql = "INSERT INTO account ( uno , account_no, account_pwd) VALUES (? , ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountDto.getUno());
            ps.setString(2, account_no);
            ps.setString(3, accountDto.getAccount_pwd());

            int result = ps.executeUpdate();
            if (result == 1) {
                System.out.println("신규 계좌번호는 '"+account_no+"' 입니다.");
                return true;
            } else {
                return false;
            }   // if end

        } catch (Exception e) {
            System.out.println("계좌 등록 실패: " + e);
            return false;
        }   // try catch end
    }   // func end

    // 계좌 존재 여부 및 uno 조회
    public Integer getUnoByAccount(String account_no, String account_pwd) {
        try {
            String sql = "SELECT uno FROM account WHERE account_no = ? AND account_pwd = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ps.setString(2, account_pwd);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("uno");
            }   // if end
        } catch (Exception e) {
            System.out.println("uno 조회 실패: " + e);
        }   // try catch end
        return null;
    }   // func end

    // 해지할 계좌 번호 일치 x
    public boolean accountnoexists(String account_no) {
        try{
            String sql = "select 1 from account where account_no = ? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account_no);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (Exception e){
            System.out.println("존재하지않음"+e);
            return false;
        }   // try catch end

    }   // func end


    // 계좌 해지
    public boolean accountDel(String account_no, String account_pwd, int uno) { // 계좌번호 , 패스워드
        try {
            conn.setAutoCommit(false);

            // 1. 계좌번호에 대응하는 acno 조회
            String selectAcnoSql = "SELECT acno FROM account WHERE account_no = ? AND account_pwd = ? AND uno = ?";
            PreparedStatement ps1 = conn.prepareStatement(selectAcnoSql);
            ps1.setString(1, account_no);
            ps1.setString(2, account_pwd);
            ps1.setInt(3, uno);
            ResultSet rs = ps1.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }   // if end
            int acno = rs.getInt("acno");

            // 2. transaction 테이블에서 거래내역 삭제
            String delTxSql = "DELETE FROM transaction WHERE from_acno = ? OR to_acno = ?";
            PreparedStatement ps2 = conn.prepareStatement(delTxSql);
            ps2.setInt(1, acno);
            ps2.setInt(2, acno);
            ps2.executeUpdate();

            // 3. account 테이블에서 계좌 삭제
            String delAccountSql = "DELETE FROM account WHERE acno = ?";
            PreparedStatement ps3 = conn.prepareStatement(delAccountSql);
            ps3.setInt(1, acno);
            int result = ps3.executeUpdate();

            if (result == 1) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }   // if end

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ex) {}
            System.out.println("계좌 해지 실패: " + e);
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception ex) {}
        }   // try end
    }   // func end




    // 거래 내역 조회
    // uno 기준으로 해당 사용자의 계좌번호 목록 반환
    public ArrayList<String> accountListUno(int uno) {
        ArrayList<String> list = new ArrayList<>();

        try {
            String sql = "SELECT account_no FROM account WHERE uno = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, uno);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("account_no"));
            }   // while end
        } catch (Exception e) {
            System.out.println("계좌 목록 조회 실패: " + e.getMessage());
        }   // try end

        return list;
    }   // func end

    // 계좌번호로 거래내역 조회
    public ArrayList<AccountDto> accountListByAccountNo(String accountNo) {
        ArrayList<AccountDto> list = new ArrayList<>();

        String sql = """
            SELECT t.tno, a.account_no, t.from_acno, t.to_acno, 
                   t.type, t.amount, t.memo, t.t_date 
              FROM transaction t
              JOIN account a ON t.from_acno = a.acno OR t.to_acno = a.acno
             WHERE a.account_no = ?
             ORDER BY t.t_date ASC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AccountDto dto = new AccountDto(
                        rs.getInt("tno"),
                        rs.getString("account_no"),
                        rs.getInt("from_acno"),
                        rs.getInt("to_acno"),
                        rs.getString("type"),
                        rs.getInt("amount"),
                        rs.getString("memo"),
                        rs.getString("t_date")
                );
                list.add(dto);
            }   // while end
        } catch (Exception e) {
            System.out.println("거래 내역 조회 실패: " + e.getMessage());
        }   // try end

        return list;
    }   // func end

} // class e
