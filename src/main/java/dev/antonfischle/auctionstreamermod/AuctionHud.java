package dev.antonfischle.auctionstreamermod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class AuctionHud {
    private AuctionHud() {}

    public static void render(DrawContext context) {
        if (!AuctionConfig.INSTANCE.showHud || !AuctionManager.isActive()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer == null) return;

        int x = 8;
        int y = 8;
        context.drawText(client.textRenderer, "STREAMER AUCTION", x, y, 0xFFFFFF, true);
        context.drawText(client.textRenderer, "Item: " + AuctionManager.getItemName(), x, y + 12, 0xFFFF55, true);
        context.drawText(client.textRenderer, "Time: " + AuctionManager.getSecondsRemaining() + "s", x, y + 24, 0x55FF55, true);
        context.drawText(client.textRenderer, "Total: " + AuctionManager.getTotal(), x, y + 36, 0x55FFFF, true);
        context.drawText(client.textRenderer, "Payments: " + AuctionManager.getPayments(), x, y + 48, 0xFFFFFF, true);
    }
}
