package dev.tellinq.firehud.client.mixin.feature.firesound;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC > 1.18.2
import net.minecraft.util.math.random.Random;
//#else
//$$ import java.util.Random;
//#endif

@Mixin(AbstractFireBlock.class)
public class Mixin_AbstractFireBlock_FireSound {
    
    @Redirect(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    private void fireHud$fireSound(World world, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        float randomFl =
                //#if MC > 1.18.2
                Random.create().nextFloat();
                //#else
                //$$ new Random().nextFloat();
                //#endif
        
        world.playSoundClient(x, y, z, sound, category, (FireHudConfig.FireVolumePitch.fireVolume / 100f) + (FireHudConfig.FireVolumePitch.randomizeFireVolume ? randomFl : 0), (FireHudConfig.FireVolumePitch.randomizeFirePitch ? randomFl * 0.7f : 0) + (FireHudConfig.FireVolumePitch.firePitch / 100f), useDistance);
    }
}
