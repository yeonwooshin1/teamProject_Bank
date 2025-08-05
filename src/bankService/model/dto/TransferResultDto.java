package bankService.model.dto;

public class TransferResultDto {
    // 멤버변수
    private boolean success;    // 이체 성공 여부
    private String message;     // 안내 메시지
    private int balance;         // 이체 후 남은 잔액

    // 생성자

    public TransferResultDto() {
    }

    public TransferResultDto(boolean success, String message, int balance) {
        this.success = success;
        this.message = message;
        this.balance = balance;
    }

    // getter , setter , toString
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "TransferResultDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", balance=" + balance +
                '}';
    }
}