package com.frtelg.cadence.exception;

public class InvalidNumberException extends RuntimeException {
    public InvalidNumberException(int number) {
        super("Invalid number: " + number);
    }
}
