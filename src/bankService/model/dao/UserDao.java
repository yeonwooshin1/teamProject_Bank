package bankService.model.dao;

import bankService.model.dto.IdResponseDto;
import bankService.model.dto.UserDto;

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
            // ─── 1) 중복 검사 ───────────────────────────────────────────────────────
            chk.setString(1, dto.getU_id());
            try (ResultSet rs = chk.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return -1;  // 이미 존재하는 아이디
                }
            }

            // ─── 2) 실제 INSERT ────────────────────────────────────────────────────
            ins.setString(1, dto.getU_id());
            ins.setString(2, dto.getU_pwd());
            ins.setString(3, dto.getU_name());
            ins.setString(4, dto.getU_phone());
            ins.setString(5, dto.getU_email());
            // java.time.LocalDate → java.sql.Date 로 변환
            ins.setDate(6, Date.valueOf(dto.getU_date()));


            // 영향 받은 row 수(1 이면 성공) 리턴
            return ins.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
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
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println( "SQLException 오류 발생" + e);
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
            System.out.println( "SQLException 오류 발생" + e);
            return false; // 예외도 실패 처리
        }
    }

    //----------------------------------------------------------------------------------------------------//


    // 계정 탈퇴
    public boolean deleteAccount(String u_id, String u_pwd) {
        String sql = "DELETE FROM user WHERE u_id = ? AND u_pwd = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, u_id);
            ps.setString(2, u_pwd);
            int deleted = ps.executeUpdate();
            return deleted == 1; // 1명 삭제됐으면 true
        } catch (SQLException e) {
            System.out.println( "계좌 잔액 있음");
            return false; // 예외 시 실패
        }
    }

    // ghoon1210@gmail.com
}







































