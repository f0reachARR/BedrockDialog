package me.f0reach.bedrockdialog.config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * A config-defined action that can be executed when a dialog button is pressed.
 */
public sealed interface DialogAction permits
        DialogAction.RunAsPlayer,
        DialogAction.RunAsConsole,
        DialogAction.OpenDialog {

    /** Executes a command as the player. */
    record RunAsPlayer(String command) implements DialogAction {}

    /** Executes a command as the console. */
    record RunAsConsole(String command) implements DialogAction {}

    /** Opens another dialog by its config ID. */
    record OpenDialog(String dialogId) implements DialogAction {}

    /**
     * Executes a list of actions for a non-input dialog (no {@code {input.*}} placeholders).
     */
    static void execute(List<DialogAction> actions, Player player, Plugin plugin,
                        DialogConfigLoader loader) {
        execute(actions, player, plugin, loader, Map.of());
    }

    /**
     * Executes a list of actions, substituting {@code {player}} and {@code {input.<key>}}
     * placeholders in command strings.
     *
     * @param inputValues pre-resolved input values, keyed by input field key
     */
    static void execute(List<DialogAction> actions, Player player, Plugin plugin,
                        DialogConfigLoader loader, Map<String, String> inputValues) {
        if (actions.isEmpty()) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (DialogAction action : actions) {
                switch (action) {
                    case RunAsPlayer a ->
                        player.performCommand(replace(a.command(), player, inputValues));
                    case RunAsConsole a ->
                        Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            replace(a.command(), player, inputValues));
                    case OpenDialog a ->
                        loader.openDialog(player, a.dialogId());
                }
            }
        });
    }

    private static String replace(String template, Player player, Map<String, String> inputValues) {
        String result = template.replace("{player}", player.getName());
        for (Map.Entry<String, String> e : inputValues.entrySet()) {
            result = result.replace("{input." + e.getKey() + "}", e.getValue());
        }
        return result;
    }
}
