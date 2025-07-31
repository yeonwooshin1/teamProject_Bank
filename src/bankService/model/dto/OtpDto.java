package bankService.model.dto;

import java.time.Instant;

public class OtpDto {   // class start
    private String otpHashHex;     // 해시값 문자열
    private Instant issuedAt;      // 발급 시각
    private Instant submitUntil;   // 입력 마감 시각
    private int attempts;          // 실패 시도 횟수
    private Instant trustUntil;    // 검증 성공하면 유효시각

    // setter getter
    public String getOtpHashHex() {
        return otpHashHex;
    }

    public void setOtpHashHex(String otpHashHex) {
        this.otpHashHex = otpHashHex;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getSubmitUntil() {
        return submitUntil;
    }

    public void setSubmitUntil(Instant submitUntil) {
        this.submitUntil = submitUntil;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attemptsUsed) {
        this.attempts = attemptsUsed;
    }

    public Instant getTrustUntil() {
        return trustUntil;
    }

    public void setTrustUntil(Instant trustUntil) {
        this.trustUntil = trustUntil;
    }
}