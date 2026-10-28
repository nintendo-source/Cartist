package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class CartPvpMod implements ClientModInitializer {
    private static KeyBinding comboKey;
    public static boolean autoTotemEnabled = true;

    @Override
    public void onInitializeClient() {
        comboKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cartistmc.combo",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.cartistmc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null) return;

            // Auto-Totem (Always active to save you)
            if (autoTotemEnabled) {
                handleAutoTotem(client);
            }

            // Cart PvP Combo (Triggers when you press R)
            if (comboKey.isPressed()) {
                executeCartCombo(client);
            }
        });
    }

    private void handleAutoTotem(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                int slotId = i < 9 ? i + 36 : i;
                client.interactionManager.clickSlot(player.playerScreenHandler.syncId, slotId, 0, SlotActionType.PICKUP, player);
                client.interactionManager.clickSlot(player.playerScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, player);
                client.interactionManager.clickSlot(player.playerScreenHandler.syncId, slotId, 0, SlotActionType.PICKUP, player);
                break;
            }
        }
    }

    private void executeCartCombo(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        int railSlot = findHotbarItem(player, Items.RAIL);
        int tntCartSlot = findHotbarItem(player, Items.TNT_MINECART);
        int bowSlot = findHotbarItem(player, Items.BOW);

        if (railSlot == -1 || tntCartSlot == -1) return;

        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHit = (BlockHitResult) hit;

        int originalSlot = player.getInventory().selectedSlot;

        // 1. Place Rail
        player.getInventory().selectedSlot = railSlot;
        interactionManager.interactBlock(player, Hand.MAIN_HAND, blockHit);

        // 2. Place TNT Minecart
        player.getInventory().selectedSlot = tntCartSlot;
        interactionManager.interactBlock(player, Hand.MAIN_HAND, blockHit);

        // 3. Shoot Bow
        if (bowSlot != -1) {
            player.getInventory().selectedSlot = bowSlot;
            interactionManager.interactItem(player, Hand.MAIN_HAND);
        }

        player.getInventory().selectedSlot = originalSlot;
    }

    private int findHotbarItem(ClientPlayerEntity player, Item targetItem) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() == targetItem) return i;
        }
        return -1;
    }
}
