package dev.tellinq.firehud.client.command;

import dev.tellinq.firehud.FireHudConstants;
import dev.tellinq.firehud.client.FireHud;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Handler;
import org.polyfrost.oneconfig.utils.v1.dsl.ScreensKt;

@Command(FireHudConstants.ID)
public class FireHudCommand {

    @Handler
    private void main() {
        ScreensKt.openUI(FireHudConfig.INSTANCE);
    }

}
