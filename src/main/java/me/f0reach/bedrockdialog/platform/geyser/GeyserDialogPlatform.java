package me.f0reach.bedrockdialog.platform.geyser;

import me.f0reach.bedrockdialog.dialog.ConfirmDialog;
import me.f0reach.bedrockdialog.dialog.InputDialog;
import me.f0reach.bedrockdialog.dialog.MultiButtonDialog;
import me.f0reach.bedrockdialog.dialog.NoticeDialog;
import me.f0reach.bedrockdialog.input.BooleanInput;
import me.f0reach.bedrockdialog.input.DialogInput;
import me.f0reach.bedrockdialog.input.DropdownInput;
import me.f0reach.bedrockdialog.input.SliderInput;
import me.f0reach.bedrockdialog.input.TextInput;
import me.f0reach.bedrockdialog.platform.DialogPlatform;
import me.f0reach.bedrockdialog.response.InputResponse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.GeyserApi;

import java.util.List;

/**
 * Geyser/Bedrock implementation of {@link DialogPlatform}.
 *
 * <p>Uses Cumulus forms to display UI to Bedrock players.</p>
 *
 * <p><b>Component serialization note:</b> Bedrock Edition renders plain text only;
 * Adventure formatting codes (color, bold, etc.) are stripped.</p>
 *
 * <p><b>SliderInput step note:</b> Bedrock requires integer step values.
 * Float step values are rounded to the nearest integer (minimum 1).</p>
 *
 * <p><b>Thread note:</b> Callbacks are invoked on a network thread.
 * Use {@code Bukkit.getScheduler().runTask(plugin, runnable)} for Bukkit API calls.</p>
 */
public class GeyserDialogPlatform implements DialogPlatform {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private String plain(Component component) {
        return PLAIN.serialize(component);
    }

    @Override
    public void showConfirmDialog(Player player, ConfirmDialog d) {
        String title = plain(d.title());
        String content = d.body() != null ? plain(d.body()) : "";

        ModalForm form = ModalForm.builder()
                .title(title)
                .content(content)
                .button1(d.yesLabel())
                .button2(d.noLabel())
                .validResultHandler((form2, response) -> {
                    if (response.clickedFirst()) {
                        d.onYes().accept(player);
                    } else {
                        d.onNo().accept(player);
                    }
                })
                .closedOrInvalidResultHandler(() -> d.onClose().accept(player))
                .build();

        GeyserApi.api().sendForm(player.getUniqueId(), form);
    }

    @Override
    public void showNoticeDialog(Player player, NoticeDialog d) {
        String title = plain(d.title());
        String content = d.body() != null ? plain(d.body()) : "";

        SimpleForm form = SimpleForm.builder()
                .title(title)
                .content(content)
                .button(d.dismissLabel())
                .validResultHandler((form2, response) -> d.onDismiss().accept(player))
                .closedOrInvalidResultHandler(() -> d.onClose().accept(player))
                .build();

        GeyserApi.api().sendForm(player.getUniqueId(), form);
    }

    @Override
    public void showMultiButtonDialog(Player player, MultiButtonDialog d) {
        String title = plain(d.title());
        String content = d.body() != null ? plain(d.body()) : "";

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(title)
                .content(content);

        List<MultiButtonDialog.DialogButton> buttons = d.buttons();
        for (MultiButtonDialog.DialogButton btn : buttons) {
            builder.button(btn.label());
        }

        SimpleForm form = builder
                .validResultHandler((form2, response) -> {
                    int idx = response.clickedButtonId();
                    if (idx >= 0 && idx < buttons.size()) {
                        buttons.get(idx).onClick().accept(player);
                    }
                })
                .closedOrInvalidResultHandler(() -> d.onClose().accept(player))
                .build();

        GeyserApi.api().sendForm(player.getUniqueId(), form);
    }

    @Override
    public void showInputDialog(Player player, InputDialog d) {
        String title = plain(d.title());

        CustomForm.Builder builder = CustomForm.builder().title(title);

        // index offset: if body is present, a label component is added at index 0
        int offset = 0;
        if (d.body() != null) {
            builder.label(plain(d.body()));
            offset = 1;
        }

        List<DialogInput> inputs = d.inputs();
        for (DialogInput input : inputs) {
            String labelText = plain(input.label());
            switch (input) {
                case TextInput ti -> {
                    String placeholder = ti.placeholder() != null ? ti.placeholder() : "";
                    String defaultVal = ti.defaultValue() != null ? ti.defaultValue() : "";
                    builder.input(labelText, placeholder, defaultVal);
                }
                case SliderInput si -> {
                    // Bedrock requires integer step; round float step to nearest int (min 1)
                    int step = Math.max(1, Math.round(si.step()));
                    builder.slider(labelText, si.min(), si.max(), step, si.defaultValue());
                }
                case BooleanInput bi -> {
                    builder.toggle(labelText, bi.defaultValue());
                }
                case DropdownInput di -> {
                    List<String> optionLabels = di.options().stream()
                            .map(opt -> plain(opt.label()))
                            .toList();
                    if (di.defaultIndex() == 0) {
                        builder.dropdown(labelText, optionLabels);
                    } else {
                        builder.dropdown(labelText, optionLabels, di.defaultIndex());
                    }
                }
            }
        }

        final int finalOffset = offset;
        CustomForm form = builder
                .validResultHandler((form2, response) -> {
                    InputResponse.Builder respBuilder = InputResponse.builder();
                    for (int i = 0; i < inputs.size(); i++) {
                        DialogInput input = inputs.get(i);
                        int idx = finalOffset + i;
                        switch (input) {
                            case TextInput ti -> respBuilder.putText(ti.key(), response.asInput(idx));
                            case SliderInput si -> respBuilder.putFloat(si.key(), response.asSlider(idx));
                            case BooleanInput bi -> respBuilder.putBoolean(bi.key(), response.asToggle(idx));
                            case DropdownInput di -> {
                                int dropIdx = response.asDropdown(idx);
                                String id = di.options().get(dropIdx).id();
                                respBuilder.putDropdown(di.key(), id, dropIdx);
                            }
                        }
                    }
                    d.onSubmit().accept(player, respBuilder.build());
                })
                .closedOrInvalidResultHandler(() -> d.onClose().accept(player))
                .build();

        GeyserApi.api().sendForm(player.getUniqueId(), form);
    }
}
