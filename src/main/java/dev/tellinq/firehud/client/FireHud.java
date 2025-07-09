package dev.tellinq.firehud.client;

import dev.deftu.omnicore.common.OmniIdentifier;
import dev.tellinq.firehud.FireHudConstants;
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

    public static Identifier getFireHudResource(String path) {
        return OmniIdentifier.create(FireHudConstants.ID, path);
    }
}
