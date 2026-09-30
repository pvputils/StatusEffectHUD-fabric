package com.memeasaur.statuseffecthudFabric.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import java.util.IdentityHashMap;
import java.util.Map;

final class StatusHud {
    private static final Map<MobEffectInstance, Integer> maximumDurations = new IdentityHashMap<>();
    private static Object player;
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("container/inventory/effect_background");

    // Track while menus are open too, and discard expired effects and old worlds.
    static void tick(Minecraft mc) {
        if (player != mc.player) { maximumDurations.clear(); player = mc.player; }
        if (mc.player == null) return;
        var active = mc.player.getActiveEffects();
        maximumDurations.keySet().removeIf(effect -> !active.contains(effect));
        for (var effect : active) maximumDurations.merge(effect, effect.getDuration(), Math::max);
    }

    static boolean blinkVisible(int duration, int maximum, boolean infinite, int threshold) {
        return infinite || maximum <= 400 || duration / 20 > threshold || duration % 20 < 10;
    }

    // codex start
    private static void drawScaledText(GuiGraphics graphics, Minecraft minecraft, String text, int x, int y, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0f);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(minecraft.font, text, 0, 0, 0xffffffff, true);
        graphics.pose().popPose();
    }
    //codex end

    static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        HudConfig c = StatuseffecthudFabricClient.config;
        if (!c.enabled || mc.player == null || mc.level == null || mc.options.hideGui
                || mc.getDebugOverlay().showDebugScreen()
                || (mc.screen != null && !(mc.screen instanceof ChatScreen && c.showInChat))) return;
        var effects = mc.player.getActiveEffects().stream().filter(MobEffectInstance::showIcon).sorted().toList();
        float textScale = c.textScalePercent / 100.0f; //codex (int spacing = c.enableBackground ? 33 : c.enableEffectName ? 20 : 18;)
        int spacing = c.enableBackground ? 33 : c.enableEffectName ? Math.max(20, (int) Math.ceil(20 * textScale)) : Math.max(18, (int) Math.ceil(18 * textScale)); //codex (int spacing = c.enableBackground ? 33 : c.enableEffectName ? 20 : 18;)
        if (effects.size() > 5 && c.enableBackground) spacing = Math.max(1, 132 / (effects.size() - 1));
        int height = effects.isEmpty() ? 0 : (effects.size() - 1) * spacing + (c.enableBackground ? 32 : 18);
        int y = c.alignMode.startsWith("middle") ? (graphics.guiHeight() - height) / 2 + (c.applyYOffsetToMiddle ? c.yOffset : 0)
                : c.alignMode.startsWith("bottom") ? graphics.guiHeight() - height - (c.alignMode.equals("bottomcenter") ? c.yOffsetBottomCenter : c.yOffset) : c.yOffset;
        boolean right = c.alignMode.endsWith("right");
        for (var effect : effects) {
            String name = c.enableEffectName ? Component.translatable(effect.getEffect().value().getDescriptionId()).getString() : "";
            if (c.enableEffectName && effect.getAmplifier() > 0) {
                int level = effect.getAmplifier() + 1;
                name += " " + (level <= 10 ? new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"}[level - 1] : level);
            }
            String duration = MobEffectUtil.formatDuration(effect, 1.0f, mc.level.tickRateManager().tickrate()).getString();
            int width = Math.max(c.enableBackground ? 140 : 0, 22 + (int) Math.ceil(Math.max(mc.font.width(name), mc.font.width(duration)) * textScale) + (c.enableBackground ? 12 : 0)); //codex (int width = Math.max(c.enableBackground ? 140 : 0, 22 + Math.max(mc.font.width(name), mc.font.width(duration)) + (c.enableBackground ? 12 : 0));)
            int x = c.alignMode.endsWith("center") ? (graphics.guiWidth() - width) / 2 + (c.applyXOffsetToCenter ? c.xOffset : 0)
                    : right ? graphics.guiWidth() - width - c.xOffset : c.xOffset;
            int pad = c.enableBackground ? 6 : 0;
            int iconX = right ? x + width - pad - 18 : x + pad;
            int iconY = y + (c.enableBackground ? 7 : 0);
            int textY = y + pad;
            if (c.enableBackground) graphics.blitSprite(RenderType::guiTextured, BACKGROUND, x, y, width, 32);
            boolean visible = blinkVisible(effect.getDuration(), maximumDurations.getOrDefault(effect, effect.getDuration()), effect.isInfiniteDuration(), c.durationBlinkSeconds);
            if (!c.enableIconBlink || visible)
                graphics.blitSprite(RenderType::guiTextured, mc.getMobEffectTextures().get(effect.getEffect()), iconX, iconY, 18, 18);
            int textX = right ? iconX - 4 - (int) Math.ceil(mc.font.width(name) * textScale) : iconX + 22; //codex (int textX = right ? iconX - 4 - mc.font.width(name) : iconX + 22;)
            // codex start
            drawScaledText(graphics, mc, "\u00a7" + c.effectNameColor + name, textX, textY, textScale); //codex (graphics.drawString(mc.font, "\u00a7" + c.effectNameColor + name, textX, textY, 0xffffffff, true);)
            //codex end
            // codex start
            if (visible) drawScaledText(graphics, mc, "\u00a7" + c.durationColor + duration,
                    right ? iconX - 4 - (int) Math.ceil(mc.font.width(duration) * textScale) : iconX + 22,
                    textY + (int) Math.ceil((c.enableEffectName ? 10 : 5) * textScale), textScale); //codex (if (visible) graphics.drawString(mc.font, "\u00a7" + c.durationColor + duration, right ? iconX - 4 - mc.font.width(duration) : iconX + 22, textY + (c.enableEffectName ? 10 : 5), 0xffffffff, true);)
            //codex end
            y += spacing;
        }
    }
}
