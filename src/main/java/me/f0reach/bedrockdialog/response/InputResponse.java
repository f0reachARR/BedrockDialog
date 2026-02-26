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
     */
    String getText(String key);

    /**
     * Returns the float value for a {@link me.f0reach.bedrockdialog.input.SliderInput} key.
     */
    float getFloat(String key);

    /**
     * Returns the boolean value for a {@link me.f0reach.bedrockdialog.input.BooleanInput} key.
     */
    boolean getBoolean(String key);

    /**
     * Returns the selected option ID for a {@link me.f0reach.bedrockdialog.input.DropdownInput} key.
     * On Paper, this is the ID set via {@code DropdownInput.Builder.addOption(id, label)}.
     * On Geyser, this is resolved from the selected index.
     */
    String getDropdownOptionId(String key);

    /**
     * Returns the selected option index for a {@link me.f0reach.bedrockdialog.input.DropdownInput} key.
     */
    int getDropdownIndex(String key);

    /**
     * Returns {@code true} if this response contains a value for the given key.
     */
    boolean has(String key);

    /**
     * Returns a mutable builder for constructing an {@link InputResponse}.
     * Used internally by platform adapters.
     */
    static Builder builder() {
        return new MapInputResponse.Builder();
    }

    /**
     * Builder for constructing an {@link InputResponse} from raw values.
     */
    interface Builder {
        Builder putText(String key, String value);
        Builder putFloat(String key, float value);
        Builder putBoolean(String key, boolean value);
        Builder putDropdown(String key, String id, int index);
        InputResponse build();
    }
}
