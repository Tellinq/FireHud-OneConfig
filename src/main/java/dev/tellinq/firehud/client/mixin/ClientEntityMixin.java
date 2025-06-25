package dev.tellinq.firehud.client.mixin;

import net.minecraft.entity.Entity;
import dev.tellinq.firehud.client.SoulFireEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class ClientEntityMixin implements SoulFireEntityAccessor {
    @Unique private boolean soulFire;
    
    @Override
    public boolean fireHud$isOnSoulFire() {
        return soulFire;
    }
    
    @Override
    public void fireHud$setOnSoulFire(boolean onSoulFire) {
        this.soulFire = onSoulFire;
    }
}
