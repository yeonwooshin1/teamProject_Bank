package bankService.model.dao;

import bankService.model.dto.IdResponseDto;
import bankService.model.dto.UserDto;
import com.mysql.cj.jdbc.ConnectionGroup;

import java.sql.*;

public class UserDao { // class start

    // 드라이버 로드 (최초 1회만 실행됨)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 3드라이버 로드 실패: " + e.getMessage());
        }
    }

    // 싱글톤
    private UserDao(){}
    private static final UserDao instance = new UserDao();
    public static UserDao getInstance(){
        return instance;
    }


    // DB 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    UserDto dto = new UserDto();

    // 로그인 5회 실패시 유저 아이디에 있는 유저번호 가져오기
    public int getUno (String u_id) {
        String sql = "select uno from user where u_id = ?";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, u_id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int uno = rs.getInt( "uno");
                return uno;    // 유저값 주기
            }

        } catch ( Exception e) {
            System.out.println("SQLException 오류 발생 " + e.getMessage());
        }

        return -999999999;  // 없는 유저
    }

    // 로그인
    public int login( UserDto dto ) {



        String sql = "SELECT uno FROM user WHERE u_id = ? AND u_pwd = ?";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, dto.getU_id());
            preparedStatement.setString(2, dto.getU_pwd());
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int uno = rs.getInt( "uno");
                return uno;
            }

        } catch ( Exception e) {
            System.out.println("SQLException 오류 발생 " + e.getMessage());
        }

        return 0; // 로그인 실패

    }

    //----------------------------------------------------------------------------------------------------//

    // 회원가입
    public int registerMember(UserDto dto) {
        // 1) 아이디 중복 체크용 SQL
        String checkSql  = "SELECT COUNT(*) FROM `user` WHERE `u_id` = ?";
        // 2) 실제 INSERT용 SQL (u_email, u_date 포함)
        String insertSql = """
                INSERT INTO `user`(u_id, u_pwd, u_name, u_phone, u_email, u_date) VALUES(?,    ?,     ?,      ?,       ?,       ?)
                """;

        try (
                Connection conn       = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement chk = conn.prepareStatement(checkSql);
                PreparedStatement ins = conn.prepareStatement(insertSql);
        ) {
            // 중복 검사
            chk.setString(1, dto.getU_id());
            try (ResultSet rs = chk.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return -1;  // 이미 존재하는 아이디
                }
            }

            // 실제 INSERT
            ins.setString(1, dto.getU_id());
            ins.setString(2, dto.getU_pwd());
            ins.setString(3, dto.getU_name());
            ins.setString(4, dto.getU_phone());
            ins.setString(5, dto.getU_email());
            // dto.getU_date(): DTO에서 날짜(Date 타입 또는 String 타입) 값을 꺼냄
            // Date.valueOf(): String(yyyy-MM-dd) → java.sql.Date 객체로 변환
            ins.setDate(6, Date.valueOf(dto.getU_date()));


            // 영향 받은 row 수(1 이면 성공) 리턴
            return ins.executeUpdate();

        } catch (Exception e) {
            System.out.println( "SQL 오류 발생 " + e );
            return -3;  // DB 오류
        }
    }



    //----------------------------------------------------------------------------------------------------//

    // 아이디찾기
    // 아이디 찾기
    public IdResponseDto findId(String u_name, String u_phone) {
        String sql = "SELECT u_id FROM user WHERE u_name = ? AND u_phone = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u_name);
            ps.setString(2, u_phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new IdResponseDto(rs.getString("u_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println( "SQLException 오류 발생 " + e );
        }
        return null; // 못 찾으면 null 반환
    }

//----------------------------------------------------------------------------------------------------//

    // 비밀번호찾기1
    public int verifyAccount(String u_id, String u_email) {
        String sql = "SELECT COUNT(*) FROM user WHERE u_id = ? AND u_email = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, u_id);
            ps.setString(2, u_email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return 1; // 사용자 정보 확인됨
                }
            }
        } catch (SQLException e) {
            System.out.println( "SQLException 오류 발생" + e);
        }
        return 0; // 계정 없음
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호찾기2
    public int updatePassword(String u_id, String newPwd) {
        String sql = "UPDATE user SET u_pwd = ? WHERE u_id = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, newPwd);
            ps.setString(2, u_id);
            int updated = ps.executeUpdate();
            return (updated == 1) ? 1 : 0;  // 1: 변경 성공, 0: 변경 실패(아이디 없음 등)
        } catch (SQLException e) {
            System.out.println( "SQLException 오류 발생" + e);
            return 0; // DB오류도 실패로 처리
        }
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호 변경1
    public boolean verifyPassword(String u_id, String u_pwd) {
        String sql = "SELECT COUNT(*) FROM user WHERE u_id = ? AND u_pwd = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, u_id);
            ps.setString(2, u_pwd);
            try (ResultSet rs = ps.executeQuery()) {
                // rs.getInt(1): 첫 번째 컬럼의 값을 int로 가져옴
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println( "SQLException 오류 발생 " + e);
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호 변경2
    public boolean update2Password(String u_id, String new_pwd) {
        String sql = "UPDATE user SET u_pwd = ? WHERE u_id = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, new_pwd);
            ps.setString(2, u_id);
            int updated = ps.executeUpdate();
            return updated == 1; // true: 변경 성공, false: 실패(아이디 틀림 등)
        } catch (SQLException e) {
            System.out.println( "SQLException 오류 발생 " + e);
            return false; // 예외도 실패 처리
        }
    }

    //----------------------------------------------------------------------------------------------------//


    // 계정 탈퇴

    // 합쳐보니까 SQL 외래키 무결성 오류때문에 계정 탈퇴가 안됨
    // -> 부모 데이터를 지운다고 다 지워지는 게 아니고 자식 데이터부터 삭제해야함

    //[ user ]
    //  uno (PK)
    //  u_id (아이디, unique)
    //   ↓
    //[ account ]
    //  acno (PK)
    //  uno (FK, user PK 참조)
    //   ↓
    //[ transaction ]
    //  tno (PK)
    //  from_acno (FK, account PK 참조)
    //  to_acno (FK, account PK 참조)

    public boolean deleteAccount(String u_id, String u_pwd) {
        // 코드가 겹치니까 미리 위에다가 다 선언

        // 먼저 입력한 아이디로 uno를 찾고나서 uno에 맞는 acno 찾고 해당되면 삭제
//        String sql0 = "DELETE FROM transaction WHERE from_acno IN (SELECT acno FROM account WHERE uno = (SELECT uno FROM user WHERE u_id = ?)) OR to_acno IN (SELECT acno FROM account WHERE uno = (SELECT uno FROM user WHERE u_id = ?))";
//        String sql1 = "DELETE FROM account WHERE uno = (SELECT uno FROM user WHERE u_id = ?)";
        String sql2 = "DELETE FROM user WHERE u_id = ? AND u_pwd = ?";

        // try 여러 개에 catch 하나 가능 괄호 처리
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 거래내역 삭제
            // 쿼리문이 다르면 다 다르게 만들어야함
//            try (PreparedStatement ps0 = conn.prepareStatement(sql0)) {
//                ps0.setString(1, u_id);
//                ps0.setString(2, u_id);
//                ps0.executeUpdate();
//            }
            // 1) 아이디 + 비밀번호가 DB에서 실제로 매칭되는지 확인
            try (PreparedStatement test = conn.prepareStatement(
                    "SELECT COUNT(*) FROM user WHERE u_id=? AND u_pwd=?")) {

                test.setString(1, u_id);
                test.setString(2, u_pwd);          // 평문 저장이면 그대로, 해시 저장이면 해시값
                ResultSet rs = test.executeQuery();
                rs.next();
                System.out.println("[DEBUG] match rows = " + rs.getInt(1));  // 0 또는 1
            }

// 2) 실제 삭제 수행
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "DELETE FROM user WHERE u_id=? AND u_pwd=?")) {

                ps2.setString(1, u_id);
                ps2.setString(2, u_pwd);
                int deleted = ps2.executeUpdate();
                System.out.println("[DEBUG] deleted rows = " + deleted);     // 0 또는 1
                return deleted == 1;
            }
        } catch (Exception e) {
            System.out.println("SQL 오류 발생 " + e);
        }
        return false;
    }
}







































