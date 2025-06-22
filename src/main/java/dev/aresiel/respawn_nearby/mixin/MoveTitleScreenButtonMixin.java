package dev.aresiel.respawn_nearby.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DeathScreen.class)
public class MoveTitleScreenButtonMixin {
    @ModifyExpressionValue(
            method = "init",
            at = @At(value = "CONSTANT", args = "intValue=96")
    )
    private int moveButtonY(int original) {
        return 120;
    }
}

