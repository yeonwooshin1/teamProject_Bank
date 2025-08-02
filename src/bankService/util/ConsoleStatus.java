package bankService.util;

public class ConsoleStatus {
    private final Object lock = new Object();
    private int lastLen = 0;
    private boolean paused = false;

    public void show(String msg) {
        synchronized(lock) {
            if (paused) return;
            StringBuilder sb = new StringBuilder("\r").append(msg);
            int pad = Math.max(0, lastLen - msg.length());
            sb.append(" ".repeat(pad));
            System.out.print(sb);
            System.out.flush();
            lastLen = msg.length();
        }
    }
    public void pause()  { clearLine(); paused=true; }
    public void resume() { paused=false; }

    private void clearLine() {
        synchronized(lock){
            System.out.print("\r"+" ".repeat(lastLen)+"\r");
            System.out.flush();
            lastLen=0;
        }
    }
}
