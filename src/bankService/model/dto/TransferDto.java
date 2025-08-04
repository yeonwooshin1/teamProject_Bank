package bankService.model.dto;

public class TransferDto {
    // 멤버변수
    private String sender_no;       // 이체할 계좌 번호
    private String receiver_no;     // 이체 받을 계좌 번호
    private String account_pwd;     // 계좌 비밀번호
    private int amount;             // 이체 할 금액
    private String t_text;          // 이체 시 메모

    // 생성자

    public TransferDto() {
    }

    public TransferDto(String sender_no, String receiver_no, String account_pwd, int amount, String t_text) {
        this.sender_no = sender_no;
        this.receiver_no = receiver_no;
        this.account_pwd = account_pwd;
        this.amount = amount;
        this.t_text = t_text;
    }

    // getter , setter , toString

    public String getSender_no() {
        return sender_no;
    }

    public void setSender_no(String sender_no) {
        this.sender_no = sender_no;
    }

    public String getReceiver_no() {
        return receiver_no;
    }

    public void setReceiver_no(String receiver_no) {
        this.receiver_no = receiver_no;
    }

    public String getAccount_pwd() {
        return account_pwd;
    }

    public void setAccount_pwd(String account_pwd) {
        this.account_pwd = account_pwd;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getT_text() {
        return t_text;
    }

    public void setT_text(String t_text) {
        this.t_text = t_text;
    }

    @Override
    public String toString() {
        return "TransferDto{" +
                "sender_no='" + sender_no + '\'' +
                ", receiver_no='" + receiver_no + '\'' +
                ", account_pwd='" + account_pwd + '\'' +
                ", amount=" + amount +
                ", t_text='" + t_text + '\'' +
                '}';
    }
}
