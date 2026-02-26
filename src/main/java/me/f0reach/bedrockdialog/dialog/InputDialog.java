package me.f0reach.bedrockdialog.dialog;

import me.f0reach.bedrockdialog.input.DialogInput;
import me.f0reach.bedrockdialog.response.InputResponse;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A form-style dialog with multiple typed input fields.
 *
 * <p><b>Thread note:</b> Callbacks may be invoked on a network thread (not the main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for any Bukkit API calls inside callbacks.</p>
 *
 * <p><b>onClose note:</b> Best-effort. May not fire on Paper (Java Edition).</p>
 */
public non-sealed interface InputDialog extends UnifiedDialog {

    Component title();

    @Nullable Component body();

    List<DialogInput> inputs();

    String submitLabel();

    BiConsumer<Player, InputResponse> onSubmit();

    /**
     * Called when the dialog is closed without submitting.
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private final List<DialogInput> inputs = new ArrayList<>();
        private String submitLabel = "Submit";
        private BiConsumer<Player, InputResponse> onSubmit = (p, r) -> {};
        private Consumer<Player> onClose = p -> {};

        private Builder() {}

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder body(Component body) {
            this.body = body;
            return this;
        }

        public Builder addInput(DialogInput input) {
            this.inputs.add(input);
            return this;
        }

        public Builder submitLabel(String submitLabel) {
            this.submitLabel = submitLabel;
            return this;
        }

        public Builder onSubmit(BiConsumer<Player, InputResponse> onSubmit) {
            this.onSubmit = onSubmit;
            return this;
        }

        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        public InputDialog build() {
            Set<String> seen = new HashSet<>();
            for (DialogInput input : inputs) {
                if (!seen.add(input.key())) {
                    throw new IllegalStateException(
                            "Duplicate input key in InputDialog: '" + input.key() + "'");
                }
            }
            final Component t = title;
            final @Nullable Component b = body;
            final List<DialogInput> ins = Collections.unmodifiableList(new ArrayList<>(inputs));
            final String sl = submitLabel;
            final BiConsumer<Player, InputResponse> submit = onSubmit;
            final Consumer<Player> close = onClose;
            return new InputDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public List<DialogInput> inputs() { return ins; }
                @Override public String submitLabel() { return sl; }
                @Override public BiConsumer<Player, InputResponse> onSubmit() { return submit; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
