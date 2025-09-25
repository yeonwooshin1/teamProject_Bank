package bankService.controller;

import bankService.model.dao.AccountDao;
import bankService.model.dao.UserDao;
import bankService.model.dto.IdResponseDto;
import bankService.model.dto.UserDto;
import bankService.util.EmailValidationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserController2 { // class start

    // 싱글톤
    private UserController2(){}
    private static final UserController2 instance = new UserController2();
    public static UserController2 getInstance(){
        return instance;
    }

    // 싱글톤 DAO 가져오기
    private final UserDao userDao = UserDao.getInstance();
    private final AccountDao accountDao = AccountDao.getInstance();

    // 로그인 후 세션용 멤버
    private int uno;

    // 로그인 성공 후 세션 저장
    public void wire(int uno){
        this.uno = uno; // 현재 로그인한 회원 번호 저장
    }

    public int getUno() {
        return this.uno;
    }

    // 실패시 count 해주는 map
    private final Map<String, Integer> loginFailMap = new HashMap<>();

    // 5회 실패 인증시 loginFailMap 값 초기화
    public void resetLoginFailMap(String u_id) {
        loginFailMap.put(u_id , 0);
    }

    // 로그인
    public int login(String u_id, String u_pwd) {
        if (loginFailMap.getOrDefault(u_id, 0) >= 5) {
            return -(userDao.getUno(u_id));
        }

        UserDto dto = new UserDto(u_id, u_pwd);
        int result = userDao.login(dto);

        if (result > 0) {
            loginFailMap.put(u_id, 0); // 실패횟수 초기화
            this.uno = result;         // 로그인 세션 저장
            return result;
        } else{
            int failCount = loginFailMap.getOrDefault(u_id, 0) + 1;
            loginFailMap.put(u_id, failCount);
            System.out.println("실패횟수 : " + failCount);
            return 0;
        }
    }

    // 회원가입
    public int registerMember(String u_id, String u_pwd1, String u_pwd2,
                              String u_name, String u_phone, String u_email, String u_date) {
        if (!u_pwd1.equals(u_pwd2)) return -2;

        String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        if (!u_pwd1.matches(pwPattern)) {
            return -5;
        }

        ArrayList<String> hostList = new ArrayList<>();
        hostList.add("gmail.com");
        hostList.add("naver.com");
        hostList.add("daum.net");
        hostList.add("kakao.com");

        String email = u_email.split("@")[1];
        if(!hostList.contains(email)) return -4;

        LocalDate birth;
        try {
            birth = LocalDate.parse(u_date);
        } catch (DateTimeParseException e) {
            System.out.println("생년월일 형식 오류입니다");
            return -3;
        }

        UserDto dto = new UserDto(u_id, u_pwd1, u_name, u_phone, u_email, birth);
        return userDao.registerMember(dto);
    }

    // 아이디 찾기
    public IdResponseDto findId(String u_name, String u_phone) {
        return userDao.findId(u_name, u_phone);
    }

    // 비밀번호 찾기1
    public int verifyAccount(String u_id, String u_email) {
        boolean result = EmailValidationUtil.isSimpleEmailFormat(u_email);
        if(!result) return -1;
        return userDao.verifyAccount(u_id, u_email);
    }

    // 비밀번호 찾기2
    public int updatePassword(String u_id, String newPwd) {
        return userDao.updatePassword(u_id, newPwd);
    }

    // 비밀번호 변경1
    public boolean verifyPassword(String u_id, String u_pwd) {
        return userDao.verifyPassword(u_id, u_pwd);
    }

    // 비밀번호 변경2
    public boolean update2Password(String u_id, String new_pwd) {
        String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        if (!new_pwd.matches(pwPattern)) {
            return false;
        }
        return userDao.update2Password(u_id, new_pwd);
    }

    // 계정 탈퇴
    public boolean deleteAccount(String u_id, String u_pwd) {
        return userDao.deleteAccount(u_id, u_pwd);
    }

} // class end
