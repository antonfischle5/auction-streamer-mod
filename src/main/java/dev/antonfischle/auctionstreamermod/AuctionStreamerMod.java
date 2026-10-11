package dev.antonfischle.auctionstreamermod;

import com.mojang.blaze3d.platform.InputUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
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

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import com.mojang.brigadier.arguments.IntegerArgumentType;

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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("auction")
                        .then(ClientCommandManager.literal("start")
                                .executes(context -> {
                                    AuctionManager.start(AuctionConfig.INSTANCE.durationSeconds, findItemFrameName());
                                    return 1;
                                })
                                .then(argument("seconds", IntegerArgumentType.integer(1, 3600))
                                        .executes(context -> {
                                            AuctionManager.start(
                                                    IntegerArgumentType.getInteger(context, "seconds"),
                                                    findItemFrameName());
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("stop")
                                .executes(context -> {
                                    AuctionManager.stop(true);
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("reset")
                                .executes(context -> {
                                    AuctionManager.reset();
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("status")
                                .executes(context -> {
                                    AuctionManager.showStatus();
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("reload")
                                .executes(context -> {
                                    AuctionConfig.load();
                                    localMessage("Auction settings reloaded.");
                                    return 1;
                                }))));

        localMessage("Auction Streamer Mod loaded. Press O or change the key in Options > Controls.");
    }

    private static String findItemFrameName() {
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
