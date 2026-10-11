package dev.antonfischle.auctionstreamermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AuctionConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("auction-streamer-mod.json");

    public static AuctionConfig INSTANCE = new AuctionConfig();

    public int durationSeconds = 60;
    public long minimumPayment = 1;
    public boolean showHud = true;
    public boolean announceStartAndEnd = true;
    public boolean countOnlyWhenAuctionActive = true;

    /*
     * Regex capture groups:
     *   group 1 = payment amount
     *   group 2 = payer/player name
     *
     * Supports HugoSMP messages such as:
     * "Du hast $1 von .FPAnton erhalten."
     */
    public String paymentRegex = "(?i).*?hast\\s+\\$?([0-9][0-9,._]*)\\s+von\\s+([A-Za-z0-9_.]{1,32})\\s+erhalten.*";

    private AuctionConfig() {}

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                AuctionConfig loaded = GSON.fromJson(Files.readString(FILE), AuctionConfig.class);
                if (loaded != null) INSTANCE = loaded;
            } else {
                save();
            }
        } catch (Exception exception) {
            System.err.println("[Auction Streamer Mod] Could not read config: " + exception.getMessage());
            INSTANCE = new AuctionConfig();
            save();
        }
        INSTANCE.durationSeconds = Math.max(1, Math.min(3600, INSTANCE.durationSeconds));
        INSTANCE.minimumPayment = Math.max(0, INSTANCE.minimumPayment);
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(INSTANCE));
        } catch (IOException exception) {
            System.err.println("[Auction Streamer Mod] Could not save config: " + exception.getMessage());
        }
    }
}