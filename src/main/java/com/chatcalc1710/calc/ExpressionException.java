package com.chatcalc1710.calc;

/** Thrown when a chat word is not a valid math expression. */
public class ExpressionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ExpressionException(String message) {
        super(message);
    }
}
