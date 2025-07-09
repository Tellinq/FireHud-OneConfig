package dev.tellinq.firehud.client;

//import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
//import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import dev.tellinq.firehud.FireHudConstants;
import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import dev.tellinq.firehud.client.command.FireHudCommand;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.polyfrost.oneconfig.api.commands.v1.CommandManager;

public class FireHud {
    public static final FireHud INSTANCE = new FireHud();

    public void initialize() {
        FireHudConfig.INSTANCE.preload();
        CommandManager.register(new FireHudCommand());
    }

    // TODO: Make Fire Tint work and backport it to 1.19 and older as well
    //#if MC >= 1.20
    private void fireTint(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        if (player != null && player.isOnFire() && client.options.getPerspective().isFirstPerson() &&
                !(!FireHudConfig.FirstPersonFire.whenInLava && player.isInLava()) && !(!FireHudConfig.FirstPersonFire.fireResistance && player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) {

            if (FireHudConfig.FireScreenTint.enabled && !((Accessor_SoulFireEntity) player).fireHud$isOnSoulFire()) {
                context.fillGradient(0, 0, width, height, FireHudConfig.FireScreenTint.tintStartColor.getArgb(), FireHudConfig.FireScreenTint.tintEndColor.getArgb());
            }
            if (FireHudConfig.FireScreenTint.enabled && FireHudConfig.renderSoulFire && ((Accessor_SoulFireEntity) player).fireHud$isOnSoulFire()) {
                context.fillGradient(0, 0, width, height, FireHudConfig.FireScreenTint.soulTintStartColor.getArgb(), FireHudConfig.FireScreenTint.soulTintEndColor.getArgb());
            }
        }
    }
    //#endif

    public static Identifier getIdentifierOf(String a) {
        return
                //#if MC >= 1.21
                Identifier.of
                        //#else
                        //$$  new Identifier
                        //#endif
                                (a);
    }

    public static Identifier getIdentifierOf(String a, String b) {
        return
                //#if MC >= 1.21
                Identifier.of
                        //#else
                        //$$  new Identifier
                        //#endif
                                (a, b);
    }

    public static Identifier getFireHudResource(String path) {
        return getIdentifierOf(FireHudConstants.ID, path);
    }
}
