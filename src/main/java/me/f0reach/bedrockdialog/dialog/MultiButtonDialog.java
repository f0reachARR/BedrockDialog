package me.f0reach.bedrockdialog.dialog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A dialog with multiple clickable buttons.
 *
 * <p><b>Thread note:</b> Callbacks may be invoked on a network thread (not the main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for any Bukkit API calls inside callbacks.</p>
 *
 * <p><b>onClose note:</b> Best-effort. May not fire on Paper (Java Edition).</p>
 */
public non-sealed interface MultiButtonDialog extends UnifiedDialog {

    /** {@return the title shown at the top of the dialog} */
    Component title();

    /**
     * {@return the body text shown below the title, or {@code null} if no body
     * should be rendered}
     */
    @Nullable Component body();

    /** {@return the ordered, unmodifiable list of buttons displayed in this dialog} */
    List<DialogButton> buttons();

    /**
     * {@return the callback invoked when the dialog closes without a button press}
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    /**
     * A button in a {@link MultiButtonDialog}.
     *
     * @param label   Display label of the button
     * @param onClick Action to perform when the button is clicked
     * @param width   Desired width on Java Edition (1-1024), or {@code null} for
     *                the platform default. Ignored on Bedrock Edition.
     */
    record DialogButton(Component label, Consumer<Player> onClick, @Nullable Integer width) {
        /**
         * Convenience constructor that uses the platform default width.
         *
         * @param label   display label of the button
         * @param onClick action to perform when the button is clicked
         */
        public DialogButton(Component label, Consumer<Player> onClick) {
            this(label, onClick, null);
        }
    }

    /** {@return a new builder for a {@link MultiButtonDialog}} */
    static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link MultiButtonDialog}. */
    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private final List<DialogButton> buttons = new ArrayList<>();
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
         * Appends a button with the platform default width.
         *
         * @param label   display label of the button
         * @param onClick callback invoked when the button is pressed; may run
         *                off the main server thread
         * @return this builder
         */
        public Builder button(Component label, Consumer<Player> onClick) {
            this.buttons.add(new DialogButton(label, onClick, null));
            return this;
        }

        /**
         * Adds a button with an explicit width on Java Edition.
         *
         * @param label   display label of the button
         * @param width   width in pixels (1-1024); ignored on Bedrock Edition
         * @param onClick callback invoked when the button is pressed; may run
         *                off the main server thread
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder button(Component label, int width, Consumer<Player> onClick) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.buttons.add(new DialogButton(label, onClick, width));
            return this;
        }

        /**
         * Sets the callback fired when the dialog closes without pressing any
         * button. Best-effort — may not fire on Paper (Java Edition).
         *
         * @param onClose callback invoked with the player when the dialog closes
         * @return this builder
         */
        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        /**
         * Builds the immutable {@link MultiButtonDialog} from the configured buttons.
         *
         * @return a new immutable {@link MultiButtonDialog} with the configured buttons
         * @throws IllegalStateException if no buttons have been added
         */
        public MultiButtonDialog build() {
            if (buttons.isEmpty()) {
                throw new IllegalStateException("MultiButtonDialog must have at least one button");
            }
            final Component t = title;
            final @Nullable Component b = body;
            final List<DialogButton> btns = Collections.unmodifiableList(new ArrayList<>(buttons));
            final Consumer<Player> close = onClose;
            return new MultiButtonDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public List<DialogButton> buttons() { return btns; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
