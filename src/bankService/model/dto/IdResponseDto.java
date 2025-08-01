package bankService.model.dto;

public class IdResponseDto { // class start
    private String u_id;

    public IdResponseDto() {
    }

    public IdResponseDto(String u_id) {
        this.u_id = u_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }
} // class end
