package dev.tellinq.firehud.client.mixin.feature.firetint;

import dev.deftu.omnicore.api.client.OmniClient;
import dev.deftu.omnicore.api.client.options.OmniPerspective;
import dev.deftu.omnicore.api.client.render.OmniResolution;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC <= 1.21.1
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif

//#if MC >= 1.21
import net.minecraft.client.render.RenderTickCounter;
//#endif

//#if MC <= 1.19.4
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(InGameHud.class)
public class Mixin_InGameHud_FireTint {
    @Inject(
            //#if MC >= 1.20.5
            method = "renderMiscOverlays",
            //#else
            //$$ method = "render",
            //#endif
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    private void fireHud$render(
            //#if MC <= 1.19.4
            //$$ MatrixStack matrices,
            //#else
            DrawContext context,
            //#endif
            //#if MC >= 1.21
            RenderTickCounter tickCounter,
            //#else
            //$$ float tickDelta,
            //#endif
            CallbackInfo ci) {
        PlayerEntity player = OmniClient.getPlayer();

        if (player == null) return;

        int width = OmniResolution.getScaledWidth();
        int height = OmniResolution.getScaledHeight();

        boolean isFirstPerson = OmniPerspective.getCurrentPerspective().isFirstPerson();
        boolean isOnFire = player.isOnFire();
        boolean isInLava = player.isInLava();
        boolean hasFireResistance = player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
        boolean showInLava = FireHudConfig.FirstPersonFire.whenInLava || !isInLava;
        boolean showWithFireRes = FireHudConfig.FirstPersonFire.fireResistance || !hasFireResistance;

        if (FireHudConfig.FireScreenTint.enabled && isOnFire && isFirstPerson && showInLava && showWithFireRes) {
                boolean isOnSoulFire = ((Accessor_SoulFireEntity) player).fireHud$isOnSoulFire() && FireHudConfig.renderSoulFire;

                //#if MC > 1.19.4
                context.fillGradient(0, 0, width, height,
                        isOnSoulFire ? FireHudConfig.FireScreenTint.soulTintStartColor.getArgb() : FireHudConfig.FireScreenTint.tintStartColor.getArgb(),
                        isOnSoulFire ? FireHudConfig.FireScreenTint.soulTintEndColor.getArgb() : FireHudConfig.FireScreenTint.tintEndColor.getArgb());
                //#endif
        }
    }
}
