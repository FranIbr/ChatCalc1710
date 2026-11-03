package com.chatcalc1710.client;

import net.minecraft.client.gui.GuiTextField;

import com.chatcalc1710.ChatCalcConfig;
import com.chatcalc1710.calc.ChatCalculator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Applies the calculator to the chat box. Kept out of the mixin package, which can't be referenced directly. */
@SideOnly(Side.CLIENT)
public final class ChatCalcClient {

    /** Vanilla refuses to send chat messages longer than this. */
    private static final int MAX_CHAT_LENGTH = 100;

    private ChatCalcClient() {}

    /** @return true if the text of the field was changed */
    public static boolean calculate(GuiTextField field) {
        ChatCalculator.Edit edit = ChatCalculator
            .calculate(field.getText(), field.getCursorPosition(), ChatCalcConfig.settings(), MAX_CHAT_LENGTH);
        if (edit == null) return false;
        field.setText(edit.text);
        field.setCursorPosition(edit.cursor);
        return true;
    }
}
