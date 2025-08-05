package bankService.app;

// 전역 세션 관리 클래스
// 기존 ConsoleSession 에 static으로 선언된 uno 없음
// 때문에 다른 controller 에서 못부름

public class ConsoleSessionManager {
    private static ConsoleSession currentSession;

    public static void setSession(ConsoleSession session) {
        currentSession = session;
    }

    public static ConsoleSession getSession() {
        return currentSession;
    }
}