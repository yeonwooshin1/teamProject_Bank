package bankService.model.dao;  // package


import java.sql.*;

public class OtpDao {  //
    // 싱글톤
    private OtpDao() {
        connectDB();
    }

    private static final OtpDao instance = new OtpDao();

    public static OtpDao getInstance() {
        return instance;
    }

    // DB 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    // DB 연동 메소드 Class.forName("com.mysql.cj.jdbc.Driver");
    private void connectDB() {
        try {
            // mysql 를 지정한 Driver 클래스 가져오기
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("[실패] 2mysql 드라이버 연동 실패");
        } // 클래스 있는지 잘 연결됐는지 예외
    }   // func end

    // Connection 연결 메소드
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }   // func end


    public String findEmail(int uno) {
        String sql = "SELECT u_email FROM user WHERE uno = ? ";
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, uno);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("u_email");
            }
        } catch (SQLException e) {
            System.out.println("SQLException 오류 발생: " + e.getMessage());
        }

        return null; // 못 찾으면 null 반환
    }
}