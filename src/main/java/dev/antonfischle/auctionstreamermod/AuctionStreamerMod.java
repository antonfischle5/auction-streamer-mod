package dev.antonfischle.auctionstreamermod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class AuctionStreamerMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AuctionConfig.load();
        ClientReceiveMessageEvents.GAME.register((message, overlay) ->
                AuctionManager.onChat(message.getString()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> AuctionManager.tick());
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

        localMessage("Auction Streamer Mod loaded. Use /auction start [seconds].");
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
