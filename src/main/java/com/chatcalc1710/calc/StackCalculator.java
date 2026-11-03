package com.chatcalc1710.calc;

import java.util.ArrayList;
import java.util.List;

/** Splits an item count into double chests / stacks / items. */
public final class StackCalculator {

    public static final int STACK_SIZE = 64;
    public static final int DOUBLE_CHEST_SIZE = 54 * STACK_SIZE;

    /** The largest unit used when splitting a count. Smaller units are always used for the remainder. */
    public enum Unit {
        STACK,
        DOUBLE_CHEST
    }

    private StackCalculator() {}

    /** e.g. 5000 with DOUBLE_CHEST gives "1 (Double Chest) + 24 (Stacks) + 8 (Items)". Zero parts are left out. */
    public static String format(long count, Unit unit, CalcSettings labels) {
        List<String> parts = new ArrayList<String>();
        long remaining = count;

        if (unit == Unit.DOUBLE_CHEST) {
            addPart(parts, remaining / DOUBLE_CHEST_SIZE, labels.doubleChest, labels.doubleChests);
            remaining %= DOUBLE_CHEST_SIZE;
        }

        addPart(parts, remaining / STACK_SIZE, labels.stack, labels.stacks);
        remaining %= STACK_SIZE;

        if (remaining > 0 || parts.isEmpty()) {
            addPart(parts, remaining, labels.item, labels.items, true);
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) out.append(" + ");
            out.append(parts.get(i));
        }
        return out.toString();
    }

    private static void addPart(List<String> parts, long amount, String singular, String plural) {
        addPart(parts, amount, singular, plural, false);
    }

    private static void addPart(List<String> parts, long amount, String singular, String plural, boolean force) {
        if (amount == 0 && !force) return;
        parts.add(amount + " (" + (amount == 1 ? singular : plural) + ")");
    }
}
