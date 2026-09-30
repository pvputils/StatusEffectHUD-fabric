package com.memeasaur.statuseffecthudFabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.network.chat.Component;
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class StatuseffecthudFabricClient implements ClientModInitializer {
    static HudConfig config = new HudConfig();
    private static boolean openConfig;
    public static boolean isEnabled() { return config.enabled; }
    public static boolean hideInventoryEffects() { return config.enabled && config.disableInventoryEffectList; }
    // codex start
    public static boolean hideVanillaEffectBadges() { return config.enabled && config.disableVanillaEffectBadges; }
    //codex end
    private static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }
    @Override public void onInitializeClient() {
        try { config = HudConfig.load(); }
        catch (Exception e) { LoggerFactory.getLogger("statuseffecthud").error("Cannot load statuseffecthud.json; using defaults without overwriting the file", e); }
        HudRenderCallback.EVENT.register((graphics, delta) -> StatusHud.render(graphics));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            StatusHud.tick(client);
            if (openConfig) { openConfig = false; client.setScreen(new HudConfigScreen()); }
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> dispatcher.register(literal("statuseffect")
            .executes(context -> { openConfig = true; return 1; })
            .then(literal("config").executes(context -> { openConfig = true; return 1; }))
            .then(literal("toggle").executes(context -> {
                HudConfig updated = config.copy(); updated.enabled = !updated.enabled;
                try { updated.save(); config = updated; context.getSource().sendFeedback(Component.literal("StatusEffectHUD " + (config.enabled ? "enabled" : "disabled"))); return 1; }
                catch (Exception e) { context.getSource().sendError(Component.literal("Could not save StatusEffectHUD config: " + e.getMessage())); return 0; }
            }))
            .then(literal("reload").executes(context -> {
                try { config = HudConfig.load(); context.getSource().sendFeedback(Component.literal("StatusEffectHUD config reloaded")); return 1; }
                catch (Exception e) { context.getSource().sendError(Component.literal("Could not reload config: " + e.getMessage())); return 0; }
            }))));
    }
}
