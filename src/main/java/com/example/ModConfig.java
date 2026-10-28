package com.example;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModConfig {
    public static boolean autoCartEnabled = true;
    public static boolean autoTotemEnabled = true;
    
    public static KeyBinding menuKey = new KeyBinding(
        "key.cartistmc.menu",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_RIGHT_SHIFT,
        "category.cartistmc"
    );
}
