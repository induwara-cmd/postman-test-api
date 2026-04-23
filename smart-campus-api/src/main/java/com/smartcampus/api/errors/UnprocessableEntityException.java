package com.smartcampus.api.errors;

public final class UnprocessableEntityException extends ApiException {
    public UnprocessableEntityException(String safeMessage) {
        super(422, safeMessage);
    }
}
