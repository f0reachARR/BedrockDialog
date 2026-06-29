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

    /** {@return the initial state of the toggle} */
    public boolean defaultValue() {
        return defaultValue;
    }

    /**
     * Returns a new builder for a {@link BooleanInput}.
     *
     * @param key unique key identifying this input within its dialog
     * @return a new {@link Builder}
     */
    public static Builder builder(String key) {
        return new Builder(key);
    }

    /** Builder for {@link BooleanInput}. */
    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private boolean defaultValue = false;

        private Builder(String key) {
            this.key = key;
        }

        /**
         * Sets the display label shown next to the toggle.
         *
         * @param label the label to display next to the toggle
         * @return this builder
         */
        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the initial state of the toggle.
         *
         * @param defaultValue the initial state
         * @return this builder
         */
        public Builder defaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /** {@return a new immutable {@link BooleanInput} with the configured values} */
        public BooleanInput build() {
            return new BooleanInput(this);
        }
    }
}
