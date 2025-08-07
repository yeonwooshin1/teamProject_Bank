package bankService.model.dto;


// 아이디 정보
public class IdResponseDto {
    // 멤버변수
    private String u_id;

    // 생성자
    public IdResponseDto(String u_id) {
        this.u_id = u_id;
    }

    // setter ane getter
    public String getU_id() { return u_id; }
    public void setU_id(String u_id) { this.u_id = u_id; }
}