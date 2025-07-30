package bankService.model.dto;

import java.time.Instant;
import java.util.Objects;

public final class OtpDto { // class start
    // 멤버변수
    private final String userId;        // 사용자 ID
    private final String otpHash;       // DB에 저장된 OTP 해시(SHA-256 등)
    private final Instant expiresAt;    // 만료 시각(UTC 기준 Instant)
    private final int attempts;         // 실패 시도 횟수

    // 생성자
    public OtpDto(String userId, String otpHash, Instant expiresAt, int attempts) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.otpHash = Objects.requireNonNull(otpHash, "otpHash");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.attempts = attempts;
    }
    // 파라미터로 입력된 값이 null이라면 NullPointerException(NPE)이 발생하고, 그렇지 않다면 입력값을 그래도 반환한다.

    // getter
    public String getUserId() { return userId; }
    public String otpHash() { return otpHash; }
    public Instant expiresAt() { return expiresAt; }
    public int attempts() { return attempts; }

    // toString
    @Override
    public String toString() {
        return "OtpDTO[userId=" + userId
                + ", otpHash=" + otpHash
                + ", expiresAt=" + expiresAt
                + ", attempts=" + attempts + "]";


    }
}   // class end