package me.strathdee.assignmentapi.exceptionhandling;

public class TempNotAvailableException extends RuntimeException {
    public TempNotAvailableException(Integer tempId) {
        super(String.format("The temp with tempId %d does not exist or is not available for the specified time-range.", tempId));
    }

    public TempNotAvailableException(String message) {
        super(message);
    }
}
