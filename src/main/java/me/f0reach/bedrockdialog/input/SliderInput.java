package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;

/**
 * A slider (numeric range) input.
 *
 * <p><b>Note:</b> Bedrock Edition (via Geyser) only supports integer step values.
 * Float step values will be rounded to the nearest integer (minimum 1).
 * If sub-integer precision is required, use {@link TextInput} instead.</p>
 */
public final class SliderInput implements DialogInput {

    private final String key;
    private final Component label;
    private final float min;
    private final float max;
    private final float step;
    private final float defaultValue;

    private SliderInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
        this.min = builder.min;
        this.max = builder.max;
        this.step = builder.step;
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

    public float min() {
        return min;
    }

    public float max() {
        return max;
    }

    public float step() {
        return step;
    }

    public float defaultValue() {
        return defaultValue;
    }

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private float min = 0f;
        private float max = 100f;
        private float step = 1f;
        private float defaultValue = 0f;

        private Builder(String key) {
            this.key = key;
        }

        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        public Builder min(float min) {
            this.min = min;
            return this;
        }

        public Builder max(float max) {
            this.max = max;
            return this;
        }

        public Builder step(float step) {
            this.step = step;
            return this;
        }

        public Builder defaultValue(float defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public SliderInput build() {
            return new SliderInput(this);
        }
    }
}
