package com.example.assignmentapi.exceptionhandling;

import java.io.Serial;

public class TempNotAvailableException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public TempNotAvailableException(Integer tempId) {
        super(String.format("The temp with tempId %d does not exist or is not available for the specified time-range.", tempId));
    }

    public TempNotAvailableException(String message) {
        super(message);
    }
}
