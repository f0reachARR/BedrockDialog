package me.f0reach.bedrockdialog.dialog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An informational dialog with a single dismiss button.
 *
 * <p><b>Thread note:</b> Callbacks may be invoked on a network thread (not the main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for any Bukkit API calls inside callbacks.</p>
 *
 * <p><b>onClose note:</b> Best-effort. May not fire on Paper (Java Edition).</p>
 */
public non-sealed interface NoticeDialog extends UnifiedDialog {

    Component title();

    @Nullable Component body();

    Component dismissLabel();

    Consumer<Player> onDismiss();

    /**
     * Called when the dialog is closed without pressing the dismiss button.
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private Component dismissLabel = Component.text("OK");
        private Consumer<Player> onDismiss = p -> {};
        private Consumer<Player> onClose = p -> {};

        private Builder() {}

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder body(Component body) {
            this.body = body;
            return this;
        }

        public Builder dismissLabel(Component dismissLabel) {
            this.dismissLabel = dismissLabel;
            return this;
        }

        public Builder onDismiss(Consumer<Player> onDismiss) {
            this.onDismiss = onDismiss;
            return this;
        }

        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        public NoticeDialog build() {
            final Component t = title;
            final @Nullable Component b = body;
            final Component dl = dismissLabel;
            final Consumer<Player> dismiss = onDismiss;
            final Consumer<Player> close = onClose;
            return new NoticeDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public Component dismissLabel() { return dl; }
                @Override public Consumer<Player> onDismiss() { return dismiss; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
