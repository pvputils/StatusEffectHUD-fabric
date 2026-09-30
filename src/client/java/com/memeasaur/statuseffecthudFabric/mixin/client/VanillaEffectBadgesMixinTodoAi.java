package com.memeasaur.statuseffecthudFabric.mixin.client;

import com.memeasaur.statuseffecthudFabric.client.StatuseffecthudFabricClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class VanillaEffectBadgesMixinTodoAi {
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void statuseffecthud$hideVanillaEffectBadges(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (StatuseffecthudFabricClient.hideVanillaEffectBadges()) ci.cancel();
    }
}
