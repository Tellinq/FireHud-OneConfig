package dev.tellinq.firehud.client;

import dev.deftu.omnicore.api.OmniIdentifier;
import dev.tellinq.firehud.FireHudConstants;
import dev.tellinq.firehud.client.hud.FireResistanceTimer;
import net.minecraft.util.Identifier;
import dev.tellinq.firehud.client.command.FireHudCommand;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.polyfrost.oneconfig.api.commands.v1.CommandManager;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;

public class FireHud {
    public static final FireHud INSTANCE = new FireHud();

    public void initialize() {
        FireHudConfig.INSTANCE.preload();
        CommandManager.register(new FireHudCommand());
        HudManager.register(new FireResistanceTimer());
    }

    public static Identifier getFireHudResource(String path) {
        return OmniIdentifier.createOrNull(FireHudConstants.ID, path);
    }
}
