package bankService.model.dto;

public class CustomerDto { // class start

    // 멤버변수
    private int uno;
    private String u_id;
    private String u_pwd;
    private String u_phone;
    private String u_email;
    private String u_date;
    private int state;

    // 생성자
    public CustomerDto(int uno, String u_id, String u_pwd, String u_phone, String u_email, String u_date, int state) {
        this.uno = uno;
        this.u_id = u_id;
        this.u_pwd = u_pwd;
        this.u_phone = u_phone;
        this.u_email = u_email;
        this.u_date = u_date;
        this.state = state;
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
    public String getU_date() {
        return u_date;
    }
    public void setU_date(String u_date) {
        this.u_date = u_date;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
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
                ", state=" + state +
                '}';
    }
} // class end
