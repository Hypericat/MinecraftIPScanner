import java.io.File;

public class RunnablePing implements Runnable {
    public void run() {
        while (true) {
            IP ip = Main.getNextIP();
            if (ip == null) break;
            ip.ping(Main.logsPath, Main.timeOut, Main.port);
        }
    }
}
