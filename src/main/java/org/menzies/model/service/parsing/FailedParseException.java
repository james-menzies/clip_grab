package org.menzies.model.service.parsing;

public class FailedParseException extends Exception {

    private String phase;
    private String message;

    FailedParseException(String phase, String message) {
        this.phase = phase;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getPhase() {
        return phase;
    }
}
