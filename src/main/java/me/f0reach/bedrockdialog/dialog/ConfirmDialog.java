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

    /** {@return the title shown at the top of the dialog} */
    Component title();

    /**
     * {@return the body text shown below the title, or {@code null} if no body
     * should be rendered}
     */
    @Nullable Component body();

    /** {@return the label of the affirmative ("Yes") button} */
    Component yesLabel();

    /** {@return the label of the negative ("No") button} */
    Component noLabel();

    /**
     * {@return the desired width of the Yes button on Java Edition (1-1024),
     * or {@code null} to use the platform default}
     * Ignored on Bedrock Edition.
     */
    @Nullable Integer yesWidth();

    /**
     * {@return the desired width of the No button on Java Edition (1-1024),
     * or {@code null} to use the platform default}
     * Ignored on Bedrock Edition.
     */
    @Nullable Integer noWidth();

    /** {@return the callback invoked when the player presses the Yes button} */
    Consumer<Player> onYes();

    /** {@return the callback invoked when the player presses the No button} */
    Consumer<Player> onNo();

    /**
     * {@return the callback invoked when the dialog closes without a button press}
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    /** {@return a new builder for a {@link ConfirmDialog}} */
    static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link ConfirmDialog}. */
    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private Component yesLabel = Component.text("Yes");
        private Component noLabel = Component.text("No");
        private @Nullable Integer yesWidth = null;
        private @Nullable Integer noWidth = null;
        private Consumer<Player> onYes = p -> {};
        private Consumer<Player> onNo = p -> {};
        private Consumer<Player> onClose = p -> {};

        private Builder() {}

        /**
         * Sets the dialog title.
         *
         * @param title the title to display
         * @return this builder
         */
        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the dialog body. Pass {@code null} to omit the body.
         *
         * @param body the body text, or {@code null} to render no body
         * @return this builder
         */
        public Builder body(Component body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the label of the Yes button (defaults to "Yes").
         *
         * @param yesLabel the label to display on the Yes button
         * @return this builder
         */
        public Builder yesLabel(Component yesLabel) {
            this.yesLabel = yesLabel;
            return this;
        }

        /**
         * Sets the label of the No button (defaults to "No").
         *
         * @param noLabel the label to display on the No button
         * @return this builder
         */
        public Builder noLabel(Component noLabel) {
            this.noLabel = noLabel;
            return this;
        }

        /**
         * Sets the desired width of the Yes button on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder yesWidth(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.yesWidth = width;
            return this;
        }

        /**
         * Sets the desired width of the No button on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder noWidth(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.noWidth = width;
            return this;
        }

        /**
         * Sets the callback for the Yes button. Callbacks may run off the main
         * server thread — schedule Bukkit API calls with the scheduler.
         *
         * @param onYes callback invoked with the player who pressed Yes
         * @return this builder
         */
        public Builder onYes(Consumer<Player> onYes) {
            this.onYes = onYes;
            return this;
        }

        /**
         * Sets the callback for the No button. Callbacks may run off the main
         * server thread — schedule Bukkit API calls with the scheduler.
         *
         * @param onNo callback invoked with the player who pressed No
         * @return this builder
         */
        public Builder onNo(Consumer<Player> onNo) {
            this.onNo = onNo;
            return this;
        }

        /**
         * Sets the callback fired when the dialog closes without a button press.
         * Best-effort — may not fire on Paper (Java Edition).
         *
         * @param onClose callback invoked with the player when the dialog closes
         * @return this builder
         */
        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        /** {@return a new immutable {@link ConfirmDialog} with the configured values} */
        public ConfirmDialog build() {
            final Component t = title;
            final @Nullable Component b = body;
            final Component yl = yesLabel;
            final Component nl = noLabel;
            final @Nullable Integer yw = yesWidth;
            final @Nullable Integer nw = noWidth;
            final Consumer<Player> yes = onYes;
            final Consumer<Player> no = onNo;
            final Consumer<Player> close = onClose;
            return new ConfirmDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public Component yesLabel() { return yl; }
                @Override public Component noLabel() { return nl; }
                @Override public @Nullable Integer yesWidth() { return yw; }
                @Override public @Nullable Integer noWidth() { return nw; }
                @Override public Consumer<Player> onYes() { return yes; }
                @Override public Consumer<Player> onNo() { return no; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
