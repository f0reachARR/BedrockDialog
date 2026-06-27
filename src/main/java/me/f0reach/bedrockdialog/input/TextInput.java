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

    public @Nullable String placeholder() {
        return placeholder;
    }

    public @Nullable String defaultValue() {
        return defaultValue;
    }

    /**
     * The desired width of this input on Java Edition (1-1024), or {@code null}
     * to use the platform default. Ignored on Bedrock Edition.
     */
    public @Nullable Integer width() {
        return width;
    }

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private @Nullable String placeholder = null;
        private @Nullable String defaultValue = null;
        private @Nullable Integer width = null;

        private Builder(String key) {
            this.key = key;
        }

        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Sets the desired width of the input on Java Edition.
         *
         * @param width width in pixels (1-1024); ignored on Bedrock Edition
         */
        public Builder width(int width) {
            if (width < 1 || width > 1024) {
                throw new IllegalArgumentException("width must be between 1 and 1024, was " + width);
            }
            this.width = width;
            return this;
        }

        public TextInput build() {
            return new TextInput(this);
        }
    }
}
