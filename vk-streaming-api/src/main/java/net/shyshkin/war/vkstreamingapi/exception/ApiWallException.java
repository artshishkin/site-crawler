package net.shyshkin.war.vkstreamingapi.exception;

public class ApiWallException extends RuntimeException{
    public ApiWallException() {
        super();
    }

    public ApiWallException(String message) {
        super(message);
    }

    public ApiWallException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiWallException(Throwable cause) {
        super(cause);
    }
}
