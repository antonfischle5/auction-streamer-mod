package dev.antonfischle.auctionstreamermod;

import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;


public class AuctionStreamerMod implements ClientModInitializer {
    private static KeyBinding openSettingsKey;

    @Override
    public void onInitializeClient() {
        AuctionConfig.load();

        openSettingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auctionstreamermod.open_settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.auctionstreamermod"
        ));

        ClientReceiveMessageEvents.GAME.register((message, overlay) ->
                AuctionManager.onChat(message.getString()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AuctionManager.tick();
            while (openSettingsKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new AuctionSettingsScreen());
                }
            }
        });
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
                AuctionHud.render(drawContext));

        localMessage("Auction Streamer Mod loaded. Press O to open the GUI. No commands are needed.");
    }

    public static String findItemFrameName() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget instanceof EntityHitResult hit
                && hit.getEntity() instanceof ItemFrameEntity frame
                && !frame.getHeldItemStack().isEmpty()) {
            return frame.getHeldItemStack().getName().getString();
        }
        return "Item not selected";
    }

    static void localMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(Text.literal("[Auction] " + message));
        }
    }
}
