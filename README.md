# Auction Streamer Mod

A **client-side Fabric mod** for Minecraft Java Edition that runs timed streamer auctions and counts payment-confirmation messages in chat.

## Download

**Minecraft Java Edition 1.21.11**

[![Download for Minecraft 1.21.11](https://img.shields.io/badge/Download-Minecraft%201.21.11-blue?style=for-the-badge)](https://github.com/antonfischle5/auction-streamer-mod/releases/latest/download/auction-streamer-mod-1.21.11.jar)

Click the download button to download the mod .jar file. You need Minecraft Java Edition **1.21.11**, Fabric Loader, Fabric API, and Java 21. The button will work after the first successful automatic build and release completes.

## Installation

1. Download the .jar file using the button above.
2. Make sure Minecraft Java **1.21.11** and Fabric Loader are installed.
3. Install Fabric API for Minecraft 1.21.11.
4. Put the downloaded .jar file into your Minecraft mods folder.
5. Start the Fabric profile in the Minecraft Launcher.

## Features

- Open an in-game settings GUI with a configurable keybind (default: **O**)
- Change the auction duration, minimum payment, payment chat regex, HUD, announcements and active-auction-only counting in the GUI
- Start, stop and reset auctions directly in the GUI—no commands required
- Start a timed auction (default: 60 seconds)
- Aim at an item frame holding an item before starting to use that item's name in the auction summary
- HUD with remaining time, item name, total counted and payment count
- Counts matching chat confirmations only while an auction is active by default
- Totals grouped by payer/player name
- End summary and bidder totals in local chat

## Settings GUI and keybind

- Press **O** by default to open the settings screen.
- Change the key under **Options → Controls → Key Binds → Auction Streamer Mod → Open Auction Settings**.
- In the GUI you can configure auction duration (1–3600 seconds), minimum payment, the payment-message regex, HUD visibility, start/end announcements and whether payments are counted only during an active auction.
- Click **Save** to store the settings in config/auction-streamer-mod.json; invalid numbers or regex patterns are rejected.

## Use the mod without commands

Open the GUI with the configurable keybind (default: **O**). Use the **Start**, **Stop** and **Reset** buttons in the same window to control auctions. The Start button uses the configured duration and the item in the item frame you are looking at. No chat commands are registered by this mod.

The mod does not send payment commands or move money. It only reads payment-confirmation messages visible to your Minecraft client.

## Configure payment detection

Launch the game once with the mod. It creates config/auction-streamer-mod.json.

The default regex is configured for the HugoSMP-style message: "Du hast $1 von .FPAnton erhalten." Regex capture groups must be:
1. **Group 1:** amount
2. **Group 2:** payer/player name (including dots)

If your server uses a different message, change the regex in the GUI. Test carefully: a too-broad pattern could count unrelated messages. This mod cannot inspect server-side economy transactions that never appear in your chat.

## Build

The repository includes a GitHub Actions workflow that builds the mod for Minecraft 1.21.11 and publishes the JAR as a GitHub Release whenever changes are pushed to main.

For manual builds, use JDK 21 and Gradle 8.14.3, then run gradle build. The JAR is generated in build/libs/.

## Important limitations

- This is a client-only mod; it cannot verify payments against the server economy database.
- Payment detection depends on the exact chat confirmation text and your regex configuration.
- The displayed item name is captured when you aim at an item frame and start the auction; it does not lock the item or prevent anyone from changing it.
- A download is published only when the automated build succeeds.

## Restore point

The previous repository state is preserved on the branch backup-before-emptying-2026-10-11.
