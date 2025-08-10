package dev.tellinq.firehud.client.mixin.feature.fireoverlay;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.deftu.omnicore.client.OmniClient;
import dev.deftu.omnicore.client.OmniClientPlayer;
import dev.deftu.omnicore.client.render.OmniMatrixStack;
import dev.deftu.omnicore.client.render.pipeline.DrawModes;
import dev.deftu.omnicore.client.render.pipeline.OmniRenderPipeline;
import dev.deftu.omnicore.client.render.pipeline.OmniRenderPipelineBuilder;
import dev.deftu.omnicore.client.render.pipeline.VertexFormats;
import dev.deftu.omnicore.client.render.state.OmniManagedBlendState;
import dev.deftu.omnicore.client.render.vertex.OmniBufferBuilder;
import dev.deftu.omnicore.common.OmniIdentifier;
import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

//#if MC >= 1.21.4
import net.minecraft.client.render.VertexConsumerProvider;
//#endif

//#if MC > 1.19.2
import net.minecraft.util.math.RotationAxis;
//#else
//$$ import net.minecraft.util.math.Vec3f;
//#endif

@Mixin(InGameOverlayRenderer.class)
public class Mixin_InGameOverlayRenderer_FirstPersonFireOverlay {
    @Unique private static final SpriteIdentifier SOUL_FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, OmniIdentifier.create("block/soul_fire_1"));
    @Unique private static OmniRenderPipeline pipeline;

    //#if MC >= 1.21.4
    @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderFireOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", shift = At.Shift.AFTER))
    private static void fireHud$renderSideFireHUD(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
    //#else
    //$$ @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    //$$ private static void fireHud$renderSideFireHUD(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
    //#endif
        if (FireHudConfig.FirstPersonFire.sideFire && fireHud$shouldRenderFire(client)){
            fireHud$renderSideFireOverlay(matrices);
        }
    }

    //#if MC >= 1.21.4
    @WrapWithCondition(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderFireOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private static boolean fireHud$shouldRenderFirstPersonFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
    //#elseif MC <= 1.21.3
    //$$ @WrapWithCondition(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V"))
    //$$ private static boolean fireHud$shouldRenderFirstPersonFire(MinecraftClient client, MatrixStack matrices) {
    //#endif
        if (!FireHudConfig.FirstPersonFire.enabled) {
            return false;
        }
        //#if MC >= 1.21.4
        MinecraftClient client = OmniClient.getInstance();
        //#endif
        return fireHud$shouldRenderFire(client);
    }

    @Unique
    private static boolean fireHud$shouldRenderFire(MinecraftClient client) {
        if (client.player != null) {
            if ((!FireHudConfig.FirstPersonFire.whenInLava && client.player.isInLava())) {
                return false;
            }
            if ((!FireHudConfig.FirstPersonFire.fireResistance && client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) {
                int duration = Objects.requireNonNull(client.player.getStatusEffect(StatusEffects.FIRE_RESISTANCE)).getDuration();
                return duration < 100;
            }
        }
        return true;
    }

    @ModifyArg(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"), index = 3)
    private static float fireHud$fireOpacity(float opacity) {
        float fireOpacity = FireHudConfig.FirstPersonFire.opacity / 100F;
        if (OmniClientPlayer.getInstance().hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            int duration = OmniClientPlayer.getInstance().getStatusEffect(StatusEffects.FIRE_RESISTANCE).getDuration();
            fireOpacity *= duration > 100 ? 1.0F : 0.5F - MathHelper.sin(((float)duration - 0) * (float)Math.PI * 0.2F) * 0.5F;
        }
        return fireOpacity;
    }


    @ModifyArg(
            method = "renderFireOverlay",
            at = @At(
                    value = "INVOKE",
                    //#if MC > 1.19.2
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"
                    //#else
                    //$$ target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"
                    //#endif
            ),
            index = 1
    )

    //#if MC > 1.19.2
    private static float fireHud$firePos(float y) {
    //#else
    //$$ private static double fireHud$firePos(double y) {
    //#endif
        return -1.0f + (FireHudConfig.FirstPersonFire.height / 100F);
    }

    @Redirect(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;"))
    private static Sprite fireHud$getSprite(SpriteIdentifier instance) {
        if (FireHudConfig.renderSoulFire && OmniClientPlayer.getInstance() != null && ((Accessor_SoulFireEntity) OmniClientPlayer.getInstance()).fireHud$isOnSoulFire()) {
            return SOUL_FIRE_1.getSprite();
        }

        return instance.getSprite();
    }


    @Unique
    private static void fireHud$renderSideFireOverlay(MatrixStack matrices) {
        Sprite sprite = fireHud$getSprite(ModelBaker.FIRE_1);

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float midU = (minU + maxU) / 2.0f;

        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        float midV = (minV + maxV) / 2.0f;

        float animationFrameDelta = sprite.getAnimationFrameDelta();

        float interpolatedU1 = MathHelper.lerp(animationFrameDelta, minU, midU);
        float interpolatedU2 = MathHelper.lerp(animationFrameDelta, maxU, midU);
        float interpolatedV1 = MathHelper.lerp(animationFrameDelta, minV, midV);
        float interpolatedV2 = MathHelper.lerp(animationFrameDelta, maxV, midV);

        float fireOpacity = fireHud$fireOpacity(0.9f);

        for (int r = 0; r < 2; ++r) {
            matrices.push();
            matrices.translate((float)(-(r * 2 - 1)) * 0.24f, fireHud$firePos(-0.3f), -0.2f); // y:-0.3f

            //#if MC > 1.19.2
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(r * 2 - 1) * 70.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((r == 1 ? -10.0f : 10.0f)));
            //#else
            //$$ matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0.0f));
            //$$ matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)(r * 2 - 1) * 70.0f));
            //$$ matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((r == 1 ? -10.0f : 10.0f)));
            //#endif

            OmniMatrixStack omniMatrixStack = OmniMatrixStack.vanilla(matrices);
            OmniBufferBuilder bufferBuilder = OmniBufferBuilder.create(DrawModes.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            bufferBuilder.vertex(omniMatrixStack, -0.5f, -0.5f, -0.5f).texture(interpolatedU2, interpolatedV2).color(1.0f, 1.0f, 1.0f, fireOpacity).next();
            bufferBuilder.vertex(omniMatrixStack, 0.5f, -0.5f, -0.5f).texture(interpolatedU1, interpolatedV2).color(1.0f, 1.0f, 1.0f, fireOpacity).next();
            bufferBuilder.vertex(omniMatrixStack, 0.5f, 0.5f, -0.5f).texture(interpolatedU1, interpolatedV1).color(1.0f, 1.0f, 1.0f, fireOpacity).next();
            bufferBuilder.vertex(omniMatrixStack, -0.5f, 0.5f, -0.5f).texture(interpolatedU2, interpolatedV1).color(1.0f, 1.0f, 1.0f, fireOpacity).next();
            bufferBuilder.build().drawWithCleanup(getPipeline(), f -> {});

            matrices.pop();
        }
    }

    @Unique
    private static OmniRenderPipeline getPipeline() {
        if (pipeline == null) {
            OmniRenderPipelineBuilder builder = OmniRenderPipeline.builderWithDefaultShader(
                    OmniIdentifier.create("block/soul_fire_1"),
                    VertexFormats.POSITION_TEXTURE_COLOR,
                    DrawModes.QUADS
            );
            builder.blendState = OmniManagedBlendState.NORMAL;

            pipeline = builder.build();
        }
        return pipeline;
    }

}
