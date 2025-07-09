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

    @Inject(method =
            //#if MC > 1.17.1
            "applyFog"
            //#else
            //$$ "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V"
            //#endif
            , at = @At("HEAD"))
    private static void captureViewDistance(Camera camera,
                                            BackgroundRenderer.FogType fogType,
                                            //#if MC >= 1.21.2
                                            Vector4f color,
                                            //#endif
                                            float viewDistance,
                                            boolean thickenFog,
                                            //#if MC >= 1.19
                                            float tickDelta,
                                            //#endif
                                            //#if MC >= 1.21.2
                                            CallbackInfoReturnable<Fog> cir
                                            //#elseif MC <= 1.21.1
                                            //$$ CallbackInfo ci
                                            //#endif
    ) {
        capturedViewDistance = viewDistance;
    }

    @Redirect(method =
            //#if MC > 1.17.1
            "applyFog"
            //#else
            //$$ "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V"
            //#endif
            , at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSpectator()Z"))
    private static boolean applyFog(Entity entity) {
        return FireHudConfig.Lava.renderLavaFog == 1;
    }

    @ModifyExpressionValue(
            method =
                    //#if MC > 1.17.1
                    "applyFog"
            //#else
            //$$ "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V"
            //#endif
            ,
            at = @At(value = "CONSTANT", args = "floatValue=0.5", ordinal = 0)
    )
    private static float viewDistFogEndFix(float original) {
        if (FireHudConfig.Lava.renderLavaFog == 1) {
            return FireHudConfig.Lava.distance / capturedViewDistance;
        }
        return original;
    }

    @ModifyExpressionValue(method =
            //#if MC > 1.17.1
            "applyFog"
            //#else
            //$$ "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V"
            //#endif
            , at = @At(value = "CONSTANT", args = "floatValue=-8.0", ordinal = 0))
    private static float viewDistFogStartFix(float original) {
        if (FireHudConfig.Lava.renderLavaFog == 1) {
            return 0.0f;
        }
        return original;
    }

    @Inject(method =
            //#if MC > 1.17.1
            "applyFog"
            //#else
            //$$ "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V"
            //#endif
            , at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSpectator()Z", ordinal = 0), cancellable = true)
    private static void applyFog(Camera camera,
                                 BackgroundRenderer.FogType fogType,
                                 //#if MC >= 1.21.2
                                 Vector4f color,
                                 //#endif
                                 float viewDistance,
                                 boolean thickenFog,
                                 //#if MC >= 1.19
                                 float tickDelta,
                                 //#endif
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
