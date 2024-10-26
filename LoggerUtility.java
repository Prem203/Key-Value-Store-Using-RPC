import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtility {
    public static void log(String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("[" + timestamp + "] " + message);
    }
}
