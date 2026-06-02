package me.f0reach.bedrockdialog.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.f0reach.bedrockdialog.BedrockDialogPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

/**
 * Defines the {@code /dialog} command tree using Paper's Brigadier API.
 *
 * <pre>
 * /dialog
 * ├── open &lt;id&gt; [target]  (requires: bedrockdialog.dialog.open)
 * └── reload       (requires: bedrockdialog.dialog.reload)
 * </pre>
 */
@SuppressWarnings("UnstableApiUsage")
public final class DialogCommand {

    private DialogCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("bdialog")
                .then(Commands.literal("open")
                        .requires(s -> s.getSender().hasPermission("bedrockdialog.dialog.open"))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests(DialogCommand::suggestDialogIds)
                                .executes(DialogCommand::executeOpenSelf)
                                .then(Commands.argument("target", ArgumentTypes.player())
                                        .executes(DialogCommand::executeOpenTarget))))
                .then(Commands.literal("reload")
                        .requires(s -> s.getSender().hasPermission("bedrockdialog.dialog.reload"))
                        .executes(DialogCommand::executeReload))
                .build();
    }

    private static CompletableFuture<Suggestions> suggestDialogIds(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        BedrockDialogPlugin plugin = JavaPlugin.getPlugin(BedrockDialogPlugin.class);
        if (plugin == null)
            return builder.buildFuture();
        String remaining = builder.getRemainingLowerCase();
        plugin.getConfigLoader().getDialogIds().stream()
                .filter(id -> id.toLowerCase().startsWith(remaining))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static int executeOpenSelf(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("You must specify a target when running this command from console.");
            return Command.SINGLE_SUCCESS;
        }

        return openDialog(ctx, player);
    }

    private static int executeOpenTarget(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PlayerSelectorArgumentResolver selector = ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
        Player target = selector.resolve(ctx.getSource()).getFirst();
        return openDialog(ctx, target);
    }

    private static int openDialog(CommandContext<CommandSourceStack> ctx, Player target) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();
        String id = StringArgumentType.getString(ctx, "id");
        BedrockDialogPlugin plugin = JavaPlugin.getPlugin(BedrockDialogPlugin.class);
        if (plugin == null) {
            sender.sendPlainMessage("BedrockDialog is not loaded.");
            return Command.SINGLE_SUCCESS;
        }

        boolean found = plugin.getConfigLoader().openDialog(target, id);
        if (!found) {
            sender.sendPlainMessage("Unknown dialog: " + id);
            return Command.SINGLE_SUCCESS;
        }

        if (sender == target && executor == target) {
            sender.sendPlainMessage("Opened dialog '" + id + "'.");
        } else {
            sender.sendPlainMessage("Opened dialog '" + id + "' for " + target.getName() + ".");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeReload(CommandContext<CommandSourceStack> ctx) {
        BedrockDialogPlugin plugin = JavaPlugin.getPlugin(BedrockDialogPlugin.class);
        if (plugin == null) {
            ctx.getSource().getSender().sendPlainMessage("BedrockDialog is not loaded.");
            return Command.SINGLE_SUCCESS;
        }
        plugin.reloadConfig();
        plugin.getConfigLoader().reload();
        ctx.getSource().getSender().sendPlainMessage("[BedrockDialog] Config reloaded.");
        return Command.SINGLE_SUCCESS;
    }
}
