package com.chatcalc1710.calc;

import java.util.regex.Pattern;

/**
 * Decides what pressing Tab should do to the text of the chat box. It only works on strings, so it has no
 * dependency on Minecraft.
 *
 * <ul>
 * <li>{@code 3+3} becomes {@code 6}</li>
 * <li>{@code 3+3=} becomes {@code 3+3=6}</li>
 * <li>{@code 1234s} becomes {@code 19 (Stacks) + 18 (Items)}</li>
 * <li>{@code 5000dc} becomes {@code 1 (Double Chest) + 24 (Stacks) + 8 (Items)}</li>
 * </ul>
 *
 * The number before {@code s} or {@code dc} can also be a parenthesised expression, like
 * {@code (64*9+3)s}.
 */
public final class ChatCalculator {

    /** Largest count the stack suffixes accept, keeps the maths well inside the range of a long. */
    private static final double MAX_COUNT = 1e15;

    private static final Pattern PLAIN_NUMBER = Pattern.compile("[+-]?([0-9][0-9,]*(\\.[0-9]*)?|\\.[0-9]+)");

    /** The new content of the chat box and where the cursor goes. */
    public static final class Edit {

        public final String text;
        public final int cursor;

        Edit(String text, int cursor) {
            this.text = text;
            this.cursor = cursor;
        }
    }

    private ChatCalculator() {}

    /**
     * @param text      current content of the chat box
     * @param cursor    cursor position inside {@code text}
     * @param maxLength the chat box length limit; results that would not fit are rejected
     * @return the edit to apply, or null if there is nothing to calculate at the cursor
     */
    public static Edit calculate(String text, int cursor, CalcSettings settings, int maxLength) {
        cursor = Math.max(0, Math.min(cursor, text.length()));

        int start = text.lastIndexOf(' ', cursor - 1) + 1;
        int end = text.indexOf(' ', cursor);
        if (end < 0) end = text.length();
        if (start >= end) return null;

        String word = text.substring(start, end);

        String replacement = stackReplacement(word, settings);
        if (replacement != null) {
            return replace(text, start, end, replacement, maxLength);
        }

        if (word.endsWith("=")) {
            String result = evaluate(word.substring(0, word.length() - 1), settings);
            if (result == null) return null;
            return replace(text, end, end, result, maxLength);
        }

        if (PLAIN_NUMBER.matcher(word)
            .matches()) return null;
        String result = evaluate(word, settings);
        if (result == null) return null;
        return replace(text, start, end, result, maxLength);
    }

    private static Edit replace(String text, int start, int end, String replacement, int maxLength) {
        String out = text.substring(0, start) + replacement + text.substring(end);
        if (out.length() > maxLength) return null;
        return new Edit(out, start + replacement.length());
    }

    private static String evaluate(String expression, CalcSettings settings) {
        if (expression.isEmpty()) return null;
        try {
            double value = ExpressionParser.evaluate(expression);
            if (Double.isNaN(value) || Double.isInfinite(value)) return null;
            return settings.formatNumber(value);
        } catch (ExpressionException e) {
            return null;
        }
    }

    private static String stackReplacement(String word, CalcSettings settings) {
        String lower = word.toLowerCase();
        StackCalculator.Unit unit;
        int suffixLength;
        if (lower.endsWith("dc")) {
            unit = StackCalculator.Unit.DOUBLE_CHEST;
            suffixLength = 2;
        } else if (lower.endsWith("s")) {
            unit = StackCalculator.Unit.STACK;
            suffixLength = 1;
        } else {
            return null;
        }

        String amount = word.substring(0, word.length() - suffixLength);
        boolean parenthesised = amount.startsWith("(") && amount.endsWith(")");
        if (!parenthesised && !PLAIN_NUMBER.matcher(amount)
            .matches()) return null;

        double value;
        try {
            value = ExpressionParser.evaluate(amount);
        } catch (ExpressionException e) {
            return null;
        }
        if (Double.isNaN(value) || value < 0 || value > MAX_COUNT) return null;

        return StackCalculator.format((long) Math.floor(value), unit, settings);
    }
}
