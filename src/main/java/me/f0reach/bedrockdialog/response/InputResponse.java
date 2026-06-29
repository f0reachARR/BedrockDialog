package me.f0reach.bedrockdialog.response;

import java.util.Map;

/**
 * Provides access to the values submitted in an {@link me.f0reach.bedrockdialog.dialog.InputDialog}.
 *
 * <p>All keys correspond to the {@code key} of the {@link me.f0reach.bedrockdialog.input.DialogInput}
 * that was registered in the dialog.</p>
 */
public interface InputResponse {

    /**
     * Returns the text value for a {@link me.f0reach.bedrockdialog.input.TextInput} key.
     *
     * @param key the input key
     * @return the submitted text value
     * @throws IllegalArgumentException if no text value exists for {@code key}
     */
    String getText(String key);

    /**
     * Returns the float value for a {@link me.f0reach.bedrockdialog.input.SliderInput} key.
     *
     * @param key the input key
     * @return the submitted slider value
     * @throws IllegalArgumentException if no float value exists for {@code key}
     */
    float getFloat(String key);

    /**
     * Returns the boolean value for a {@link me.f0reach.bedrockdialog.input.BooleanInput} key.
     *
     * @param key the input key
     * @return the submitted toggle value
     * @throws IllegalArgumentException if no boolean value exists for {@code key}
     */
    boolean getBoolean(String key);

    /**
     * Returns the selected option ID for a {@link me.f0reach.bedrockdialog.input.DropdownInput} key.
     * On Paper, this is the ID set via {@code DropdownInput.Builder.addOption(id, label)}.
     * On Geyser, this is resolved from the selected index.
     *
     * @param key the input key
     * @return the selected option's stable identifier
     * @throws IllegalArgumentException if no dropdown value exists for {@code key}
     */
    String getDropdownOptionId(String key);

    /**
     * Returns the selected option index for a {@link me.f0reach.bedrockdialog.input.DropdownInput} key.
     *
     * @param key the input key
     * @return the 0-based index of the selected option
     * @throws IllegalArgumentException if no dropdown value exists for {@code key}
     */
    int getDropdownIndex(String key);

    /**
     * Reports whether this response contains a value for the given key.
     *
     * @param key the input key
     * @return {@code true} if this response contains a value for the given key
     */
    boolean has(String key);

    /**
     * Returns a mutable builder for constructing an {@link InputResponse}.
     * Used internally by platform adapters.
     *
     * @return a new {@link Builder}
     */
    static Builder builder() {
        return new MapInputResponse.Builder();
    }

    /**
     * Builder for constructing an {@link InputResponse} from raw values.
     * Used internally by platform adapters when mapping platform-specific form
     * submissions back to the unified response shape.
     */
    interface Builder {
        /**
         * Stores a text value under the given key.
         *
         * @param key   the input key
         * @param value the submitted text value
         * @return this builder
         */
        Builder putText(String key, String value);

        /**
         * Stores a slider (float) value under the given key.
         *
         * @param key   the input key
         * @param value the submitted slider value
         * @return this builder
         */
        Builder putFloat(String key, float value);

        /**
         * Stores a boolean toggle value under the given key.
         *
         * @param key   the input key
         * @param value the submitted toggle value
         * @return this builder
         */
        Builder putBoolean(String key, boolean value);

        /**
         * Stores a dropdown selection under the given key.
         *
         * @param key   the input key
         * @param id    selected option's stable identifier
         * @param index selected option's 0-based index
         * @return this builder
         */
        Builder putDropdown(String key, String id, int index);

        /**
         * Builds the immutable {@link InputResponse}.
         *
         * @return a new immutable {@link InputResponse} with the recorded values
         */
        InputResponse build();
    }
}
