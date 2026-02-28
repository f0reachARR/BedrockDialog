package me.f0reach.bedrockdialog.platform.paper;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.f0reach.bedrockdialog.dialog.ConfirmDialog;
import me.f0reach.bedrockdialog.dialog.InputDialog;
import me.f0reach.bedrockdialog.dialog.MultiButtonDialog;
import me.f0reach.bedrockdialog.dialog.NoticeDialog;
import me.f0reach.bedrockdialog.input.BooleanInput;
import me.f0reach.bedrockdialog.input.DropdownInput;
import me.f0reach.bedrockdialog.input.SliderInput;
import me.f0reach.bedrockdialog.input.TextInput;
import me.f0reach.bedrockdialog.platform.DialogPlatform;
import me.f0reach.bedrockdialog.response.InputResponse;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Paper (Java Edition) implementation of {@link DialogPlatform}.
 *
 * <p><b>Thread note:</b> Callbacks are invoked on the netty (network) thread.
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for Bukkit API calls.</p>
 *
 * <p><b>onClose note:</b> Paper does not fire an event when a dialog is closed without
 * pressing a button, so {@link ConfirmDialog#onClose()} and {@link InputDialog#onClose()}
 * will not be called on Java Edition.</p>
 */
public class PaperDialogPlatform implements DialogPlatform {

    // NOTE: ClickCallback.Options.DEFAULT may need to be replaced with
    // ClickCallback.Options.builder().build() depending on Paper version.
    private static final ClickCallback.Options CB_OPTIONS = ClickCallback.Options.builder().build();

    @Override
    public void showConfirmDialog(Player player, ConfirmDialog d) {
        ActionButton yesBtn = ActionButton.builder(Component.text(d.yesLabel()))
                .action(DialogAction.customClick(
                        (response, audience) -> {
                            if (audience instanceof Player p) d.onYes().accept(p);
                        },
                        CB_OPTIONS
                ))
                .build();

        ActionButton noBtn = ActionButton.builder(Component.text(d.noLabel()))
                .action(DialogAction.customClick(
                        (response, audience) -> {
                            if (audience instanceof Player p) d.onNo().accept(p);
                        },
                        CB_OPTIONS
                ))
                .build();

        Dialog dialog = buildDialog(
                buildBase(d.title(), d.body(), List.of()),
                DialogType.confirmation(yesBtn, noBtn)
        );
        player.showDialog(dialog);
    }

    @Override
    public void showNoticeDialog(Player player, NoticeDialog d) {
        ActionButton dismissBtn = ActionButton.builder(Component.text(d.dismissLabel()))
                .action(DialogAction.customClick(
                        (response, audience) -> {
                            if (audience instanceof Player p) d.onDismiss().accept(p);
                        },
                        CB_OPTIONS
                ))
                .build();

        Dialog dialog = buildDialog(
                buildBase(d.title(), d.body(), List.of()),
                DialogType.notice(dismissBtn)
        );
        player.showDialog(dialog);
    }

    @Override
    public void showMultiButtonDialog(Player player, MultiButtonDialog d) {
        List<ActionButton> buttons = new ArrayList<>();
        for (MultiButtonDialog.DialogButton btn : d.buttons()) {
            buttons.add(ActionButton.builder(Component.text(btn.label()))
                    .action(DialogAction.customClick(
                            (response, audience) -> {
                                if (audience instanceof Player p) btn.onClick().accept(p);
                            },
                            CB_OPTIONS
                    ))
                    .build());
        }

        Dialog dialog = buildDialog(
                buildBase(d.title(), d.body(), List.of()),
                // multiAction(List) returns MultiActionType.Builder; call build() to finalize
                DialogType.multiAction(buttons).build()
        );
        player.showDialog(dialog);
    }

    @Override
    public void showInputDialog(Player player, InputDialog d) {
        List<DialogInput> paperInputs = toPaperInputs(d);

        ActionButton submitBtn = ActionButton.builder(Component.text(d.submitLabel()))
                .action(DialogAction.customClick(
                        (response, audience) -> {
                            if (!(audience instanceof Player p)) return;
                            InputResponse.Builder builder = InputResponse.builder();
                            for (me.f0reach.bedrockdialog.input.DialogInput input : d.inputs()) {
                                switch (input) {
                                    case TextInput ti -> {
                                        String val = response.getText(ti.key());
                                        builder.putText(ti.key(), val != null ? val : "");
                                    }
                                    case SliderInput si -> {
                                        Float val = response.getFloat(si.key());
                                        builder.putFloat(si.key(), val != null ? val : si.defaultValue());
                                    }
                                    case BooleanInput bi -> {
                                        Boolean val = response.getBoolean(bi.key());
                                        builder.putBoolean(bi.key(), val != null ? val : bi.defaultValue());
                                    }
                                    case DropdownInput di2 -> {
                                        // Paper returns the selected option ID via getText(key)
                                        String selectedId = response.getText(di2.key());
                                        if (selectedId == null) selectedId = "";
                                        int idx = -1;
                                        for (int i = 0; i < di2.options().size(); i++) {
                                            if (di2.options().get(i).id().equals(selectedId)) {
                                                idx = i;
                                                break;
                                            }
                                        }
                                        builder.putDropdown(di2.key(), selectedId, idx);
                                    }
                                }
                            }
                            d.onSubmit().accept(p, builder.build());
                        },
                        CB_OPTIONS
                ))
                .build();

        Dialog dialog = buildDialog(
                buildBase(d.title(), d.body(), paperInputs),
                DialogType.notice(submitBtn)
        );
        player.showDialog(dialog);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private DialogBase buildBase(Component title, @Nullable Component body, List<DialogInput> inputs) {
        DialogBase.Builder builder = DialogBase.builder(title);
        if (body != null) {
            // NOTE: DialogBody.plainText(Component) — adjust if API differs
            builder.body(List.of(DialogBody.plainMessage(body)));
        }
        if (!inputs.isEmpty()) {
            builder.inputs(inputs);
        }
        return builder.build();
    }

    private Dialog buildDialog(DialogBase base, io.papermc.paper.registry.data.dialog.type.DialogType type) {
        return Dialog.create(factory ->
                factory.empty()
                        .base(base)
                        .type(type)
        );
    }

    private List<DialogInput> toPaperInputs(InputDialog d) {
        List<DialogInput> result = new ArrayList<>();
        for (me.f0reach.bedrockdialog.input.DialogInput input : d.inputs()) {
            switch (input) {
                case TextInput ti -> {
                    // Short factory: DialogInput.text(key, label) — may return builder or instance
                    // NOTE: adjust if .build() is required or more params are needed
                    result.add(DialogInput.text(ti.key(), ti.label()).build());
                }
                case SliderInput si -> {
                    // Long factory: (key, width, label, labelFormat, start, end, initial, step)
                    result.add(DialogInput.numberRange(
                            si.key(), 200, si.label(), "options.generic_value",
                            si.min(), si.max(), si.defaultValue(), si.step()
                    ));
                }
                case BooleanInput bi -> {
                    result.add(DialogInput.bool(bi.key(), bi.label(), bi.defaultValue(), "true", "false"));
                }
                case DropdownInput di -> {
                    List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();
                    for (int i = 0; i < di.options().size(); i++) {
                        DropdownInput.DropdownOption opt = di.options().get(i);
                        entries.add(SingleOptionDialogInput.OptionEntry.create(
                                opt.id(), opt.label(), i == di.defaultIndex()));
                    }
                    result.add(DialogInput.singleOption(di.key(), 200, entries, di.label(), true));
                }
            }
        }
        return result;
    }
}
