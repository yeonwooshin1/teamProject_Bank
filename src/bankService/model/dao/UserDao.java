package bankService.model.dao;

import bankService.controller.UserController;
import bankService.model.dto.IdResponseDto;
import bankService.model.dto.UserDto;

import java.sql.*;

public class UserDao { // class start

    // 싱글톤
    private UserDao(){}
    private static final UserDao instance = new UserDao();
    public static UserDao getInstance(){
        return instance;
    }

    // UserDao 싱글톤 가져오기
    private UserController userController = UserController.getInstance();


    // DB 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";


    // 로그인
    public int login( UserDto dto) {
        String sql = "SELECT COUNT(*) FROM user WHERE u_id = ? AND u_pwd = ?";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement preparedStatement = conn.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, dto.getU_id());
            preparedStatement.setString(2, dto.getU_pwd());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return dto.getUno(); // 로그인 성공
            }
        } catch ( SQLException e ) {
            System.out.println( "SQLException 오류 발생" );
        }

        return 0; // 로그인 실패
    }

    //----------------------------------------------------------------------------------------------------//

    // 회원가입
    public int registerMember(UserDto dto) {


        try {

            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String checkSql = "SELECT COUNT(*) FROM user WHERE u_id = ?";
            String insertSql = "INSERT INTO user (u_id, u_pwd, u_name, phone, u_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement check = conn.prepareStatement( checkSql );
            PreparedStatement insert = conn.prepareStatement( insertSql );

            // 1. 아이디 중복 검사
            check.setString(1, dto.getU_id());
            ResultSet resultSet = check.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return -1; // 중복
            }

            // 2. 회원가입 진행
            insert.setString(1, dto.getU_id());
            insert.setString(2, dto.getU_pwd());
            insert.setString(3, dto.getU_name());
            insert.setString(4, dto.getU_phone());
            insert.setString(5, String.valueOf(dto.getU_date()));


            // 영향을 받은(변경된) 행(row)의 개수를 int로 반환
            return insert.executeUpdate(); // 성공 시 1 반환

        } catch ( SQLException e ) {
            System.out.println( "SQLException 오류 발생" );
        }

        return -3; // DB 오류
    }



    //----------------------------------------------------------------------------------------------------//

    // 아이디찾기
    public IdResponseDto findId(String u_name, String phone) {
        String sql = "SELECT u_id FROM user WHERE u_name = ? AND phone = ?";
        try{
            Connection conn = DriverManager.getConnection( DB_URL , DB_USER , DB_PASSWORD );
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, u_name);
            preparedStatement.setString(2, phone);

            ResultSet resultSet = preparedStatement.executeQuery();
            if ( resultSet.next() ) {
                return new IdResponseDto( resultSet.getString("u_id" ) );
            }
        } catch ( SQLException e ) {

            System.out.println( "SQLException 오류 발생" );
        }
        // 아이디가 없다면
        return null;
    }













} // class end

























