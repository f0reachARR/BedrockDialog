package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

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
    private final @Nullable Integer width;

    private SliderInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
        this.min = builder.min;
        this.max = builder.max;
        this.step = builder.step;
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

    /** {@return the minimum value of the slider} */
    public float min() {
        return min;
    }

    /** {@return the maximum value of the slider} */
    public float max() {
        return max;
    }

    /**
     * {@return the step size between selectable values}
     * Rounded to the nearest integer on Bedrock Edition (minimum 1).
     */
    public float step() {
        return step;
    }

    /** {@return the initial value of the slider} */
    public float defaultValue() {
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
     * Returns a new builder for a {@link SliderInput}.
     *
     * @param key unique key identifying this input within its dialog
     * @return a new {@link Builder}
     */
    public static Builder builder(String key) {
        return new Builder(key);
    }

    /** Builder for {@link SliderInput}. */
    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private float min = 0f;
        private float max = 100f;
        private float step = 1f;
        private float defaultValue = 0f;
        private @Nullable Integer width = null;

        private Builder(String key) {
            this.key = key;
        }

        /**
         * Sets the display label shown next to the slider.
         *
         * @param label the label to display next to the slider
         * @return this builder
         */
        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        /**
         * Sets the minimum value of the slider.
         *
         * @param min the minimum value
         * @return this builder
         */
        public Builder min(float min) {
            this.min = min;
            return this;
        }

        /**
         * Sets the maximum value of the slider.
         *
         * @param max the maximum value
         * @return this builder
         */
        public Builder max(float max) {
            this.max = max;
            return this;
        }

        /**
         * Sets the step size between selectable values.
         * Rounded to the nearest integer on Bedrock Edition.
         *
         * @param step the step size
         * @return this builder
         */
        public Builder step(float step) {
            this.step = step;
            return this;
        }

        /**
         * Sets the initial value of the slider.
         *
         * @param defaultValue the initial value
         * @return this builder
         */
        public Builder defaultValue(float defaultValue) {
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

        /** {@return a new immutable {@link SliderInput} with the configured values} */
        public SliderInput build() {
            return new SliderInput(this);
        }
    }
}
