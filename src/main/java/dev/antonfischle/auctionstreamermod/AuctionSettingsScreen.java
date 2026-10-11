package dev.antonfischle.auctionstreamermod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class AuctionSettingsScreen extends Screen {
    private TextFieldWidget durationField;
    private TextFieldWidget minimumPaymentField;
    private TextFieldWidget paymentRegexField;

    private boolean showHud;
    private boolean announceStartAndEnd;
    private boolean countOnlyWhenAuctionActive;

    private ButtonWidget hudButton;
    private ButtonWidget announcementsButton;
    private ButtonWidget activeOnlyButton;

    public AuctionSettingsScreen() {
        super(Text.translatable("screen.auctionstreamermod.settings"));
    }

    @Override
    protected void init() {
        AuctionConfig config = AuctionConfig.INSTANCE;
        showHud = config.showHud;
        announceStartAndEnd = config.announceStartAndEnd;
        countOnlyWhenAuctionActive = config.countOnlyWhenAuctionActive;

        int fieldWidth = Math.min(300, this.width - 24);
        int left = (this.width - fieldWidth) / 2;

        durationField = new TextFieldWidget(textRenderer, left, 52, fieldWidth, 20,
                Text.translatable("screen.auctionstreamermod.duration"));
        durationField.setText(Integer.toString(config.durationSeconds));
        durationField.setMaxLength(4);
        addDrawableChild(durationField);

        minimumPaymentField = new TextFieldWidget(textRenderer, left, 91, fieldWidth, 20,
                Text.translatable("screen.auctionstreamermod.minimum_payment"));
        minimumPaymentField.setText(Long.toString(config.minimumPayment));
        minimumPaymentField.setMaxLength(19);
        addDrawableChild(minimumPaymentField);

        paymentRegexField = new TextFieldWidget(textRenderer, left, 130, fieldWidth, 20,
                Text.translatable("screen.auctionstreamermod.payment_regex"));
        paymentRegexField.setText(config.paymentRegex);
        paymentRegexField.setMaxLength(1000);
        addDrawableChild(paymentRegexField);

        int toggleWidth = Math.min(104, (this.width - 28) / 3);
        int toggleGap = 4;
        int totalToggleWidth = toggleWidth * 3 + toggleGap * 2;
        int toggleLeft = (this.width - totalToggleWidth) / 2;
        hudButton = addDrawableChild(ButtonWidget.builder(toggleLabel("HUD", showHud), button -> {
            showHud = !showHud;
            button.setMessage(toggleLabel("HUD", showHud));
        }).dimensions(toggleLeft, 163, toggleWidth, 20).build());

        announcementsButton = addDrawableChild(ButtonWidget.builder(toggleLabel("Chat", announceStartAndEnd), button -> {
            announceStartAndEnd = !announceStartAndEnd;
            button.setMessage(toggleLabel("Chat", announceStartAndEnd));
        }).dimensions(toggleLeft + toggleWidth + toggleGap, 163, toggleWidth, 20).build());

        activeOnlyButton = addDrawableChild(ButtonWidget.builder(toggleLabel("Nur Auktion", countOnlyWhenAuctionActive), button -> {
            countOnlyWhenAuctionActive = !countOnlyWhenAuctionActive;
            button.setMessage(toggleLabel("Nur Auktion", countOnlyWhenAuctionActive));
        }).dimensions(toggleLeft + (toggleWidth + toggleGap) * 2, 163, toggleWidth, 20).build());

        int buttonWidth = Math.min(140, (this.width - 36) / 2);
        int buttonGap = 8;
        int buttonsLeft = (this.width - buttonWidth * 2 - buttonGap) / 2;
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.auctionstreamermod.save"), button -> saveSettings())
                .dimensions(buttonsLeft, 198, buttonWidth, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.auctionstreamermod.cancel"), button -> close())
                .dimensions(buttonsLeft + buttonWidth + buttonGap, 198, buttonWidth, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("screen.auctionstreamermod.duration_label"), (width - Math.min(300, width - 24)) / 2, 40, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("screen.auctionstreamermod.minimum_payment_label"), (width - Math.min(300, width - 24)) / 2, 79, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("screen.auctionstreamermod.regex_label"), (width - Math.min(300, width - 24)) / 2, 118, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.auctionstreamermod.toggle_hint"), width / 2, 151, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    private void saveSettings() {
        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            long minimumPayment = Long.parseLong(minimumPaymentField.getText().trim());
            if (duration < 1 || duration > 3600 || minimumPayment < 0) {
                showError("Duration must be 1-3600 seconds; minimum payment must be 0 or more.");
                return;
            }

            String regex = paymentRegexField.getText();
            Pattern.compile(regex);
            AuctionConfig.INSTANCE.durationSeconds = duration;
            AuctionConfig.INSTANCE.minimumPayment = minimumPayment;
            AuctionConfig.INSTANCE.paymentRegex = regex;
            AuctionConfig.INSTANCE.showHud = showHud;
            AuctionConfig.INSTANCE.announceStartAndEnd = announceStartAndEnd;
            AuctionConfig.INSTANCE.countOnlyWhenAuctionActive = countOnlyWhenAuctionActive;
            AuctionConfig.save();
            AuctionStreamerMod.localMessage("Settings saved.");
            close();
        } catch (NumberFormatException exception) {
            showError("Enter valid numbers for duration and minimum payment.");
        } catch (PatternSyntaxException exception) {
            showError("The payment regex is invalid: " + exception.getDescription());
        }
    }

    private void showError(String message) {
        AuctionStreamerMod.localMessage("Settings not saved: " + message);
    }

    private static Text toggleLabel(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(null);
    }
}
