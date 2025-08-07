package bankService.model.dto;

import java.time.LocalDate;

//UserDto
//DB에서 가져오거나, DB에 저장할 “회원 정보”를 담는 데이터 객체
//오직 데이터 보관/이동만 담당(비즈니스 로직 없음).

public class UserDto { // class start

    // 멤버변수
    private int uno; // 회원번호
    private String u_id; // 회원 아이디
    private String u_pwd; // 회원 비밀번호
    private String u_name; // 회원 이름
    private String u_phone; // 회원 전화번호
    private String u_email; // 회원 이메일
    private LocalDate u_date; // 회원 생년월일
//    private int state; // 회원 상태 안쓸거

    // 기본생성자
    public UserDto(){}


    // 생성자
    public UserDto(String u_id, String u_pwd) {
        this.u_id = u_id;
        this.u_pwd = u_pwd;
    }
    public UserDto(String u_id, String u_pwd, String u_name, String u_phone, String u_email, LocalDate u_date) {
        this.u_id = u_id;
        this.u_pwd = u_pwd;
        this.u_name = u_name;
        this.u_phone = u_phone;
        this.u_email = u_email;
        this.u_date = u_date;
    }


    // setter and getter
    public int getUno() {
        return uno;
    }
    public void setUno(int uno) {
        this.uno = uno;
    }
    public String getU_id() {
        return u_id;
    }
    public void setU_id(String u_id) {
        this.u_id = u_id;
    }
    public String getU_pwd() {
        return u_pwd;
    }
    public void setU_pwd(String u_pwd) {
        this.u_pwd = u_pwd;
    }
    public String getU_name() {
        return u_name;
    }
    public void setU_name(String u_name) {
        this.u_name = u_name;
    }
    public String getU_phone() {
        return u_phone;
    }
    public void setU_phone(String u_phone) {
        this.u_phone = u_phone;
    }
    public String getU_email() {
        return u_email;
    }
    public void setU_email(String u_email) {
        this.u_email = u_email;
    }

    //getU_date()에서 LocalDate → java.sql.Date로 변환해서 DB저장 시 호환성.
    //setU_date(String u_date)는 String 입력받아 LocalDate로 파싱.

    public LocalDate getU_date() {
        return u_date;
    }

    public void setU_date(String u_date) {
        this.u_date = LocalDate.parse(u_date);
    }
//    public int getState() {
//        return state;
//    }
//    public void setState(int state) {
//        this.state = state;
//    }

    // toString
    @Override
    public String toString() {
        return "CustomerDto{" +
                "uno=" + uno +
                ", u_id='" + u_id + '\'' +
                ", u_pwd='" + u_pwd + '\'' +
                ", u_phone='" + u_phone + '\'' +
                ", u_email='" + u_email + '\'' +
                ", u_date='" + u_date + '\'' +
//                ", state=" + state +
                '}';
    }
} // class end
