package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * A text input field.
 */
public final class TextInput implements DialogInput {

    private final String key;
    private final Component label;
    private final @Nullable String placeholder;
    private final @Nullable String defaultValue;
    private final @Nullable Integer width;

    private TextInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
        this.placeholder = builder.placeholder;
        this.defaultValue = builder.defaultValue;
        this.width = builder.width;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Component label() {
        return label;
    }

    /**
     * {@return the placeholder text shown when the field is empty, or
     * {@code null} if no placeholder was configured}
     */
    public @Nullable String placeholder() {
        return placeholder;
    }

    /** {@return the initial value of the field, or {@code null} to start empty} */
    public @Nullable String defaultValue() {
        return defaultValue;
    }

    /**
     * {@return the desired width of this input on Java Edition (1-1024), or
     * {@code null} to use the platform default}
     * Ignored on Bedrock Edition.
     */
    public @Nullable Integer width() {
        return width;
    }

    /**
     * Returns a new builder for a {@link TextInput}.
     *
     * @param key unique key identifying this input within its dialog
     * @return a new {@link Builder}
     */
    public static Builder builder(String key) {
        return new Builder(key);
    }

    /** Builder for {@link TextInput}. */
    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private @Nullable String placeholder = null;
        private @Nullable String defaultValue = null;
        private @Nullable Integer width = null;

        private Builder(String key) {
            this.key = key;
        }

        /**
         * Sets the display label shown next to the field.
         *
         * @param label the label to display next to the field
         * @return this builder
         */
        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the placeholder shown when the field is empty.
         *
         * @param placeholder the placeholder text
         * @return this builder
         */
        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        /**
         * Sets the initial value of the field.
         *
         * @param defaultValue the initial value
         * @return this builder
         */
        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Sets the desired width of the input on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         * @return this builder
         * @throws IllegalArgumentException if {@code width} is outside 1-1024
         */
        public Builder width(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.width = width;
            return this;
        }

        /** {@return a new immutable {@link TextInput} with the configured values} */
        public TextInput build() {
            return new TextInput(this);
        }
    }
}
