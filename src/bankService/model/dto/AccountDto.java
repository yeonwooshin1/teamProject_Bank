package bankService.model.dto;

public class AccountDto {
    private int tno;             // 거래 번호
    private String account_no;   // 계좌번호
    private int from_acno;       // 출금 계좌 로그번호
    private int to_acno;         // 입금 계좌 로그번호
    private String type;         // 거래 유형 (입금, 출금, 이체)
    private int amount;          // 거래 금액
    private String memo;         // 메모
    private String t_date;       // 거래 일자

    public AccountDto() {
    }

    public AccountDto(int tno, String account_no, int from_acno, int to_acno, String type, int amount, String memo, String t_date) {
        this.tno = tno;
        this.account_no = account_no;
        this.from_acno = from_acno;
        this.to_acno = to_acno;
        this.type = type;
        this.amount = amount;
        this.memo = memo;
        this.t_date = t_date;
    }

    public int getTno() {
        return tno;
    }

    public void setTno(int tno) {
        this.tno = tno;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public int getFrom_acno() {
        return from_acno;
    }

    public void setFrom_acno(int from_acno) {
        this.from_acno = from_acno;
    }

    public int getTo_acno() {
        return to_acno;
    }

    public void setTo_acno(int to_acno) {
        this.to_acno = to_acno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getT_date() {
        return t_date;
    }

    public void setT_date(String t_date) {
        this.t_date = t_date;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "tno=" + tno +
                ", account_no='" + account_no + '\'' +
                ", from_acno=" + from_acno +
                ", to_acno=" + to_acno +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", memo='" + memo + '\'' +
                ", t_date='" + t_date + '\'' +
                '}';
    }
}
