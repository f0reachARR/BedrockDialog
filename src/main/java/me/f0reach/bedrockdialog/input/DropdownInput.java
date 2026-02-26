package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;

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

    private DropdownInput(Builder builder) {
        this.key = builder.key;
        this.label = builder.label;
        this.options = Collections.unmodifiableList(new ArrayList<>(builder.options));
        this.defaultIndex = builder.defaultIndex;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Component label() {
        return label;
    }

    public List<DropdownOption> options() {
        return options;
    }

    public int defaultIndex() {
        return defaultIndex;
    }

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

    public static final class Builder {
        private final String key;
        private Component label = Component.empty();
        private final List<DropdownOption> options = new ArrayList<>();
        private int defaultIndex = 0;

        private Builder(String key) {
            this.key = key;
        }

        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        public Builder addOption(String id, Component label) {
            this.options.add(new DropdownOption(id, label));
            return this;
        }

        public Builder defaultIndex(int defaultIndex) {
            this.defaultIndex = defaultIndex;
            return this;
        }

        public DropdownInput build() {
            if (options.isEmpty()) {
                throw new IllegalStateException("DropdownInput must have at least one option");
            }
            return new DropdownInput(this);
        }
    }
}
