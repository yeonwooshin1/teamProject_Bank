package bankService.model.dto;

public class TransferDto {
    // 멤버변수
    private String sender_no;       // 이체할 계좌 번호
    private String receiver_no;     // 이체 받을 계좌 번호
    private String account_pwd;     // 계좌 비밀번호
    private int amount;             // 이체 할 금액
    private String t_text;          // 이체 시 메모
}
