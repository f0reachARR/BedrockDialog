package me.f0reach.bedrockdialog.dialog;

/**
 * Common supertype for every dialog that can be passed to
 * {@link me.f0reach.bedrockdialog.BedrockDialog#show(org.bukkit.entity.Player, UnifiedDialog)}.
 *
 * <p>
 * Sealed so the runtime can pattern-match the concrete dialog kind and dispatch
 * it to the right platform backend (Paper or Geyser). Plugins build dialogs via
 * each subtype's {@code builder()} rather than implementing this interface
 * directly.
 * </p>
 */
public sealed interface UnifiedDialog
        permits ConfirmDialog, NoticeDialog, MultiButtonDialog, InputDialog {}
