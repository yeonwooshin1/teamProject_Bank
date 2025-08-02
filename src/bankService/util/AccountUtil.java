package bankService.util;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * 단행 계좌번호 생성 및 검증 유틸.
 * 형식: 111-301-xxxxxx
 * xxxxxx 는 6자리 숫자 난수 (앞에 0 포함)
 */
public final class AccountUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    // 고정된 앞부분 (단행)
    private static final String PREFIX = "111-301-";

    // 전체 패턴 검증
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^111-\\d{3}$-\\d{6}$");

    private AccountUtil() {}

    /**
     * 계좌번호 생성 (예: 111-223-034582)
     */
    public static String generateAccountNumber() {
        int number = RANDOM.nextInt(1_000_000); // 0 .. 999999
        String sixDigits = String.format("%06d", number);
        return PREFIX + sixDigits;
    }

    /**
     * 계좌번호 형식 유효성 검사
     * @param account 계좌번호 문자열
     * @return 형식이 "111-zzz-xxxxxx"인지 여부
     */
    public static boolean isValidAccountNumber(String account) {
        if (account == null) return false;
        return ACCOUNT_PATTERN.matcher(account).matches();
    }
}