package com.smartcampus.api.errors;

public final class ConflictException extends ApiException {
    public ConflictException(String safeMessage) {
        super(409, safeMessage);
    }
}
