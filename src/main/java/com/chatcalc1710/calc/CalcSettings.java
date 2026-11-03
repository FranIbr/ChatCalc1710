package com.chatcalc1710.calc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** User configurable output options. Pure data, so the calculator logic can be tested without Minecraft. */
public final class CalcSettings {

    public static final String DEFAULT_DECIMAL_FORMAT = "###0.##";

    public final String decimalFormat;
    public final String item, items;
    public final String stack, stacks;
    public final String doubleChest, doubleChests;

    public CalcSettings(String decimalFormat, String item, String items, String stack, String stacks,
        String doubleChest, String doubleChests) {
        this.decimalFormat = decimalFormat;
        this.item = item;
        this.items = items;
        this.stack = stack;
        this.stacks = stacks;
        this.doubleChest = doubleChest;
        this.doubleChests = doubleChests;
    }

    public static CalcSettings defaults() {
        return new CalcSettings(
            DEFAULT_DECIMAL_FORMAT,
            "Item",
            "Items",
            "Stack",
            "Stacks",
            "Double Chest",
            "Double Chests");
    }

    /**
     * Formats a result using the configured pattern. Always uses '.' as decimal separator so the output can be
     * evaluated again, whatever the player's system locale is. Falls back to the default pattern if the configured
     * one is invalid.
     */
    public String formatNumber(double value) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.ROOT);
        DecimalFormat format;
        try {
            format = new DecimalFormat(decimalFormat, symbols);
        } catch (IllegalArgumentException | NullPointerException e) {
            format = new DecimalFormat(DEFAULT_DECIMAL_FORMAT, symbols);
        }
        String text = format.format(value);
        return text.equals("-0") ? "0" : text;
    }
}
