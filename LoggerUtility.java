import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility class for logging messages with timestamps, log levels, and optional context.
 */
public class LoggerUtility {
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean enableDebug = true; // Enable or disable DEBUG logs
    private static PrintStream logOutput = System.out; // Default to console

    /**
     * Logs an informational message.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        log("INFO", message, null);
    }

    /**
     * Logs a warning message.
     *
     * @param message The message to log.
     */
    public static void warn(String message) {
        log("WARN", message, null);
    }

    /**
     * Logs an error message.
     *
     * @param message The message to log.
     */
    public static void error(String message) {
        log("ERROR", message, null);
    }

    /**
     * Logs a debug message if debug logging is enabled.
     *
     * @param message The message to log.
     */
    public static void debug(String message) {
        if (enableDebug) {
            log("DEBUG", message, null);
        }
    }

    /**
     * Logs a message with an optional context.
     *
     * @param level   The log level (e.g., INFO, WARN, ERROR, DEBUG).
     * @param message The message to log.
     * @param context Optional context information to include in the log.
     */
    public static void log(String level, String message, String context) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        String logMessage = "[" + timestamp + "] [" + level + "] " + message;
        if (context != null && !context.isEmpty()) {
            logMessage += " | Context: " + context;
        }
        logOutput.println(logMessage);
    }

    /**
     * Sets whether debug logging is enabled.
     *
     * @param enable True to enable debug logs, false to disable.
     */
    public static void setDebugEnabled(boolean enable) {
        enableDebug = enable;
    }

    /**
     * Redirects logs to a different output stream (e.g., file).
     *
     * @param output The new output stream.
     */
    public static void setLogOutput(PrintStream output) {
        logOutput = output;
    }
}
