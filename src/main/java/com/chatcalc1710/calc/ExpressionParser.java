package com.chatcalc1710.calc;

import java.util.Locale;

/**
 * Small recursive descent evaluator for the expressions typed in chat.
 *
 * <pre>
 * expression = term { ("+" | "-") term }
 * term       = unary { ("*" | "/") unary }
 * unary      = ("+" | "-") unary | power
 * power      = primary [ "^" unary ]
 * primary    = number | "(" expression ")" | function ( "(" expression ")" | unary )
 * </pre>
 *
 * The functions are sqrt, sin, cos and tan. Numbers may contain "," as a thousands separator (it is simply ignored).
 * Trigonometric functions work in degrees.
 */
public final class ExpressionParser {

    private final String src;
    private int pos;

    private ExpressionParser(String src) {
        this.src = src;
    }

    public static double evaluate(String expression) throws ExpressionException {
        ExpressionParser parser = new ExpressionParser(expression);
        double value = parser.parseExpression();
        if (parser.pos < parser.src.length()) {
            throw new ExpressionException("Unexpected '" + parser.src.charAt(parser.pos) + "' at " + parser.pos);
        }
        return value;
    }

    private boolean eat(char expected) {
        if (pos < src.length() && src.charAt(pos) == expected) {
            pos++;
            return true;
        }
        return false;
    }

    private double parseExpression() throws ExpressionException {
        double x = parseTerm();
        while (true) {
            if (eat('+')) x += parseTerm();
            else if (eat('-')) x -= parseTerm();
            else return x;
        }
    }

    private double parseTerm() throws ExpressionException {
        double x = parseUnary();
        while (true) {
            if (eat('*')) x *= parseUnary();
            else if (eat('/')) x /= parseUnary();
            else return x;
        }
    }

    private double parseUnary() throws ExpressionException {
        if (eat('+')) return parseUnary();
        if (eat('-')) return -parseUnary();
        return parsePower();
    }

    private double parsePower() throws ExpressionException {
        double base = parsePrimary();
        if (eat('^')) return Math.pow(base, parseUnary());
        return base;
    }

    private double parsePrimary() throws ExpressionException {
        if (pos >= src.length()) throw new ExpressionException("Unexpected end of expression");

        char c = src.charAt(pos);
        if (c == '(') {
            pos++;
            double x = parseExpression();
            if (!eat(')')) throw new ExpressionException("Missing ')'");
            return x;
        }
        if (isNumberChar(c)) return parseNumber();
        if (Character.isLetter(c)) return parseName();
        throw new ExpressionException("Unexpected '" + c + "' at " + pos);
    }

    private static boolean isNumberChar(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == ',';
    }

    private double parseNumber() throws ExpressionException {
        int start = pos;
        while (pos < src.length() && isNumberChar(src.charAt(pos))) pos++;
        String text = src.substring(start, pos)
            .replace(",", "");
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new ExpressionException("Invalid number: " + text);
        }
    }

    private double parseName() throws ExpressionException {
        int start = pos;
        while (pos < src.length() && Character.isLetter(src.charAt(pos))) pos++;
        String name = src.substring(start, pos)
            .toLowerCase(Locale.ROOT);

        double arg;
        if (eat('(')) {
            arg = parseExpression();
            if (!eat(')')) throw new ExpressionException("Missing ')'");
        } else {
            arg = parseUnary();
        }
        return applyFunction(name, arg);
    }

    private static double applyFunction(String name, double x) throws ExpressionException {
        switch (name) {
            case "sqrt":
                return Math.sqrt(x);
            case "sin":
                return Math.sin(Math.toRadians(x));
            case "cos":
                return Math.cos(Math.toRadians(x));
            case "tan":
                return Math.tan(Math.toRadians(x));
            default:
                throw new ExpressionException("Unknown function: " + name);
        }
    }
}
