package cc.jweb.boot.common.exception;

public class ParameterValidationException extends RuntimeException {
    private static final long serialVersionUID = -2427136931621038094L;

    public ParameterValidationException(String message) {
        super(message);
    }
}