package com.marcel.sorter.service;

public class NoRackAvailableException extends Exception {
    public NoRackAvailableException(String message) {
        super(message);
    }
}
