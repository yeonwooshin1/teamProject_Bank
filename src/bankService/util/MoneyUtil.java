package bankService.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


// 금액 표시용 Util
public final class MoneyUtil {  // class start
    private MoneyUtil() {}

    // int 타입 금액을 "1,234원" 형태로 포맷해줌
    // db 에서 가져온 int값 이 메소드 쓰면 view 출력할 때 유용

    public static String formatWon(int amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.KOREA);
        symbols.setGroupingSeparator(',');
        // 소수점 없이 정수부만 그룹핑
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(amount) + "원";
    }   // return end
}   // class end