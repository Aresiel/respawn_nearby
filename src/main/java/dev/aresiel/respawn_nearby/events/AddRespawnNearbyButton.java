package dev.aresiel.respawn_nearby.events;

import dev.aresiel.respawn_nearby.RespawnNearby;
import dev.aresiel.respawn_nearby.config.ClientConfig;
import dev.aresiel.respawn_nearby.mixin.DeathScreenAccessor;
import dev.aresiel.respawn_nearby.mixin.ScreenInvokerAccessor;
import dev.aresiel.respawn_nearby.networking.UpdateStoredPreferencePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT, modid = RespawnNearby.MODID)
public class AddRespawnNearbyButton {

    private static Component getToggleComponent(boolean enabled, boolean text){
        Style enabledStyle = Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN);
        Style disabledStyle = Style.EMPTY.withBold(true).withColor(ChatFormatting.RED);

        var glyph = Component.literal(enabled ? "✔" : "✘").withStyle(enabled ? enabledStyle : disabledStyle);
        var textComponent = Component.translatable("respawn_nearby.respawn_button.toggle." + (enabled ? "enabled" : "disabled"))
                .withStyle(enabled ? enabledStyle : disabledStyle);

        return text ? textComponent : glyph;
    }

    private static Component getButtonComponent() {
        boolean enabled = ClientConfig.ENABLE_REPAWN_NEARBY.get();
        return Component.translatable("respawn_nearby.respawn_button").append(" [").append(getToggleComponent(enabled, false)).append("]");
    }

    private static Component getTooltipComponent() {
        boolean enabled = ClientConfig.ENABLE_REPAWN_NEARBY.get();
        return Component.translatable("respawn_nearby.respawn_button.tooltip")
                .append(getToggleComponent(enabled, true));
    }

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if(event.getScreen() instanceof DeathScreen deathScreen){
            var exitButtons = ((DeathScreenAccessor) deathScreen).getExitButtons();

            int width = ((ScreenInvokerAccessor) deathScreen).getWidth();
            int height = ((ScreenInvokerAccessor) deathScreen).getHeight();

            var button = Button.builder(getButtonComponent(), (btn) -> {
                        boolean enabled = ClientConfig.ENABLE_REPAWN_NEARBY.get();
                        ClientConfig.ENABLE_REPAWN_NEARBY.set(!enabled);
                        ClientConfig.ENABLE_REPAWN_NEARBY.save();
                        btn.setMessage(getButtonComponent());
                        btn.setTooltip(Tooltip.create(getTooltipComponent()));

                        PacketDistributor.sendToServer(new UpdateStoredPreferencePayload(ClientConfig.ENABLE_REPAWN_NEARBY.get()));
                    })
                    .bounds(width / 2 - 100, height / 4 + 96, 200, 20)
                    .tooltip(Tooltip.create(getTooltipComponent()))
                    .build();

            Button renderableButton = ((ScreenInvokerAccessor) deathScreen).invokeAddRenderableWidget(button);

            exitButtons.add(renderableButton);
            renderableButton.active = false;
        }
    }
}
