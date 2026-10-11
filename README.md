# Auction Streamer Mod

A **client-side Fabric mod** for Minecraft Java Edition that runs timed streamer auctions and counts payment-confirmation messages in chat.

## Current target

- Minecraft Java Edition **1.21.1**
- Fabric Loader
- Java 21
- Fabric API

You said you were unsure which Minecraft version you use, so this first version targets 1.21.1 as a starting point. The mod will need a version update if your game uses a different release.

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
- Config file for duration, minimum payment, HUD visibility and a server-specific payment-message regex

## Settings GUI and keybind

- Press **O** by default to open the settings screen.
- Change the key under **Options → Controls → Key Binds → Auction Streamer Mod → Open Auction Settings**. Minecraft lets you assign another key there.
- In the GUI you can configure auction duration (1–3600 seconds), minimum payment, the payment-message regex, HUD visibility, start/end announcements and whether payments are counted only during an active auction.
- Click **Save** to store the settings in `config/auction-streamer-mod.json`; invalid numbers or regex patterns are rejected.

## Use the mod without commands

Open the GUI with the configurable keybind (default: **O**). Use the **Start**, **Stop** and **Reset** buttons in the same window to control auctions. The Start button uses the configured duration and the item in the item frame you are looking at. No chat commands are registered by this mod.

The mod does not send payment commands or move money. It only reads payment-confirmation messages visible to your Minecraft client.

## Configure payment detection

Launch the game once with the mod. It creates:

`config/auction-streamer-mod.json`

The default regex is only an example and will not match every server. Change `paymentRegex` to the exact confirmation format your server prints.

Regex capture groups must be:
1. **Group 1:** amount (digits, optionally with separators)
2. **Group 2:** payer/player name

For example, if the server prints `You received 250 coins from Steve`, this pattern matches it:

```regex
(?i).*?(?:received|got|erhalten|bekommen)\s+\$?([0-9][0-9,._]*)\s*(?:coins?|dollars?|geld)?\s+(?:from|von)\s+([A-Za-z0-9_]{1,16}).*
```

If your server uses a different message (for example `Steve paid you $250`), change the regex to match that exact format and keep the amount in group 1 and the payer in group 2. Test carefully: a too-broad pattern could count unrelated messages. This mod cannot inspect server-side economy transactions that never appear in your chat.

## Build

Install JDK 21 and Gradle, clone the repository, then run:

- Windows: `gradle build`
- macOS/Linux: `gradle build`

The mod jar will be in `build/libs/`.

## Important limitations

- This is a client-only first version; it cannot verify payments against the server economy database.
- Payment detection depends on the exact chat confirmation text and your regex configuration.
- The displayed item name is captured when you aim at an item frame and start the auction; it does not lock the item or prevent anyone from changing it.
- The mod has not yet been tested against your specific server or Minecraft installation.

## Restore point

The previous repository state is preserved on the branch `backup-before-emptying-2026-10-11`.
