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
    public int login(String u_id, String u_pwd) {
        if (loginFailMap.getOrDefault(u_id, 0) >= 5) {
            return -1; // 로그인 차단
        }

        UserDto dto = new UserDto(u_id, u_pwd);

        int result = userDao.login(dto);

        if (result == 1) {
            loginFailMap.put(u_id, 0); // 실패횟수 초기화
            return dto.getUno();// 로그인 성공


        } else {
            int failCount = loginFailMap.getOrDefault(u_id, 0) + 1;
            loginFailMap.put(u_id, failCount);
            return failCount; // 실패 횟수 반환
        }
    }


    // 회원가입
    // Controller에서 비밀번호 비교 후 호출
    public int registerMember(String u_id, String u_pwd1, String u_pwd2, String u_name, String phone, String u_date) {
        // 1. 비밀번호 일치 확인
        if ( !u_pwd1.equals(u_pwd2) ) return -2;

        // 4. DTO 생성
        UserDto dto = new UserDto(u_id, u_pwd1, u_name, phone, u_date);

        // 5. DAO에게 저장 요청
        int result = userDao.registerMember( dto );

        return result == 1 ? 1 : -3; // 1이면 성공, 아니면 형식 오류
    }





} // class end
