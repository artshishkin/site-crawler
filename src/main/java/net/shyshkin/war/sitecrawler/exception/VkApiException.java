package net.shyshkin.war.sitecrawler.exception;

public class VkApiException extends RuntimeException{
    public VkApiException() {
        super();
    }

    public VkApiException(String message) {
        super(message);
    }

    public VkApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public VkApiException(Throwable cause) {
        super(cause);
    }
}
