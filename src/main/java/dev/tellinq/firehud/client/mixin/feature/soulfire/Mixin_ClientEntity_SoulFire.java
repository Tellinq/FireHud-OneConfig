package dev.tellinq.firehud.client.mixin.feature.soulfire;

import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class Mixin_ClientEntity_SoulFire implements Accessor_SoulFireEntity {
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
