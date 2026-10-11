package dev.antonfischle.auctionstreamermod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class AuctionManager {
    private static boolean active = false;
    private static long endTimeMillis = 0;
    private static long total = 0;
    private static int payments = 0;
    private static String itemName = "Item not selected";
    private static final Map<String, Long> totalsByPlayer = new LinkedHashMap<>();

    private AuctionManager() {}

    public static void start(int seconds, String item) {
        if (active) {
            AuctionStreamerMod.localMessage("An auction is already running. Stop it using the GUI first.");
            return;
        }
        int duration = Math.max(1, Math.min(3600, seconds));
        itemName = item == null || item.isBlank() ? "Item not selected" : item;
        total = 0;
        payments = 0;
        totalsByPlayer.clear();
        endTimeMillis = System.currentTimeMillis() + duration * 1000L;
        active = true;
        if (AuctionConfig.INSTANCE.announceStartAndEnd) {
            announce("Auction started for " + duration + " seconds! Item: " + itemName);
        }
    }

    public static void stop(boolean announceResult) {
        if (!active) {
            AuctionStreamerMod.localMessage("No auction is currently running.");
            return;
        }
        active = false;
        if (announceResult || AuctionConfig.INSTANCE.announceStartAndEnd) {
            announce("Auction ended. Total: " + total + " | Payments: " + payments
                    + " | Item: " + itemName);
            if (!totalsByPlayer.isEmpty()) {
                StringBuilder summary = new StringBuilder("Bidders: ");
                int shown = 0;
                for (Map.Entry<String, Long> entry : totalsByPlayer.entrySet()) {
                    if (shown++ > 0) summary.append(", ");
                    summary.append(entry.getKey()).append(" (").append(entry.getValue()).append(")");
                    if (shown >= 8) {
                        if (totalsByPlayer.size() > shown) summary.append(", ...");
                        break;
                    }
                }
                announce(summary.toString());
            }
        }
    }

    public static void reset() {
        active = false;
        total = 0;
        payments = 0;
        endTimeMillis = 0;
        itemName = "Item not selected";
        totalsByPlayer.clear();
        AuctionStreamerMod.localMessage("Auction data reset.");
    }

    public static void showStatus() {
        long remaining = active ? Math.max(0, (endTimeMillis - System.currentTimeMillis() + 999) / 1000) : 0;
        AuctionStreamerMod.localMessage((active ? "RUNNING" : "IDLE") + " | Item: " + itemName
                + " | Time left: " + remaining + "s | Total: " + total
                + " | Payments: " + payments);
    }

    public static void tick() {
        if (active && System.currentTimeMillis() >= endTimeMillis) {
            stop(true);
        }
    }

    public static void onChat(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) return;
        if (AuctionConfig.INSTANCE.countOnlyWhenAuctionActive && !active) return;

        try {
            Pattern pattern = Pattern.compile(AuctionConfig.INSTANCE.paymentRegex);
            Matcher matcher = pattern.matcher(rawMessage);
            if (!matcher.matches() || matcher.groupCount() < 2) return;

            String amountText = matcher.group(1).replaceAll("[^0-9]", "");
            if (amountText.isEmpty()) return;
            long amount = Long.parseLong(amountText);
            if (amount < AuctionConfig.INSTANCE.minimumPayment || amount <= 0) return;

            String player = matcher.group(2).trim();
            if (player.isEmpty()) player = "Unknown";
            total = Math.addExact(total, amount);
            payments++;
            totalsByPlayer.merge(player, amount, Math::addExact);
            AuctionStreamerMod.localMessage("Payment counted: +" + amount + " from " + player
                    + " | total " + total + " (" + payments + " payments)");
        } catch (PatternSyntaxException exception) {
            // Keep the game running if a user enters an invalid regular expression.
        } catch (NumberFormatException | ArithmeticException exception) {
            AuctionStreamerMod.localMessage("Payment ignored: amount was invalid or too large.");
        }
    }

    public static boolean isActive() { return active; }
    public static long getTotal() { return total; }
    public static int getPayments() { return payments; }
    public static String getItemName() { return itemName; }
    public static long getSecondsRemaining() {
        return active ? Math.max(0, (endTimeMillis - System.currentTimeMillis() + 999) / 1000) : 0;
    }

    private static void announce(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(Text.literal("[Auction] " + message));
        }
    }
}
