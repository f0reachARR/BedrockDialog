package me.f0reach.bedrockdialog;

import me.f0reach.bedrockdialog.dialog.ConfirmDialog;
import me.f0reach.bedrockdialog.dialog.InputDialog;
import me.f0reach.bedrockdialog.dialog.MultiButtonDialog;
import me.f0reach.bedrockdialog.dialog.NoticeDialog;
import me.f0reach.bedrockdialog.dialog.UnifiedDialog;
import me.f0reach.bedrockdialog.platform.DialogPlatform;
import me.f0reach.bedrockdialog.platform.geyser.GeyserDialogPlatform;
import me.f0reach.bedrockdialog.platform.paper.PaperDialogPlatform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for BedrockDialog.
 *
 * <p>Call {@link #init(Plugin)} in your plugin's {@code onEnable()}, then use
 * {@link #get()} to obtain the singleton and {@link #show(Player, UnifiedDialog)} to
 * display dialogs to players.</p>
 *
 * <h2>Threading</h2>
 * <p>Dialog callbacks may arrive on a network thread (not the main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} when calling Bukkit API
 * inside any callback.</p>
 *
 * <h2>Geyser support</h2>
 * <p>If Geyser is not installed, dialogs will only be shown to Java Edition players.
 * Bedrock players who connect without Geyser will see nothing (with a warning logged).</p>
 */
public final class BedrockDialog {

    private static volatile @Nullable BedrockDialog instance;

    private final DialogPlatform javaPlatform;
    private final @Nullable DialogPlatform bedrockPlatform;
    private final Logger logger;

    private BedrockDialog(Plugin plugin) {
        this.logger = plugin.getLogger();
        this.javaPlatform = new PaperDialogPlatform();
        this.bedrockPlatform = tryCreateGeyserPlatform();
    }

    /**
     * Initializes the BedrockDialog singleton.
     * Must be called once in {@code onEnable()} before any other usage.
     *
     * @param plugin the owning plugin
     * @throws IllegalStateException if already initialized
     */
    public static void init(Plugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("BedrockDialog is already initialized");
        }
        instance = new BedrockDialog(plugin);
    }

    /**
     * Returns the singleton instance.
     *
     * @throws IllegalStateException if {@link #init(Plugin)} has not been called
     */
    public static BedrockDialog get() {
        BedrockDialog inst = instance;
        if (inst == null) {
            throw new IllegalStateException("BedrockDialog has not been initialized. Call BedrockDialog.init(plugin) in onEnable()");
        }
        return inst;
    }

    /**
     * Shows the given dialog to the player, choosing the appropriate platform
     * (Paper or Geyser) automatically.
     *
     * @param player the player to show the dialog to
     * @param dialog the dialog to display
     */
    public void show(Player player, UnifiedDialog dialog) {
        DialogPlatform platform = resolvePlatform(player);
        switch (dialog) {
            case ConfirmDialog     d -> platform.showConfirmDialog(player, d);
            case NoticeDialog      d -> platform.showNoticeDialog(player, d);
            case MultiButtonDialog d -> platform.showMultiButtonDialog(player, d);
            case InputDialog       d -> platform.showInputDialog(player, d);
        }
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    private DialogPlatform resolvePlatform(Player player) {
        if (bedrockPlatform != null && isBedrockPlayer(player)) {
            return bedrockPlatform;
        }
        return javaPlatform;
    }

    private boolean isBedrockPlayer(Player player) {
        try {
            return org.geysermc.geyser.api.GeyserApi.api().isBedrockPlayer(player.getUniqueId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Attempts to create a {@link GeyserDialogPlatform}, returning {@code null} if
     * Geyser is not available on this server.
     *
     * <p>Detection order:
     * <ol>
     *   <li>PluginManager lookup for {@code Geyser-Spigot} or {@code Geyser-Paper}</li>
     *   <li>Classpath check for {@code org.geysermc.geyser.api.GeyserApi}</li>
     * </ol>
     */
    private @Nullable DialogPlatform tryCreateGeyserPlatform() {
        boolean geyserPresent =
                Bukkit.getPluginManager().getPlugin("Geyser-Spigot") != null
                || Bukkit.getPluginManager().getPlugin("Geyser-Paper") != null;

        if (!geyserPresent) {
            // Fallback: check classpath (e.g., shaded Geyser or non-standard plugin name)
            try {
                Class.forName("org.geysermc.geyser.api.GeyserApi");
                geyserPresent = true;
            } catch (ClassNotFoundException ignored) {
                // Geyser is not available
            }
        }

        if (geyserPresent) {
            logger.info("[BedrockDialog] Geyser detected — Bedrock Edition support enabled.");
            return new GeyserDialogPlatform();
        } else {
            logger.warning("[BedrockDialog] Geyser not found — only Java Edition players will receive dialogs.");
            return null;
        }
    }
}
