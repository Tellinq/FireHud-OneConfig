package dev.tellinq.firehud.client.mixin.feature.soulfire;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 1.21.2
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class Mixin_EntityRenderState_SoulFire implements Accessor_EntityRenderState_SoulFire {
    @Unique private boolean onSoulFire;
    
    @Override
    public boolean fireHud$onSoulFire() {
        return onSoulFire;
    }

    @Override
    public void fireHud$setOnSoulFire(boolean onSoulFire) {
        this.onSoulFire = onSoulFire;
    }
}
//#else
//$$ import net.minecraft.client.render.entity.EntityRenderer;
//$$ @Mixin(EntityRenderer.class)
//$$ public class EntityRenderStateMixin {
//$$ }
//#endif