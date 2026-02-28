package me.f0reach.bedrockdialog;

import me.f0reach.bedrockdialog.config.DialogConfigLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Standalone Paper plugin entry point for BedrockDialog.
 *
 * <p>When running as a plugin, dialogs are defined in {@code config.yml} and
 * displayed with {@code /dialog open <id>}.</p>
 *
 * <p>When used as a library by another plugin, the other plugin calls
 * {@link BedrockDialog#init(org.bukkit.plugin.Plugin)} in its own {@code onEnable()}.
 * A duplicate call after this plugin has already initialized will be ignored with a warning.</p>
 */
public class BedrockDialogPlugin extends JavaPlugin {

    private DialogConfigLoader configLoader;

    @Override
    public void onEnable() {
        BedrockDialog.init(this);
        saveDefaultConfig();
        configLoader = new DialogConfigLoader(this);
        configLoader.reload();
    }

    @Override
    public void onDisable() {
        BedrockDialog.reset();
    }

    public DialogConfigLoader getConfigLoader() {
        return configLoader;
    }
}
