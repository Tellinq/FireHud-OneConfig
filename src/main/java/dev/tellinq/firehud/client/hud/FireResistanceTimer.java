package dev.tellinq.firehud.client.hud;

import dev.deftu.omnicore.client.OmniClient;
import dev.deftu.omnicore.client.OmniClientPlayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;
import org.polyfrost.oneconfig.api.hud.v1.TextHud;

import java.util.Objects;

public class FireResistanceTimer extends TextHud {

    @Switch(
            title = "Show as Ticks"
    )
    public boolean showAsTicks = false;

    public FireResistanceTimer() {
        super("fireresistancetimer", "Fire Resistance Timer", Category.getINFO(), "Fire Resistance:", "");
    }

    @Override
    protected @Nullable String getText() {
        //#if MC > 1.19.4
        ClientWorld world = OmniClient.getWorld();
        ClientPlayerEntity player = OmniClientPlayer.getInstance();

        if (player != null && world != null) {
            StatusEffectInstance fireRes = player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);

            if (player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && fireRes != null && !fireRes.isInfinite()) {

                Text durationSeconds = StatusEffectUtil.getDurationText(fireRes, 1.0f, world.getTickManager().getTickRate());
                Text durationTicks = Text.literal(String.valueOf(Objects.requireNonNull(player.getStatusEffect(StatusEffects.FIRE_RESISTANCE)).getDuration()));
                Text text = Text.literal((this.showAsTicks ? durationTicks : durationSeconds).getString());
                return text.getString();
            }
        }
        //#endif

        return "00:00";
    }
}
