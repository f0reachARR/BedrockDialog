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

    private TextInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
        this.placeholder = builder.placeholder;
        this.defaultValue = builder.defaultValue;
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

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private @Nullable String placeholder = null;
        private @Nullable String defaultValue = null;

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

        public TextInput build() {
            return new TextInput(this);
        }
    }
}
