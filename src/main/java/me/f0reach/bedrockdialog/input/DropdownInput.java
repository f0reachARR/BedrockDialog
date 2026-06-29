package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A dropdown selection input.
 *
 * <p>On Java Edition (Paper), the response returns the selected option's {@code id}.
 * On Bedrock Edition (Geyser), the response returns the selected index.
 * {@link me.f0reach.bedrockdialog.response.InputResponse} exposes both via
 * {@code getDropdownOptionId(key)} and {@code getDropdownIndex(key)}.</p>
 */
public final class DropdownInput implements DialogInput {

    private final String key;
    private final Component label;
    private final List<DropdownOption> options;
    private final int defaultIndex;
    private final @Nullable Integer width;

    private DropdownInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
        this.options = Collections.unmodifiableList(new ArrayList<>(builder.options));
        this.defaultIndex = builder.defaultIndex;
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

    /** {@return the unmodifiable list of options in display order} */
    public List<DropdownOption> options() {
        return options;
    }

    /** {@return the 0-based index of the option initially selected} */
    public int defaultIndex() {
        return defaultIndex;
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
     * Returns a new builder for a {@link DropdownInput}.
     *
     * @param key unique key identifying this input within its dialog
     * @return a new {@link Builder}
     */
    public static Builder builder(String key) {
        return new Builder(key);
    }

    /**
     * An option within a {@link DropdownInput}.
     *
     * @param id    Unique identifier for this option (used by Paper for response lookup)
     * @param label Display label shown to the player
     */
    public record DropdownOption(String id, Component label) {}

    /** Builder for {@link DropdownInput}. */
    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private final List<DropdownOption> options = new ArrayList<>();
        private int defaultIndex = 0;
        private @Nullable Integer width = null;

        private Builder(String key) {
            this.key = key;
        }

        /**
         * Sets the display label shown next to the dropdown.
         *
         * @param label the label to display next to the dropdown
         * @return this builder
         */
        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        /**
         * Appends an option to the dropdown.
         *
         * @param id    stable identifier returned by
         *              {@link me.f0reach.bedrockdialog.response.InputResponse#getDropdownOptionId(String)}
         * @param label display label shown to the player
         * @return this builder
         */
        public Builder addOption(String id, Component label) {
            this.options.add(new DropdownOption(id, label));
            return this;
        }

        /**
         * Sets the index (0-based) of the option initially selected.
         *
         * @param defaultIndex the 0-based index of the option to preselect
         * @return this builder
         */
        public Builder defaultIndex(int defaultIndex) {
            this.defaultIndex = defaultIndex;
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

        /**
         * Builds the immutable {@link DropdownInput}.
         *
         * @return a new immutable {@link DropdownInput} with the configured options
         * @throws IllegalStateException if no options have been added
         */
        public DropdownInput build() {
            if (options.isEmpty()) {
                throw new IllegalStateException("DropdownInput must have at least one option");
            }
            return new DropdownInput(this);
        }
    }
}
