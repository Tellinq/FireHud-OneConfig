package dev.tellinq.firehud.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 1.21.2
import net.minecraft.client.render.entity.state.EntityRenderState;
import dev.tellinq.firehud.client.SoulFireRenderStateAccessor;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements SoulFireRenderStateAccessor {
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