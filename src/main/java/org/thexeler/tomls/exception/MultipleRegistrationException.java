package org.thexeler.tomls.exception;

public class MultipleRegistrationException extends TomlException {
    public MultipleRegistrationException(String message) {
        super("Multiple registration of " + message);
    }
}
