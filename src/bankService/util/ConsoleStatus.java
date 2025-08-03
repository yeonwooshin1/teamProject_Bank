package bankService.util;

import java.util.regex.Pattern;

/**
 * ConsoleStatus
 * ------------------------------
 * 목적:
 *  - "상태줄"을 콘솔 맨 아래 한 줄에 계속 갱신해서 보여주기
 *  - 프롬프트/일반 메시지를 찍을 땐 상태줄을 잠깐 지웠다가, 끝나면 다시 표시
 *  - ANSI 색상코드를 써도 화면이 지저분해지지 않도록 길이 계산을 '보이는 문자' 기준으로 수행
 *
 * 핵심 메서드:
 *  - show(msg)   : 상태줄 한 줄 덮어쓰기
 *  - pause()     : 상태줄 잠깐 숨김(프롬프트/메시지 찍기 전)
 *  - resume()    : 상태줄 다시 표시 가능 상태로 전환(프롬프트/메시지 찍은 후)
 *
 * 스레드 안전:
 *  - 내부 전용 lock(Object) 으로 상태줄 출력만 보호
 *  - 콘솔 전역 I/O 락(ioLock)과는 별개(입출력은 View에서 ioLock으로 보호)
 */
public final class ConsoleStatus {

    /** 상태줄 전용 락(다른 콘솔 I/O 락과는 별개) */
    private final Object lock = new Object();

    /** 직전에 찍힌 "보이는 문자 길이"(ANSI 제거 기준) — 나중에 덮어쓸 때 공백 패딩 계산용 */
    private int lastLen = 0;

    /** 상태줄 표시를 잠깐 멈출지 여부(프롬프트/메시지 찍는 동안 true) */
    private boolean paused = false;

    /**
     * show()가 끝날 때 자동으로 색상을 리셋할지 여부
     * - true: 메시지 끝에 항상 "\u001B[0m"(ANSI reset) 덧붙임 → 색상 번짐 방지
     * - false: 호출자가 직접 리셋을 책임짐(고급 사용)
     */
    private final boolean autoReset;

    /** 기본 생성자: 자동 리셋 on(권장) */
    public ConsoleStatus() { this(true); }

    /** 생성자: 자동 리셋 여부를 직접 선택 */
    public ConsoleStatus(boolean autoReset) { this.autoReset = autoReset; }

    // ===================== 공개 API =====================

    /**
     * 상태 한 줄을 "덮어쓰기" 방식으로 갱신
     * - "\r"로 커서를 줄 맨 앞으로 보낸 다음, msg를 출력
     * - 직전 출력보다 msg가 짧으면 "부족한 길이만큼 공백"을 덧붙여 이전 글자 찌꺼기를 지움
     * - ANSI 색상코드는 화면 폭에 영향을 주지 않으므로, 길이 계산에서 제거(strip) 후 길이를 비교
     */
    public void show(String msg) {
        synchronized (lock) {
            if (paused) return; // 숨김 상태라면 출력 생략

            // 1) 보이는 문자 길이 계산: ANSI 코드 제거 후 length()
            String visible = stripAnsi(msg);      // 예: "\u001B[31m빨강\u001B[0m" → "빨강"
            int visibleLen = visible.length();    // '화면에 보이는' 문자 수 기준

            // 2) 직전에 찍은 길이보다 현재가 짧으면, 부족분만큼 공백으로 덮어써서 찌꺼기 제거
            int pad = Math.max(0, lastLen - visibleLen);

            // 3) 실제 출력 문자열 구성
            StringBuilder sb = new StringBuilder("\r") // 커서를 줄 맨 앞으로
                    .append(msg);                      // 원본 메시지(색상코드 포함)
            if (autoReset) sb.append("\u001B[0m");     // 자동 리셋(색/스타일 초기화)
            if (pad > 0) sb.append(" ".repeat(pad));   // 이전 글자 길이만큼 공백 덮어쓰기

            // 4) 출력 & 버퍼 비우기
            System.out.print(sb);
            System.out.flush();

            // 5) 다음 비교를 위해 이번 '보이는 길이' 저장
            lastLen = visibleLen;
        }
    }

    /**
     * 상태줄 잠시 숨김(프롬프트/일반 메시지 출력 직전에 호출)
     * - 현재 줄의 '보이는 길이'만큼 공백으로 덮고, 커서를 줄 맨 앞으로 되돌림
     * - 이후 show() 호출 전까지는 상태줄이 나타나지 않음(paused=true)
     */
    public void pause() {
        synchronized (lock) {
            if (paused) return; // 이미 숨김 상태면 무시

            // "\r" + 'lastLen' 개의 공백 + "\r"  → 줄 지우고 커서 맨 앞으로
            System.out.print("\r" + " ".repeat(lastLen) + "\r");
            System.out.flush();

            // 상태 초기화
            lastLen = 0;
            paused = true;
        }
    }

    /**
     * 상태줄 다시 표시 가능(프롬프트/메시지 출력 '후' 호출)
     * - 여기서 즉시 출력하진 않음(show를 호출하면 다시 나타남)
     */
    public void resume() {
        synchronized (lock) {
            paused = false;
        }
    }

    // ===================== 내부 유틸 =====================

    /**
     * ANSI 이스케이프 시퀀스(색상/스타일 코드)를 제거
     * - 패턴: ESC '[' ... 최종문자
     *   예) "\u001B[31m" (빨강), "\u001B[0m" (리셋) 등
     * - 이 코드는 "표시 폭 0" 이므로, 화면에 보이는 문자 길이를 계산할 때 제외해야 함
     */
    private static final Pattern ANSI = Pattern.compile("\u001B\\[[0-?]*[ -/]*[@-~]");
    private static String stripAnsi(String s) {
        return ANSI.matcher(s).replaceAll("");
    }
}
