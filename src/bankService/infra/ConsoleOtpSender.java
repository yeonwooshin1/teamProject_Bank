package bankService.infra;

public class ConsoleOtpSender implements OtpSender {

    // --------------------------
    // 싱글톤 구현 (static inner class)
    // --------------------------
    private ConsoleOtpSender() {}
    private static class Holder {
        private static final ConsoleOtpSender INSTANCE = new ConsoleOtpSender();
    }
    public static ConsoleOtpSender getInstance() { return Holder.INSTANCE; }

    /**
     * 실제 전송 대신 System.out 으로 표시하여 사용자가 OTP 를 볼 수 있게 함
     */
    public void send(String dest, String code) {

    }
}