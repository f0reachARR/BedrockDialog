package me.f0reach.bedrockdialog;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.f0reach.bedrockdialog.command.DialogCommand;

import java.util.List;

/**
 * Paper plugin bootstrapper — registers the {@code /dialog} command tree before
 * the plugin is enabled, so the command is available immediately on server start.
 */
@SuppressWarnings("UnstableApiUsage")
public class BedrockDialogBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(
                DialogCommand.build(),
                "BedrockDialog commands",
                List.of()
            );
        });
    }
}
