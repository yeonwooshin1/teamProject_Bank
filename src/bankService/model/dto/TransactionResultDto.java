package bankService.model.dto;

public class TransactionResultDto {
    // 멤버변수
    private boolean success;    // 입,출금 성공 여부
    private String message;     // 안내 메시지
    private int balance;         // 입,출금 후 남은 잔액
}
