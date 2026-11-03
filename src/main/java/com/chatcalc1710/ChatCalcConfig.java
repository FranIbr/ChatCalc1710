package com.chatcalc1710;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.chatcalc1710.calc.CalcSettings;

public final class ChatCalcConfig {

    private static volatile CalcSettings settings = CalcSettings.defaults();

    private ChatCalcConfig() {}

    public static CalcSettings settings() {
        return settings;
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        try {
            config.load();
            CalcSettings d = CalcSettings.defaults();

            String format = config.getString(
                "decimalFormat",
                Configuration.CATEGORY_GENERAL,
                d.decimalFormat,
                "java.text.DecimalFormat pattern used for results. \"###0.##\" gives at most two decimals, \"###0\" rounds to whole numbers.");

            String cat = "labels";
            config.addCustomCategoryComment(cat, "Names used by the stack calculator (1234s, 5000dc).");

            settings = new CalcSettings(
                format,
                config.getString("item", cat, d.item, "Singular name of an item."),
                config.getString("items", cat, d.items, "Plural name of an item."),
                config.getString("stack", cat, d.stack, "Singular name of a stack."),
                config.getString("stacks", cat, d.stacks, "Plural name of a stack."),
                config.getString("doubleChest", cat, d.doubleChest, "Singular name of a double chest."),
                config.getString("doubleChests", cat, d.doubleChests, "Plural name of a double chest."));
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
