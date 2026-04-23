package com.smartcampus.api.errors;

public final class ForbiddenException extends ApiException {
    public ForbiddenException(String safeMessage) {
        super(403, safeMessage);
    }
}
