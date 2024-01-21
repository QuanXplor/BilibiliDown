package nicelee.bilibili.exceptions;

/**
 * token失效
 *
 * @author: A11181121050450
 * @date: 2023-12-27 14:01
 */
public class PeertubeTokenValidException extends RuntimeException {
    public PeertubeTokenValidException(String message) {
        super(message);
    }

    public PeertubeTokenValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
