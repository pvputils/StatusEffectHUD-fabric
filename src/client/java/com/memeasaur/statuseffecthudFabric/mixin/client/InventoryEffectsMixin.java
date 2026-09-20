package com.memeasaur.statuseffecthudFabric.mixin.client;

import com.memeasaur.statuseffecthudFabric.client.StatuseffecthudFabricClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectsInInventory.class)
public abstract class InventoryEffectsMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void statuseffecthud$hideEffects(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (StatuseffecthudFabricClient.hideInventoryEffects()) ci.cancel();
    }
}