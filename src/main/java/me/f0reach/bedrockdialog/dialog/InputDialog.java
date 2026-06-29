package me.f0reach.bedrockdialog.dialog;

import me.f0reach.bedrockdialog.input.DialogInput;
import me.f0reach.bedrockdialog.response.InputResponse;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A form-style dialog with multiple typed input fields.
 *
 * <p>
 * <b>Thread note:</b> Callbacks may be invoked on a network thread (not the
 * main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for any Bukkit
 * API calls inside callbacks.
 * </p>
 *
 * <p>
 * <b>onClose note:</b> Best-effort. May not fire on Paper (Java Edition).
 * </p>
 */
public non-sealed interface InputDialog extends UnifiedDialog {

    /** {@return the title shown at the top of the dialog} */
    Component title();

    /**
     * {@return the body text shown below the title, or {@code null} if no body
     * should be rendered}
     */
    @Nullable
    Component body();

    /** {@return the ordered, unmodifiable list of input fields in this dialog} */
    List<DialogInput> inputs();

    /** {@return the label of the submit button} */
    Component submitLabel();

    /** {@return the label of the cancel button} */
    Component cancelLabel();

    /**
     * {@return the desired width of the submit button on Java Edition (1-1024),
     * or {@code null} to use the platform default}
     * Ignored on Bedrock Edition.
     */
    @Nullable
    Integer submitWidth();

    /**
     * {@return the desired width of the cancel button on Java Edition (1-1024),
     * or {@code null} to use the platform default}
     * Ignored on Bedrock Edition.
     */
    @Nullable
    Integer cancelWidth();

    /**
     * {@return the callback invoked when the player presses the submit button,
     * supplied with the collected {@link InputResponse}}
     */
    BiConsumer<Player, InputResponse> onSubmit();

    /**
     * {@return the callback invoked when the dialog closes without submitting}
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    /** {@return a new builder for an {@link InputDialog}} */
    static Builder builder() {
        return new Builder();
    }

    /** Builder for {@link InputDialog}. */
    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private final List<DialogInput> inputs = new ArrayList<>();
        private Component submitLabel = Component.text("Submit");
        private Component cancelLabel = Component.text("Cancel");
        private @Nullable Integer submitWidth = null;
        private @Nullable Integer cancelWidth = null;
        private BiConsumer<Player, InputResponse> onSubmit = (p, r) -> {
        };
        private Consumer<Player> onClose = p -> {
        };

        private Builder() {
        }

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
         * Appends an input field. Each input's {@link DialogInput#key() key}
         * must be unique within the dialog; duplicates are rejected at
         * {@link #build()} time.
         *
         * @param input the input field to add
         * @return this builder
         */
        public Builder addInput(DialogInput input) {
            this.inputs.add(input);
            return this;
        }

        /**
         * Sets the label of the submit button (defaults to "Submit").
         *
         * @param submitLabel the label to display on the submit button
         * @return this builder
         */
        public Builder submitLabel(Component submitLabel) {
            this.submitLabel = submitLabel;
            return this;
        }

        /**
         * Sets the label of the cancel button (defaults to "Cancel").
         *
         * @param cancelLabel the label to display on the cancel button
         * @return this builder
         */
        public Builder cancelLabel(Component cancelLabel) {
            this.cancelLabel = cancelLabel;
            return this;
        }

        /**
         * Sets the desired width of the submit button on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder submitWidth(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.submitWidth = width;
            return this;
        }

        /**
         * Sets the desired width of the cancel button on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder cancelWidth(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.cancelWidth = width;
            return this;
        }

        /**
         * Sets the callback for the submit button. The supplied
         * {@link InputResponse} contains the values entered by the player.
         * Callbacks may run off the main server thread — schedule Bukkit API
         * calls with the scheduler.
         *
         * @param onSubmit callback invoked with the player and submitted response
         * @return this builder
         */
        public Builder onSubmit(BiConsumer<Player, InputResponse> onSubmit) {
            this.onSubmit = onSubmit;
            return this;
        }

        /**
         * Sets the callback fired when the dialog closes without submitting.
         * Best-effort — may not fire on Paper (Java Edition).
         *
         * @param onClose callback invoked with the player when the dialog closes
         * @return this builder
         */
        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        /**
         * Builds the immutable {@link InputDialog}.
         *
         * @return a new immutable {@link InputDialog} with the configured inputs
         * @throws IllegalStateException if two inputs share the same key
         */
        public InputDialog build() {
            Set<String> seen = new HashSet<>();
            for (DialogInput input : inputs) {
                if (!seen.add(input.key())) {
                    throw new IllegalStateException(
                            "Duplicate input key in InputDialog: '" + input.key() + "'");
                }
            }
            final Component t = title;
            final @Nullable Component b = body;
            final List<DialogInput> ins = Collections.unmodifiableList(new ArrayList<>(inputs));
            final Component sl = submitLabel;
            final Component cl = cancelLabel;
            final @Nullable Integer sw = submitWidth;
            final @Nullable Integer cw = cancelWidth;
            final BiConsumer<Player, InputResponse> submit = onSubmit;
            final Consumer<Player> close = onClose;
            return new InputDialog() {
                @Override
                public Component title() {
                    return t;
                }

                @Override
                public @Nullable Component body() {
                    return b;
                }

                @Override
                public List<DialogInput> inputs() {
                    return ins;
                }

                @Override
                public Component submitLabel() {
                    return sl;
                }

                @Override
                public Component cancelLabel() {
                    return cl;
                }

                @Override
                public @Nullable Integer submitWidth() {
                    return sw;
                }

                @Override
                public @Nullable Integer cancelWidth() {
                    return cw;
                }

                @Override
                public BiConsumer<Player, InputResponse> onSubmit() {
                    return submit;
                }

                @Override
                public Consumer<Player> onClose() {
                    return close;
                }
            };
        }
    }
}
