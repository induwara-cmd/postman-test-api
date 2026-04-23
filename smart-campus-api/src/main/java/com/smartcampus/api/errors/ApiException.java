package com.smartcampus.api.errors;

public class ApiException extends RuntimeException {
    private final int status;
    private final String safeMessage;

    public ApiException(int status, String safeMessage) {
        super(safeMessage);
        this.status = status;
        this.safeMessage = safeMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getSafeMessage() {
        return safeMessage;
    }
}
