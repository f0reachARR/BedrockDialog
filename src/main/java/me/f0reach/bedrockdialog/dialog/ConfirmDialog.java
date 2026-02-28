package me.f0reach.bedrockdialog.dialog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A confirmation dialog with "Yes" and "No" buttons.
 *
 * <p><b>Thread note:</b> Callbacks may be invoked on a network thread (not the main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for any Bukkit API calls inside callbacks.</p>
 *
 * <p><b>onClose note:</b> On Paper, there is no event for "closed without pressing a button",
 * so {@code onClose} is best-effort and may not fire on Java Edition.</p>
 */
public non-sealed interface ConfirmDialog extends UnifiedDialog {

    Component title();

    @Nullable Component body();

    Component yesLabel();

    Component noLabel();

    Consumer<Player> onYes();

    Consumer<Player> onNo();

    /**
     * Called when the dialog is closed without a button press.
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private Component yesLabel = Component.text("Yes");
        private Component noLabel = Component.text("No");
        private Consumer<Player> onYes = p -> {};
        private Consumer<Player> onNo = p -> {};
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

        public Builder yesLabel(Component yesLabel) {
            this.yesLabel = yesLabel;
            return this;
        }

        public Builder noLabel(Component noLabel) {
            this.noLabel = noLabel;
            return this;
        }

        public Builder onYes(Consumer<Player> onYes) {
            this.onYes = onYes;
            return this;
        }

        public Builder onNo(Consumer<Player> onNo) {
            this.onNo = onNo;
            return this;
        }

        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        public ConfirmDialog build() {
            final Component t = title;
            final @Nullable Component b = body;
            final Component yl = yesLabel;
            final Component nl = noLabel;
            final Consumer<Player> yes = onYes;
            final Consumer<Player> no = onNo;
            final Consumer<Player> close = onClose;
            return new ConfirmDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public Component yesLabel() { return yl; }
                @Override public Component noLabel() { return nl; }
                @Override public Consumer<Player> onYes() { return yes; }
                @Override public Consumer<Player> onNo() { return no; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
