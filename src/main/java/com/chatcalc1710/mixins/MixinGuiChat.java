package com.chatcalc1710.mixins;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chatcalc1710.client.ChatCalcClient;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    @Shadow
    protected GuiTextField inputField;

    // Cancelled when something was calculated, so vanilla doesn't also try to complete a player name.
    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void chatcalc1710$onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (keyCode == Keyboard.KEY_TAB && ChatCalcClient.calculate(inputField)) {
            ci.cancel();
        }
    }
}
