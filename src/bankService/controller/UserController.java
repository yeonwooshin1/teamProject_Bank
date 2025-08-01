package bankService.controller;

import bankService.model.dao.UserDao;
import bankService.model.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

public class UserController { // class start

    // 싱글톤
    private UserController(){}
    private static final UserController instance = new UserController();
    public static UserController getInstance(){
        return instance;
    }

    // UserDao 싱글톤 가져오기
    private UserDao userDao = UserDao.getInstance();


    // 로그인 실패 횟수 저장용 Map
    private static Map< String, Integer > loginFailMap = new HashMap<>();

    // 로그인
    public int login( String u_id , String u_pwd ){
        if ( loginFailMap.getOrDefault ( u_id, 0 ) >= 5 ) {
            return -1; // 로그인 차단
        }

        UserDto dto = new UserDto( u_id, u_pwd );

        int result = userDao.login( dto );

        if ( result == 1 ) {
            loginFailMap.put( u_id, 0 ); // 실패횟수 초기화
            return dto.getUno();// 로그인 성공


        } else {
            int failCount = loginFailMap.getOrDefault( u_id, 0 ) + 1;
            loginFailMap.put( u_id, failCount );
            return failCount; // 실패 횟수 반환
        }
    }





} // class end
