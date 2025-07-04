package dev.tellinq.firehud.client.mixin.feature.soulfire;


import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 1.21.2
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(EntityRenderer.class)
//#if MC >= 1.21.2
public class Mixin_EntityRenderer_SoulFire<T extends Entity, S extends EntityRenderState> {
    
    @Inject(method = "updateRenderState", at = @At(value = "TAIL"))
    private void fireHud$soulFireRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        ((Accessor_EntityRenderState_SoulFire) state).fireHud$setOnSoulFire(((Accessor_SoulFireEntity) entity).fireHud$isOnSoulFire() && !entity.isSpectator());
    }
//#else
//$$ public class Mixin_EntityRenderer_SoulFire {
//#endif
}

