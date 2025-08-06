package bankService.controller;

import bankService.model.dao.UserDao;
import bankService.model.dto.IdResponseDto;
import bankService.model.dto.UserDto;
import bankService.service.OtpService;
import bankService.util.EmailValidationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserController { // class start

    // 싱글톤
    private UserController(){}
    private static final UserController instance = new UserController();
    public static UserController getInstance(){
        return instance;
    }


    //싱글톤 가져오기
    private final UserDao userDao = UserDao.getInstance();

    // wire 멤버변수
    OtpService otpService;
    int uno;

    // wire 세션 연결
    public void wire (int uno, OtpService otp){
        this.uno = uno;
        this.otpService = otp;
    }
    // 실패시 count 해주는 map
    Map<String, Integer> loginFailMap = new HashMap<>();

    // 로그인
    public int login(String u_id, String u_pwd) {

        //loginFailMap에서 해당 ID의 실패 횟수를 가져옴
        //만약 기록이 없다면 0 반환
        if (loginFailMap.getOrDefault(u_id, 0) >= 5) {
            return -1; // 로그인 차단
        }

        UserDto dto = new UserDto(u_id, u_pwd);

        int result = userDao.login( dto );

        if ( result > 0 ) { // 0이면 실패니까
            loginFailMap.put(u_id, 0);     // 실패횟수 초기화
            return result;                 // dto.getUno() 대신 result 반환!
        } else{
            int failCount = loginFailMap.getOrDefault(u_id, 0) + 1;
            loginFailMap.put(u_id, failCount);
            System.out.println( "실패횟수 : " + failCount );
            return 0;              // 실패 횟수 반환
        }
    }


    // 회원가입
    // Controller에서 비밀번호 비교 후 호출
    public int registerMember(String u_id, String u_pwd1, String u_pwd2,
                              String u_name, String u_phone, String u_email, String u_date) {
        if (!u_pwd1.equals(u_pwd2)) return -2;


        // 비밀번호에 영어,숫자,특수문자가 다 포함되게 하려면 '정규표현식'을 사용해야함
        //(?=.*[A-Za-z]) : 영어 1개 이상 포함
        //(?=.*\\d) : 숫자 1개 이상 포함
        //(?=.*[!@#$%^&*...]) : 특수문자 1개 이상 포함

        //. : 아무 문자 하나
        //* : 0개 이상 반복
        //+ : 1개 이상 반복
        //[A-Za-z] : 영어 대소문자 하나
        //\\d : 숫자
        //^ : 문자열의 시작
        //$ : 문자열의 끝
        //(?=...) : "반드시 ...을 포함해야 한다"는 조건

        // 예를 들어 ^[0-9]{3}$ 이렇게 되어있으면 0-9까지 숫자만으로 이루어진 3자리 이런 느낌

        //정규표현식  = 특정 패턴이 포함되는지 체크하는 식
        String pwPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        if (!u_pwd1.matches(pwPattern)) {
            return -5;
        }

        // 리스트 만들어서
        ArrayList<String> hostList = new ArrayList<>();
        hostList.add("gmail.com");
        hostList.add("naver.com");
        hostList.add("daum.net");
        hostList.add("kakao.com");

        String email = u_email.split("@")[1];
        // indexOf와 contains 차이
        // indexOf : 어떤 문자열(String)이나 리스트(List)에서 특정 요소가 어떤 위치(인덱스)에 존재하는지
        // contains : 존재유무만 알고 싶을 때
        if( ! hostList.contains(email) ) return -4;



        // ★ String → LocalDate 변환

        LocalDate birth;
        try {
            birth = LocalDate.parse(u_date); // "2025-07-31" 같은 형식
        } catch (DateTimeParseException e) {
            System.out.println("생년월일 형식 오류입니다");
            return -3;
        }

        UserDto dto = new UserDto(u_id, u_pwd1, u_name, u_phone, u_email, birth); // ← birth

        int result = userDao.registerMember(dto);

        return result; // 1: 성공, -1: 중복, -3: DB오류
    }


    // MemberController.java
    // 싱글톤 DAO 가져오기
    private final UserDao dao = UserDao.getInstance();


    //----------------------------------------------------------------------------------------------------//

    // Controller는 DAO만 호출
    public IdResponseDto findId(String u_name, String u_phone) {
        return dao.findId(u_name, u_phone);
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호 찾기1
    public int verifyAccount(String u_id, String u_email) {
        // 이메일 유효성 검사
        boolean result = EmailValidationUtil.isSimpleEmailFormat(u_email);
        if(!result) return -1;
        return userDao.verifyAccount(u_id, u_email);
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호 찾기2
    public int updatePassword(String u_id, String newPwd) {
        return userDao.updatePassword(u_id, newPwd);
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호 변경1
    public boolean verifyPassword(String u_id, String u_pwd) {
        return userDao.verifyPassword(u_id, u_pwd);
    }

    //----------------------------------------------------------------------------------------------------//

    // 비밀번호 변경2
    public boolean update2Password(String u_id, String new_pwd) {
        return userDao.update2Password(u_id, new_pwd);
    }

    //----------------------------------------------------------------------------------------------------//


    // 계정 탈퇴
    public boolean deleteAccount(String u_id, String u_pwd) {
        return userDao.deleteAccount(u_id, u_pwd);
    }

} // class end
