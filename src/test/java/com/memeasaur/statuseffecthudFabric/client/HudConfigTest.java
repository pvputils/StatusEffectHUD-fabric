package com.memeasaur.statuseffecthudFabric.client;

public final class HudConfigTest {
    public static void main(String[] args) {
        HudConfig config = new HudConfig();
        config.validate();
        check(config.enabled && config.alignMode.equals("middleright") && config.showInChat && config.disableInventoryEffectList, "Original defaults");
        // codex start
        check(config.textScalePercent == 100 && config.disableVanillaEffectBadges, "New defaults");
        config.textScalePercent = 150;
        check(config.copy().textScalePercent == 150, "Text scale JSON round trip");
        for (int invalidScale : new int[]{49, 201}) {
            HudConfig badScale = new HudConfig(); badScale.textScalePercent = invalidScale; rejects(badScale);
        }
        //codex end
        config.xOffset = -50;
        config.durationBlinkSeconds = -1;
        HudConfig draft = config.copy();
        check(draft.xOffset == -50 && draft.durationBlinkSeconds == -1, "JSON round trip");
        draft.xOffset = 20;
        check(config.xOffset == -50, "Draft isolation");
        for (String color : new String[]{null, "", "ff", "z"}) {
            HudConfig bad = new HudConfig(); bad.durationColor = color; rejects(bad);
        }
        HudConfig bad = new HudConfig(); bad.alignMode = null; rejects(bad);
        bad = new HudConfig(); bad.durationBlinkSeconds = 61; rejects(bad);
        bad = new HudConfig(); bad.durationBlinkSeconds = -2; rejects(bad);
        check(StatusHud.blinkVisible(195, 200, false, 10), "Short effects never blink");
        check(!StatusHud.blinkVisible(195, 600, false, 10), "Long effect expiry off phase");
        check(StatusHud.blinkVisible(185, 600, false, 10), "Long effect expiry on phase");
        check(StatusHud.blinkVisible(195, 600, false, -1), "Blink disabled");
        check(StatusHud.blinkVisible(-1, 600, true, 10), "Infinite effects never blink");
        check(StatusHud.blinkVisible(415, 600, false, 10), "Outside blink threshold");
        System.out.println("StatusEffectHUD configuration and blinking checks passed");
    }
    private static void rejects(HudConfig config) {
        try { config.validate(); } catch (IllegalArgumentException expected) { return; }
        throw new AssertionError("Invalid config accepted");
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
