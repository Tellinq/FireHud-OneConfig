package dev.tellinq.firehud.client.mixin;


import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 1.21.2
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import dev.tellinq.firehud.client.SoulFireEntityAccessor;
import dev.tellinq.firehud.client.SoulFireRenderStateAccessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(EntityRenderer.class)
//#if MC >= 1.21.2
public class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    
    @Inject(method = "updateRenderState", at = @At(value = "TAIL"))
    private void soulFireRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        ((SoulFireRenderStateAccessor) state).fireHud$setOnSoulFire(((SoulFireEntityAccessor) entity).fireHud$isOnSoulFire() && !entity.isSpectator());
    }
//#else
//$$ public class EntityRendererMixin {
//#endif
}

