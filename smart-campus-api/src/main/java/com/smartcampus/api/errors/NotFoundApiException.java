package com.smartcampus.api.errors;

public final class NotFoundApiException extends ApiException {
    public NotFoundApiException(String safeMessage) {
        super(404, safeMessage);
    }
}
