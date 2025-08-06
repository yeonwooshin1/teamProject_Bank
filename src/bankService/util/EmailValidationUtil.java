package bankService.util;

import java.util.regex.Pattern;

public class EmailValidationUtil {

    // 아주 단순한 이메일 형식 검사: 반드시 xxx@xxx.com 형태만 허용
    private static final Pattern SIMPLE_EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$"
    );

    /**
     * 이메일이 xxxx@xxx.com 형식에 맞는지 검사
     *
     * @param email 검사할 이메일 문자열
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isSimpleEmailFormat(String email) {
        if (email == null || email.isEmpty()) return false;
        return SIMPLE_EMAIL_PATTERN.matcher(email).matches();
    }
}