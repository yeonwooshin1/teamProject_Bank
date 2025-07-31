package bankService.model.dto;

public class TransactionLogDto {
    // 멤버변수
    private int tno;        // 입출금 로그 번호(pk)
    private int acno;       // 계좌 로그 번호(fk)
    private int t_state;    // 입금 , 출금 , 이체 상태
    private int amount;     // 입/출금 금액
    private int balance;    // 잔액
    private String t_text;  // 사유
    private String t_date;  // 날짜

}