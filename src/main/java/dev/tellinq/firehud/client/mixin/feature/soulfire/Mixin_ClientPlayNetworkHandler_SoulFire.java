package dev.tellinq.firehud.client.mixin.feature.soulfire;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
//#if MC > 1.19.2
import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(ClientPlayNetworkHandler.class)
public class Mixin_ClientPlayNetworkHandler_SoulFire {
    //#if MC > 1.19.2
    @Shadow private ClientWorld world;
    
    @Inject(method = "onEntityDamage", at = @At("HEAD"))
    public void fireHud$entitySetsOnSoulFire(EntityDamageS2CPacket packet, CallbackInfo ci) {
        if (FireHudConfig.renderSoulFire && this.world != null) {
            Entity targetEntity = this.world.getEntityById(packet.comp_1267());
            Entity sourceEntity = this.world.getEntityById(packet.comp_1270());
            if (targetEntity != null && sourceEntity != null) {
                if ((sourceEntity instanceof ZombieEntity || sourceEntity instanceof ArrowEntity) && sourceEntity.doesRenderOnFire()) {
                    ((Accessor_SoulFireEntity) targetEntity).fireHud$setOnSoulFire(((Accessor_SoulFireEntity) sourceEntity).fireHud$isOnSoulFire());
                }
            }
            if (targetEntity != null) {
                if (packet.createDamageSource(this.world).isOf(DamageTypes.LIGHTNING_BOLT)) {
                    ((Accessor_SoulFireEntity) targetEntity).fireHud$setOnSoulFire(false);
                }
            }
        }
    }
    
    @Inject(method = "tick", at = @At("HEAD"))
    public void fireHud$clientTickEvents(CallbackInfo ci) {
        if (this.world != null && FireHudConfig.renderSoulFire) {
            this.world.getEntities().forEach(entity -> {
                Box box = entity.getBoundingBox();
                BlockPos blockPos = new BlockPos(MathHelper.floor(box.minX + 0.001), MathHelper.floor(box.minY + 0.001), MathHelper.floor(box.minZ + 0.001));
                BlockPos blockPos2 = new BlockPos(MathHelper.floor(box.maxX - 0.001), MathHelper.floor(box.maxY - 0.001), MathHelper.floor(box.maxZ - 0.001));
                if (entity.getWorld() != null && entity.getWorld().isRegionLoaded(blockPos, blockPos2)) {
                    BlockPos.Mutable mutable = new BlockPos.Mutable();
                    for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                        for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                            for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                                mutable.set(i, j, k);
                                try {
                                    Block block = entity.getWorld().getBlockState(mutable).getBlock();
                                    if (block instanceof SoulFireBlock) ((Accessor_SoulFireEntity)entity).fireHud$setOnSoulFire(true);
                                    if (block instanceof FireBlock) ((Accessor_SoulFireEntity)entity).fireHud$setOnSoulFire(false);
                                    if (entity.isInLava()) ((Accessor_SoulFireEntity)entity).fireHud$setOnSoulFire(false);
                                } catch (Throwable throwable) {
                                    CrashReport crashReport = CrashReport.create(throwable, "Colliding entity with block");
                                    throw new CrashException(crashReport);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
    //#endif
}
