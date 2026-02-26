package me.f0reach.bedrockdialog.input;

import net.kyori.adventure.text.Component;

public sealed interface DialogInput
        permits TextInput, SliderInput, BooleanInput, DropdownInput {

    String key();

    Component label();
}
