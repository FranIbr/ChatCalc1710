package com.chatcalc1710;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = ChatCalc1710.MODID,
    name = "ChatCalc1710",
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*")
public class ChatCalc1710 {

    public static final String MODID = "chatcalc1710";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ChatCalcConfig.load(event.getSuggestedConfigurationFile());
    }
}
