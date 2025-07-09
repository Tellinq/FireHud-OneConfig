package dev.tellinq.firehud.client.mixin.feature.fireoverlay;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#if MC >= 1.21.2
import net.minecraft.client.render.entity.state.EntityRenderState;
//#else
//$$ import net.minecraft.entity.Entity;
//#endif
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
//#if MC > 1.19.2
import org.joml.Quaternionf;
//#endif
import dev.tellinq.firehud.client.FireHud;
import dev.tellinq.firehud.client.config.FireHudConfig;
import dev.tellinq.firehud.client.accessor.Accessor_EntityRenderState_SoulFire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//#if MC <= 1.21.1
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
//#endif
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public
//#if MC >= 1.21.2
abstract
//#endif
class Mixin_EntityRenderDispatcher_ThirdPersonFire {
    @Unique private static final SpriteIdentifier SOUL_FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, FireHud.getIdentifierOf("block/soul_fire_0"));
    @Unique private static final SpriteIdentifier SOUL_FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, FireHud.getIdentifierOf("block/soul_fire_1"));
    
    @Inject(method = "renderFire", at = @At(value = "HEAD"), cancellable = true)
    private void fireHud$renderThirdPersonFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                       //#if MC >= 1.21.2
                                       EntityRenderState renderState,
                                       //#else
                                       //$$ Entity entity,
                                       //#endif
                                       //#if MC > 1.19.2
                                       Quaternionf rotation,
                                       //#endif
                                       CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.player.isOnFire()) {
            if ((!FireHudConfig.ThirdPersonFire.whenInLava && client.player.isInLava())) ci.cancel();
            if ((!FireHudConfig.ThirdPersonFire.fireResistance && client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) ci.cancel();
            if (!FireHudConfig.ThirdPersonFire.enabled) ci.cancel();
        }
    }

    //#if MC >= 1.21.2
    @Inject(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private void fireHud$getSprite0(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                            EntityRenderState renderState,
                            Quaternionf rotation, CallbackInfo ci, @Local(ordinal = 0) LocalRef<Sprite> localRef) {
        localRef.set(FireHudConfig.renderSoulFire && ((Accessor_EntityRenderState_SoulFire) renderState).fireHud$onSoulFire() ? SOUL_FIRE_0.getSprite() : localRef.get());
    }
    @Inject(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private void fireHud$getSprite1(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                            EntityRenderState renderState,
                            Quaternionf rotation, CallbackInfo ci, @Local(ordinal = 1) LocalRef<Sprite> localRef) {
        localRef.set(FireHudConfig.renderSoulFire && ((Accessor_EntityRenderState_SoulFire) renderState).fireHud$onSoulFire() ? SOUL_FIRE_1.getSprite() : localRef.get());
    }
    //#else
    //$$  @Redirect(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;", ordinal = 0))
    //$$  private Sprite fireHud$getSprite0(SpriteIdentifier obj, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
    //$$      if (FireHudConfig.renderSoulFire && ((Accessor_SoulFireEntity)entity).fireHud$isOnSoulFire()) {
    //$$          return SOUL_FIRE_0.getSprite();
    //$$      }
    //$$      return obj.getSprite();
    //$$  }
    //$$
    //$$  @Redirect(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;", ordinal = 1))
    //$$  private Sprite fireHud$getSprite1(SpriteIdentifier obj, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
    //$$      if (FireHudConfig.renderSoulFire && ((Accessor_SoulFireEntity)entity).fireHud$isOnSoulFire()) {
    //$$          return SOUL_FIRE_1.getSprite();
    //$$      }
    //$$      return obj.getSprite();
    //$$  }
    //#endif
}
