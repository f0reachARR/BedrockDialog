package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;

/**
 * A boolean toggle input.
 */
public final class BooleanInput implements DialogInput {

    private final String key;
    private final Component label;
    private final boolean defaultValue;

    private BooleanInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
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

    public boolean defaultValue() {
        return defaultValue;
    }

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private boolean defaultValue = false;

        private Builder(String key) {
            this.key = key;
        }

        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        public Builder defaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public BooleanInput build() {
            return new BooleanInput(this);
        }
    }
}
