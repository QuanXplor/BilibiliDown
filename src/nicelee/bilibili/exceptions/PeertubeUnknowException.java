package nicelee.bilibili.exceptions;

/**
 * Peertube未知异常
 *
 * @author: A11181121050450
 * @date: 2024-01-01 16:53
 */
public class PeertubeUnknowException extends RuntimeException {
    public PeertubeUnknowException(String message) {
        super(message);
    }

    public PeertubeUnknowException(String message, Throwable cause) {
        super(message, cause);
    }
}
