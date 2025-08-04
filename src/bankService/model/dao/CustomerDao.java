package bankService.model.dao;

import java.sql.*;
import java.util.HashMap;

public class CustomerDao { // class start

    // DB 연동 메소드 Class.forName("com.mysql.cj.jdbc.Driver");
    private void connectDB () {
        try{
            // mysql 를 지정한 Driver 클래스 가져오기
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch ( ClassNotFoundException e ) { System.out.println("[실패] 1mysql 드라이버 연동 실패"); } // 클래스 있는지 잘 연결됐는지 예외
    }   // func end

    // DB 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";


    // 로그인 함수
    public HashMap< String , Object > login( String u_id , String u_pwd ) throws SQLException {

        HashMap< String, Object > result = new HashMap<>();

        String sql = "SELECT * FROM customer WHERE u_id = ? AND u_pwd = ?";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            // ? 자리에 u_id , u_pwd 값 입력
            preparedStatement.setString( 1 , u_id );
            preparedStatement.setString( 2 , u_pwd );

            // ResultSet : SQL 쿼리 결과를 담고 있는 객체
            ResultSet rs = preparedStatement.executeQuery();

            // rs.next : 다음 행(row)이 존재하면 true, 없으면 false를 반환
            if( rs.next() ){
                // result.put("컬럼명", rs.get자료형("컬럼명"))
                // DB에서 불러온 각 컬럼 값을 HashMap에 저장
                result.put("uno", rs.getInt("uno"));
                result.put("u_id", rs.getString("u_id"));
                result.put("u_pwd", rs.getString("u_pwd"));
                result.put("u_name", rs.getString("u_name"));
                result.put("phone", rs.getString("phone"));
                result.put("u_email", rs.getString("u_email"));
                result.put("u_date", rs.getDate("u_date"));


            } // if end

            rs.close();
            preparedStatement.close();
            conn.close();

            return result;

            // 안전하게 닫기
            // 닫지 않으면 메모리 누수, 커넥션 부족, 서버 부하 등의 문제가 발생



        }catch ( SQLException e ){
            System.out.println( "SQLException 예외 오류 발생" );
        }


        return result;
    }
} // class end









