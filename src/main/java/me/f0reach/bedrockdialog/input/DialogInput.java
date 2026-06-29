package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;

/**
 * Common supertype for every input field that can be added to an
 * {@link me.f0reach.bedrockdialog.dialog.InputDialog}.
 *
 * <p>
 * Each input has a unique {@link #key()} used to look up its value in the
 * {@link me.f0reach.bedrockdialog.response.InputResponse} delivered to
 * {@code onSubmit}, and a {@link #label()} shown next to the field.
 * </p>
 *
 * <p>
 * Sealed so platform backends can pattern-match the concrete input kind.
 * Build instances via each subtype's {@code builder(key)} factory.
 * </p>
 */
public sealed interface DialogInput
        permits TextInput, SliderInput, BooleanInput, DropdownInput {

    /**
     * {@return the unique key identifying this input within its dialog}
     * Used to look up the submitted value from {@link me.f0reach.bedrockdialog.response.InputResponse}.
     */
    String key();

    /** {@return the display label shown next to the field} */
    Component label();
}
