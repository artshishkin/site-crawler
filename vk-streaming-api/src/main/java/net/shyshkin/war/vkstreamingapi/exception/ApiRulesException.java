package net.shyshkin.war.vkstreamingapi.exception;

public class ApiRulesException extends RuntimeException{
    public ApiRulesException() {
        super();
    }

    public ApiRulesException(String message) {
        super(message);
    }

    public ApiRulesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiRulesException(Throwable cause) {
        super(cause);
    }
}
