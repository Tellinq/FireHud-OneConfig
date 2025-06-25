package dev.tellinq.firehud.client.mixin;


//#if MC <= 1.21.3
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import net.minecraft.client.MinecraftClient;
//#if MC >= 1.21.2 && MC <= 1.21.3
//$$ import net.minecraft.client.gl.ShaderProgramKeys;
//#endif
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
//#if MC >= 1.21.4
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.RenderLayer;
//#endif
//#if MC <= 1.21.3
//$$ import net.minecraft.client.render.BufferBuilder;
//$$ import net.minecraft.client.render.VertexFormat;
//$$ import net.minecraft.client.render.VertexFormats;
//$$ import net.minecraft.client.render.Tessellator;
//$$ import net.minecraft.client.render.BufferRenderer;
//#endif
//#if MC <= 1.21.1
//$$ import net.minecraft.client.render.GameRenderer;
//#else
//$$
//#endif
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
//#if MC > 1.19.2
import org.joml.Matrix4f;
import net.minecraft.util.math.RotationAxis;
//#else
//$$ import net.minecraft.util.math.Matrix4f;
//$$ import net.minecraft.util.math.Vec3f;
//#endif
import dev.tellinq.firehud.client.FireHud;
import dev.tellinq.firehud.client.config.FireHudConfig;
import dev.tellinq.firehud.client.SoulFireEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Unique private static final SpriteIdentifier SOUL_FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, FireHud.getIdentifierOf("block/soul_fire_1"));

    @Inject(method = "renderOverlays", at = @At("TAIL"))
    private static void renderOverlays(MinecraftClient client, MatrixStack matrices,
                                       //#if MC >= 1.21.4
                                       VertexConsumerProvider vertexConsumers,
                                       //#endif
                                       CallbackInfo ci) {
        if (client.player != null && !client.player.isSpectator() && client.player.isOnFire() && FireHudConfig.FirstPersonFire.sideFire &&
                !(!FireHudConfig.FirstPersonFire.whenInLava && client.player.isInLava()) && !(!FireHudConfig.FirstPersonFire.fireResistance && client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) {
            renderSideFireOverlay(client, matrices
                    //#if MC >= 1.21.4
                    , vertexConsumers
                    //#endif
            );
        }
    }
    
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderVanillaHud(
            //#if MC <= 1.21.3
            //$$ MinecraftClient client,
            //#endif
            MatrixStack matrices,
            //#if MC >= 1.21.4
            VertexConsumerProvider vertexConsumers,
            //#endif
            CallbackInfo ci) {
        if (!FireHudConfig.FirstPersonFire.enabled) ci.cancel();
        //#if MC >= 1.21.4
        MinecraftClient client = MinecraftClient.getInstance();
        //#endif
        if (client.player != null && client.player.isOnFire()) {
            if ((!FireHudConfig.FirstPersonFire.whenInLava && client.player.isInLava())) ci.cancel();
            if ((!FireHudConfig.FirstPersonFire.fireResistance && client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) ci.cancel();
        }
    }
    
    @ModifyArg(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"), index = 3)
    private static float fireOpacity(float red) {
        return FireHudConfig.FirstPersonFire.opacity / 100F;
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
    private static float firePos(float y) {
    //#else
    //$$ private static double firePos(double y) {
    //#endif
        return -1.0f + (FireHudConfig.FirstPersonFire.height / 100F);
    }
    
    @Redirect(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;"))
    private static Sprite getSprite(SpriteIdentifier instance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (FireHudConfig.renderSoulFire && client.player != null && ((SoulFireEntityAccessor) client.player).fireHud$isOnSoulFire()) return SOUL_FIRE_1.getSprite();
        return instance.getSprite();
    }


    @Unique
    private static void renderSideFireOverlay(MinecraftClient client, MatrixStack matrices
                                              //#if MC >= 1.21.4
                                              , VertexConsumerProvider vertexConsumers
                                              //#endif
    ) {
        //#if MC > 1.19.2
        //#if MC < 1.21
        //$$  BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        //#endif
        //#if MC <= 1.21.3
        //$$  RenderSystem.setShader(
                //#if MC < 1.21
                //$$  GameRenderer::getPositionColorTexProgram
                //#elseif MC >= 1.21 && MC <= 1.21.1
                //$$  GameRenderer::getPositionTexColorProgram
                //#elseif MC >= 1.21.2
                //$$  ShaderProgramKeys.POSITION_TEX_COLOR
                //#endif
        //$$  );
        //$$  RenderSystem.depthFunc(519);
        //$$  RenderSystem.depthMask(false);
        //$$  RenderSystem.enableBlend();
        //#endif
        Sprite sprite = (FireHudConfig.renderSoulFire && client.player != null && ((SoulFireEntityAccessor) client.player).fireHud$isOnSoulFire() ? SOUL_FIRE_1.getSprite() : ModelBaker.FIRE_1.getSprite());
        //#if MC <= 1.21.3
        //$$  RenderSystem.setShaderTexture(0, sprite.getAtlasId());
        //#endif

        //#if MC >= 1.21.4
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getFireScreenEffect(sprite.getAtlasId()));
        //#endif

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

        float fireOpacity = FireHudConfig.FirstPersonFire.opacity / 100F;
        float firePos = FireHudConfig.FirstPersonFire.height / 100F;

        for (int r = 0; r < 2; ++r) {
            matrices.push();
            matrices.translate((float)(-(r * 2 - 1)) * 0.24f, -1.0f + firePos, -0.2f); // y:-0.3f

            //#if MC > 1.19.2
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(r * 2 - 1) * 70.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((r == 1 ? -10.0f : 10.0f)));
            //#else
            //$$ matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0.0f));
            //$$ matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)(r * 2 - 1) * 70.0f));
            //$$ matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((r == 1 ? -10.0f : 10.0f)));
            //#endif

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();

            //#if MC < 1.21
            //$$  bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            //$$  bufferBuilder.vertex(matrix4f, -0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, fireOpacity).texture(interpolatedU2, interpolatedV2).next();
            //$$  bufferBuilder.vertex(matrix4f, 0.5f, -0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, fireOpacity).texture(interpolatedU1, interpolatedV2).next();
            //$$  bufferBuilder.vertex(matrix4f, 0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, fireOpacity).texture(interpolatedU1, interpolatedV1).next();
            //$$  bufferBuilder.vertex(matrix4f, -0.5f, 0.5f, -0.5f).color(1.0f, 1.0f, 1.0f, fireOpacity).texture(interpolatedU2, interpolatedV1).next();
            //$$  BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            //#elseif MC >= 1.21 && MC <= 1.21.3
            //$$  BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            //$$  bufferBuilder.vertex(matrix4f, -0.5f, -0.5f, -0.5f).texture(interpolatedU2, interpolatedV2).color(1.0f, 1.0f, 1.0f, fireOpacity);
            //$$  bufferBuilder.vertex(matrix4f, 0.5f, -0.5f, -0.5f).texture(interpolatedU1, interpolatedV2).color(1.0f, 1.0f, 1.0f, fireOpacity);
            //$$  bufferBuilder.vertex(matrix4f, 0.5f, 0.5f, -0.5f).texture(interpolatedU1, interpolatedV1).color(1.0f, 1.0f, 1.0f, fireOpacity);
            //$$  bufferBuilder.vertex(matrix4f, -0.5f, 0.5f, -0.5f).texture(interpolatedU2, interpolatedV1).color(1.0f, 1.0f, 1.0f, fireOpacity);
            //$$  BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            //#else
            vertexConsumer.vertex(matrix4f, -0.5f, -0.5f, -0.5f).texture(interpolatedU2, interpolatedV2).color(1.0f, 1.0f, 1.0f, fireOpacity);
            vertexConsumer.vertex(matrix4f, 0.5f, -0.5f, -0.5f).texture(interpolatedU1, interpolatedV2).color(1.0f, 1.0f, 1.0f, fireOpacity);
            vertexConsumer.vertex(matrix4f, 0.5f, 0.5f, -0.5f).texture(interpolatedU1, interpolatedV1).color(1.0f, 1.0f, 1.0f, fireOpacity);
            vertexConsumer.vertex(matrix4f, -0.5f, 0.5f, -0.5f).texture(interpolatedU2, interpolatedV1).color(1.0f, 1.0f, 1.0f, fireOpacity);
            //#endif

            matrices.pop();
        }
        //#if MC <= 1.21.3
        //$$  RenderSystem.disableBlend();
        //$$  RenderSystem.depthMask(true);
        //$$  RenderSystem.depthFunc(515);
        //#endif
        //#endif
    }

}
