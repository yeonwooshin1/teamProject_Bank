package bankService.model.dto;

public class TransactionDto {
    // 멤버변수
    private String account_no;      // 계좌 번호
    private String account_pwd;     // 계좌 비밀번호
    private int amount;             // 거래 금액

    // 생성자

    public TransactionDto() {
    }

    public TransactionDto(String account_no, String account_pwd, int amount) {
        this.account_no = account_no;
        this.account_pwd = account_pwd;
        this.amount = amount;
    }

    // getter , setter , toString


    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
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

    @Override
    public String toString() {
        return "TransactionDto{" +
                "account_no='" + account_no + '\'' +
                ", account_pwd='" + account_pwd + '\'' +
                ", amount=" + amount +
                '}';
    }
}
