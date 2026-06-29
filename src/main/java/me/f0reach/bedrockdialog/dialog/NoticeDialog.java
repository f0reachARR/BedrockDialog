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

    /** {@return the title shown at the top of the dialog} */
    Component title();

    /**
     * {@return the body text shown below the title, or {@code null} if no body
     * should be rendered}
     */
    @Nullable Component body();

    /** {@return the label of the dismiss button} */
    Component dismissLabel();

    /**
     * {@return the desired width of the dismiss button on Java Edition (1-1024),
     * or {@code null} to use the platform default}
     * Ignored on Bedrock Edition.
     */
    @Nullable Integer dismissWidth();

    /** {@return the callback invoked when the player presses the dismiss button} */
    Consumer<Player> onDismiss();

    /**
     * {@return the callback invoked when the dialog closes without pressing dismiss}
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    /** {@return a new builder for a {@link NoticeDialog}} */
    static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link NoticeDialog}. */
    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private Component dismissLabel = Component.text("OK");
        private @Nullable Integer dismissWidth = null;
        private Consumer<Player> onDismiss = p -> {};
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
         * Sets the label of the dismiss button (defaults to "OK").
         *
         * @param dismissLabel the label to display on the dismiss button
         * @return this builder
         */
        public Builder dismissLabel(Component dismissLabel) {
            this.dismissLabel = dismissLabel;
            return this;
        }

        /**
         * Sets the desired width of the dismiss button on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder dismissWidth(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.dismissWidth = width;
            return this;
        }

        /**
         * Sets the callback for the dismiss button. Callbacks may run off the
         * main server thread — schedule Bukkit API calls with the scheduler.
         *
         * @param onDismiss callback invoked with the player who pressed dismiss
         * @return this builder
         */
        public Builder onDismiss(Consumer<Player> onDismiss) {
            this.onDismiss = onDismiss;
            return this;
        }

        /**
         * Sets the callback fired when the dialog closes without pressing the
         * dismiss button. Best-effort — may not fire on Paper (Java Edition).
         *
         * @param onClose callback invoked with the player when the dialog closes
         * @return this builder
         */
        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        /** {@return a new immutable {@link NoticeDialog} with the configured values} */
        public NoticeDialog build() {
            final Component t = title;
            final @Nullable Component b = body;
            final Component dl = dismissLabel;
            final @Nullable Integer dw = dismissWidth;
            final Consumer<Player> dismiss = onDismiss;
            final Consumer<Player> close = onClose;
            return new NoticeDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public Component dismissLabel() { return dl; }
                @Override public @Nullable Integer dismissWidth() { return dw; }
                @Override public Consumer<Player> onDismiss() { return dismiss; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
