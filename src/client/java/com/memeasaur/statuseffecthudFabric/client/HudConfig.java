package com.memeasaur.statuseffecthudFabric.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class HudConfig {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("statuseffecthud.json"); }
    public boolean enabled = true;
    public String alignMode = "middleright";
    public boolean disableInventoryEffectList = true;
    public boolean enableBackground = false;
    public boolean enableEffectName = true;
    public boolean enableIconBlink = true;
    public int durationBlinkSeconds = 10;
    public String effectNameColor = "f";
    public String durationColor = "f";
    public int xOffset = 2;
    public int yOffset = 2;
    public int yOffsetBottomCenter = 41;
    public boolean applyXOffsetToCenter = false;
    public boolean applyYOffsetToMiddle = false;
    public boolean showInChat = true;

    void validate() {
        if (!Arrays.asList("topleft", "topcenter", "topright", "middleleft", "middlecenter", "middleright", "bottomleft", "bottomcenter", "bottomright").contains(alignMode))
            throw new IllegalArgumentException("Invalid alignment; use e.g. middleright or bottomleft");
        if (durationBlinkSeconds < -1 || durationBlinkSeconds > 60)
            throw new IllegalArgumentException("Blink seconds must be between -1 and 60");
        for (String color : new String[]{effectNameColor, durationColor})
            if (color == null || !color.matches("[0-9a-fA-F]")) throw new IllegalArgumentException("Colors must be a single code: 0-9 or a-f");
    }
    static HudConfig load() throws IOException {
        Path PATH = path();
        if (!Files.exists(PATH)) { HudConfig config = new HudConfig(); config.save(); return config; }
        HudConfig config = GSON.fromJson(Files.readString(PATH), HudConfig.class);
        if (config == null) throw new IllegalArgumentException("Config is empty");
        config.validate();
        return config;
    }
    void save() throws IOException {
        Path PATH = path();
        validate();
        Files.createDirectories(PATH.getParent());
        Path temporary = PATH.resolveSibling(PATH.getFileName() + ".tmp");
        Files.writeString(temporary, GSON.toJson(this));
        Files.move(temporary, PATH, StandardCopyOption.REPLACE_EXISTING);
    }
    HudConfig copy() { return GSON.fromJson(GSON.toJson(this), HudConfig.class); }
}
