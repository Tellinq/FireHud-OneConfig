package dev.tellinq.firehud.client.mixin.feature.lavafog;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
//#if MC >= 1.21.2
import net.minecraft.client.render.Fog;
//#endif
import net.minecraft.entity.Entity;
//#if MC >= 1.21.2
import org.joml.Vector4f;
//#endif
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
//#if MC <= 1.21.1
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#elseif MC >= 1.21.2
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

@Mixin(BackgroundRenderer.class)
public class Mixin_BackgroundRenderer_LavaFog {

    @Unique
    private static float capturedViewDistance;

    @Inject(method = "applyFog", at = @At("HEAD"))
    private static void fireHud$captureViewDistance(Camera camera,
                                            BackgroundRenderer.FogType fogType,
                                            //#if MC >= 1.21.2
                                            Vector4f color,
                                            //#endif
                                            float viewDistance, boolean thickenFog, float tickDelta,
                                            //#if MC >= 1.21.2
                                            CallbackInfoReturnable<Fog> cir
                                            //#elseif MC <= 1.21.1
                                            //$$ CallbackInfo ci
                                            //#endif
    ) {
        capturedViewDistance = viewDistance;
    }

    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSpectator()Z"))
    private static boolean fireHud$replaceSpectatorConditionWithCustom(Entity entity) {
        return FireHudConfig.Lava.renderLavaFog == 1;
    }

    @ModifyExpressionValue(
            method = "applyFog",
            at = @At(value = "CONSTANT", args = "floatValue=0.5", ordinal = 0)
    )
    private static float fireHud$viewDistFogEndFix(float original) {
        if (FireHudConfig.Lava.renderLavaFog == 1) {
            return FireHudConfig.Lava.distance / capturedViewDistance;
        }
        return original;
    }

    @ModifyExpressionValue(method = "applyFog", at = @At(value = "CONSTANT", args = "floatValue=-8.0", ordinal = 0))
    private static float fireHud$viewDistFogStartFix(float original) {
        if (FireHudConfig.Lava.renderLavaFog == 1) {
            return 0.0f;
        }
        return original;
    }

    @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSpectator()Z", ordinal = 0), cancellable = true)
    private static void fireHud$disableFog(Camera camera,
                                 BackgroundRenderer.FogType fogType,
                                 //#if MC >= 1.21.2
                                 Vector4f color,
                                 //#endif
                                 float viewDistance, boolean thickenFog, float tickDelta,
                                 //#if MC >= 1.21.2
                                 CallbackInfoReturnable<Fog> cir
                                 //#elseif MC <= 1.21.1
                                 //$$ CallbackInfo ci
                                 //#endif
    ) {
        if (FireHudConfig.Lava.renderLavaFog == 2) {
            //#if MC >= 1.21.2
            cir.setReturnValue(Fog.DUMMY);
            //#elseif MC <= 1.21.1
            //$$ ci.cancel();
            //#endif
        }
    }
}
