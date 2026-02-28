package me.f0reach.bedrockdialog.dialog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A dialog with multiple clickable buttons.
 *
 * <p><b>Thread note:</b> Callbacks may be invoked on a network thread (not the main server thread).
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for any Bukkit API calls inside callbacks.</p>
 *
 * <p><b>onClose note:</b> Best-effort. May not fire on Paper (Java Edition).</p>
 */
public non-sealed interface MultiButtonDialog extends UnifiedDialog {

    Component title();

    @Nullable Component body();

    List<DialogButton> buttons();

    /**
     * Called when the dialog is closed without pressing any button.
     * Best-effort: may not fire on Paper (Java Edition).
     */
    Consumer<Player> onClose();

    /**
     * A button in a {@link MultiButtonDialog}.
     *
     * @param label   Display label of the button
     * @param onClick Action to perform when the button is clicked
     */
    record DialogButton(Component label, Consumer<Player> onClick) {}

    static Builder builder() {
        return new Builder();
    }

    final class Builder {
        private Component title = Component.empty();
        private @Nullable Component body = null;
        private final List<DialogButton> buttons = new ArrayList<>();
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

        public Builder button(Component label, Consumer<Player> onClick) {
            this.buttons.add(new DialogButton(label, onClick));
            return this;
        }

        public Builder onClose(Consumer<Player> onClose) {
            this.onClose = onClose;
            return this;
        }

        public MultiButtonDialog build() {
            if (buttons.isEmpty()) {
                throw new IllegalStateException("MultiButtonDialog must have at least one button");
            }
            final Component t = title;
            final @Nullable Component b = body;
            final List<DialogButton> btns = Collections.unmodifiableList(new ArrayList<>(buttons));
            final Consumer<Player> close = onClose;
            return new MultiButtonDialog() {
                @Override public Component title() { return t; }
                @Override public @Nullable Component body() { return b; }
                @Override public List<DialogButton> buttons() { return btns; }
                @Override public Consumer<Player> onClose() { return close; }
            };
        }
    }
}
