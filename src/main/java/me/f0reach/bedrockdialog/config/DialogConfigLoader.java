package me.f0reach.bedrockdialog.config;

import me.f0reach.bedrockdialog.BedrockDialog;
import me.f0reach.bedrockdialog.BedrockDialogPlugin;
import me.f0reach.bedrockdialog.dialog.ConfirmDialog;
import me.f0reach.bedrockdialog.dialog.InputDialog;
import me.f0reach.bedrockdialog.dialog.MultiButtonDialog;
import me.f0reach.bedrockdialog.dialog.NoticeDialog;
import me.f0reach.bedrockdialog.dialog.UnifiedDialog;
import me.f0reach.bedrockdialog.input.BooleanInput;
import me.f0reach.bedrockdialog.input.DialogInput;
import me.f0reach.bedrockdialog.input.DropdownInput;
import me.f0reach.bedrockdialog.input.SliderInput;
import me.f0reach.bedrockdialog.input.TextInput;
import me.f0reach.bedrockdialog.response.InputResponse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Parses {@code config.yml} and builds {@link UnifiedDialog} factories for each configured dialog.
 */
public class DialogConfigLoader {

    private final BedrockDialogPlugin plugin;
    private final Map<String, Function<Player, UnifiedDialog>> factories = new LinkedHashMap<>();

    public DialogConfigLoader(BedrockDialogPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads dialog definitions from the plugin's current config.
     * Call {@link BedrockDialogPlugin#reloadConfig()} before this to refresh from disk.
     */
    public void reload() {
        factories.clear();
        ConfigurationSection dialogs = plugin.getConfig().getConfigurationSection("dialogs");
        if (dialogs == null) {
            plugin.getLogger().info("[BedrockDialog] No 'dialogs' section found in config.yml.");
            return;
        }

        for (String id : dialogs.getKeys(false)) {
            ConfigurationSection cfg = dialogs.getConfigurationSection(id);
            if (cfg == null) continue;
            String type = cfg.getString("type", "");
            try {
                Function<Player, UnifiedDialog> factory = switch (type) {
                    case "notice" -> buildNotice(cfg);
                    case "confirm" -> buildConfirm(cfg);
                    case "multi_button" -> buildMultiButton(cfg);
                    case "input" -> buildInput(cfg);
                    default -> {
                        plugin.getLogger().warning("[BedrockDialog] Unknown dialog type '" + type + "' for id '" + id + "' — skipping.");
                        yield null;
                    }
                };
                if (factory != null) {
                    factories.put(id, factory);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[BedrockDialog] Failed to load dialog '" + id + "': " + e.getMessage());
            }
        }

        plugin.getLogger().info("[BedrockDialog] Loaded " + factories.size() + " dialog(s).");
    }

    /**
     * Opens the dialog with the given ID for the player.
     *
     * @return {@code true} if the dialog was found and shown, {@code false} if unknown ID
     */
    public boolean openDialog(Player player, String id) {
        Function<Player, UnifiedDialog> factory = factories.get(id);
        if (factory == null) return false;
        BedrockDialog.get().show(player, factory.apply(player));
        return true;
    }

    /** Returns an unmodifiable view of all loaded dialog IDs (for tab-completion). */
    public Set<String> getDialogIds() {
        return Collections.unmodifiableSet(factories.keySet());
    }

    // ── Dialog builders ────────────────────────────────────────────────────

    private Function<Player, UnifiedDialog> buildNotice(ConfigurationSection cfg) {
        String titleTpl = cfg.getString("title", "");
        String bodyTpl = cfg.getString("body");
        String dismissLabel = cfg.getString("dismiss_label", "OK");
        List<DialogAction> onDismiss = parseActions(cfg.getMapList("on_dismiss"));

        return player -> {
            NoticeDialog.Builder b = NoticeDialog.builder()
                .title(sub(titleTpl, player))
                .dismissLabel(sub(dismissLabel, player))
                .onDismiss(p -> DialogAction.execute(onDismiss, p, plugin, this));
            if (bodyTpl != null) b.body(sub(bodyTpl, player));
            return b.build();
        };
    }

    private Function<Player, UnifiedDialog> buildConfirm(ConfigurationSection cfg) {
        String titleTpl = cfg.getString("title", "");
        String bodyTpl = cfg.getString("body");
        String yesLabel = cfg.getString("yes_label", "Yes");
        String noLabel = cfg.getString("no_label", "No");
        List<DialogAction> onYes = parseActions(cfg.getMapList("on_yes"));
        List<DialogAction> onNo = parseActions(cfg.getMapList("on_no"));

        return player -> {
            ConfirmDialog.Builder b = ConfirmDialog.builder()
                .title(sub(titleTpl, player))
                .yesLabel(sub(yesLabel, player))
                .noLabel(sub(noLabel, player))
                .onYes(p -> DialogAction.execute(onYes, p, plugin, this))
                .onNo(p -> DialogAction.execute(onNo, p, plugin, this));
            if (bodyTpl != null) b.body(sub(bodyTpl, player));
            return b.build();
        };
    }

    private Function<Player, UnifiedDialog> buildMultiButton(ConfigurationSection cfg) {
        String titleTpl = cfg.getString("title", "");
        String bodyTpl = cfg.getString("body");

        record ButtonSpec(String label, List<DialogAction> actions) {}
        List<ButtonSpec> buttons = new ArrayList<>();
        for (Map<?, ?> map : cfg.getMapList("buttons")) {
            String label = toString(mapGet(map, "label", ""));
            buttons.add(new ButtonSpec(label, parseActions(nestedMapList(map, "actions"))));
        }

        if (buttons.isEmpty()) {
            plugin.getLogger().warning("[BedrockDialog] multi_button dialog has no buttons — adding fallback OK button.");
        }

        return player -> {
            MultiButtonDialog.Builder b = MultiButtonDialog.builder()
                .title(sub(titleTpl, player));
            if (bodyTpl != null) b.body(sub(bodyTpl, player));
            for (ButtonSpec btn : buttons) {
                b.button(sub(btn.label(), player), p -> DialogAction.execute(btn.actions(), p, plugin, this));
            }
            if (buttons.isEmpty()) b.button(sub("OK"), p -> {});
            return b.build();
        };
    }

    private Function<Player, UnifiedDialog> buildInput(ConfigurationSection cfg) {
        String titleTpl = cfg.getString("title", "");
        String bodyTpl = cfg.getString("body");
        String submitLabel = cfg.getString("submit_label", "Submit");
        List<DialogAction> onSubmitActions = parseActions(cfg.getMapList("on_submit"));
        List<DialogAction> onCloseActions = parseActions(cfg.getMapList("on_close"));

        List<InputSpec> specs = new ArrayList<>();
        for (Map<?, ?> map : cfg.getMapList("inputs")) {
            InputSpec spec = parseInputSpec(map);
            if (spec != null) specs.add(spec);
        }

        return player -> {
            InputDialog.Builder b = InputDialog.builder()
                .title(sub(titleTpl, player))
                .submitLabel(sub(submitLabel, player));
            if (bodyTpl != null) b.body(sub(bodyTpl, player));
            for (InputSpec spec : specs) b.addInput(spec.buildInput());
            b.onSubmit((p, response) -> {
                Map<String, String> inputValues = new LinkedHashMap<>();
                for (InputSpec spec : specs) {
                    if (response.has(spec.key())) {
                        inputValues.put(spec.key(), spec.valueAsString(response));
                    }
                }
                DialogAction.execute(onSubmitActions, p, plugin, this, inputValues);
            });
            if (!onCloseActions.isEmpty()) {
                b.onClose(p -> DialogAction.execute(onCloseActions, p, plugin, this));
            }
            return b.build();
        };
    }

    // ── InputSpec types ────────────────────────────────────────────────────

    private interface InputSpec {
        String key();
        DialogInput buildInput();
        String valueAsString(InputResponse response);
    }

    private record TextSpec(String key, String label, String placeholder, String defaultValue)
            implements InputSpec {
        @Override public DialogInput buildInput() {
            TextInput.Builder b = TextInput.builder(key).label(sub(label));
            if (!placeholder.isEmpty()) b.placeholder(placeholder);
            if (!defaultValue.isEmpty()) b.defaultValue(defaultValue);
            return b.build();
        }
        @Override public String valueAsString(InputResponse r) { return r.getText(key); }
    }

    private record SliderSpec(String key, String label, float min, float max, float step, float defaultValue)
            implements InputSpec {
        @Override public DialogInput buildInput() {
            return SliderInput.builder(key).label(sub(label))
                .min(min).max(max).step(step).defaultValue(defaultValue).build();
        }
        @Override public String valueAsString(InputResponse r) {
            float v = r.getFloat(key);
            return v == (int) v ? String.valueOf((int) v) : String.valueOf(v);
        }
    }

    private record BooleanSpec(String key, String label, boolean defaultValue)
            implements InputSpec {
        @Override public DialogInput buildInput() {
            return BooleanInput.builder(key).label(sub(label)).defaultValue(defaultValue).build();
        }
        @Override public String valueAsString(InputResponse r) { return String.valueOf(r.getBoolean(key)); }
    }

    private record DropdownSpec(String key, String label, int defaultIndex,
                                List<DropdownInput.DropdownOption> options)
            implements InputSpec {
        @Override public DialogInput buildInput() {
            DropdownInput.Builder b = DropdownInput.builder(key)
                .label(sub(label)).defaultIndex(defaultIndex);
            for (DropdownInput.DropdownOption opt : options) b.addOption(opt.id(), opt.label());
            return b.build();
        }
        @Override public String valueAsString(InputResponse r) { return r.getDropdownOptionId(key); }
    }

    private @Nullable InputSpec parseInputSpec(Map<?, ?> map) {
        String type = (String) map.get("type");
        String key = (String) map.get("key");
        if (key == null) {
            plugin.getLogger().warning("[BedrockDialog] Input is missing 'key' field — skipping.");
            return null;
        }
        String label = toString(mapGet(map, "label", ""));

        return switch (type == null ? "" : type) {
            case "text" -> new TextSpec(
                key, label,
                toString(mapGet(map, "placeholder", "")),
                toString(mapGet(map, "default", ""))
            );
            case "slider" -> new SliderSpec(
                key, label,
                toFloat(mapGet(map, "min", 0)),
                toFloat(mapGet(map, "max", 100)),
                toFloat(mapGet(map, "step", 1)),
                toFloat(mapGet(map, "default", 0))
            );
            case "boolean" -> new BooleanSpec(
                key, label,
                toBool(mapGet(map, "default", false))
            );
            case "dropdown" -> {
                List<DropdownInput.DropdownOption> opts = nestedMapList(map, "options").stream()
                    .map(m -> new DropdownInput.DropdownOption(
                        toString(m.get("id")),
                        sub(toString(mapGet(m, "label", "")))))
                    .toList();
                yield new DropdownSpec(key, label, toInt(mapGet(map, "default", 0)), opts);
            }
            default -> {
                plugin.getLogger().warning("[BedrockDialog] Unknown input type '" + type + "' for key '" + key + "' — skipping.");
                yield null;
            }
        };
    }

    // ── Action parsing ─────────────────────────────────────────────────────

    private List<DialogAction> parseActions(List<Map<?, ?>> list) {
        List<DialogAction> result = new ArrayList<>();
        for (Map<?, ?> map : list) {
            String type = toString(map.get("type"));
            switch (type) {
                case "run_as_player" ->
                    result.add(new DialogAction.RunAsPlayer(toString(map.get("command"))));
                case "run_as_console" ->
                    result.add(new DialogAction.RunAsConsole(toString(map.get("command"))));
                case "open_dialog" ->
                    result.add(new DialogAction.OpenDialog(toString(map.get("dialog_id"))));
                default ->
                    plugin.getLogger().warning("[BedrockDialog] Unknown action type '" + type + "' — skipping.");
            }
        }
        return result;
    }

    // ── Utilities ──────────────────────────────────────────────────────────

    private static MiniMessage MM = MiniMessage.miniMessage();

    private static Component sub(String template, Player player) {
        return MM.deserialize(template, Placeholder.unparsed("player", player.getName()));
    }

    private static Component sub(String template) {
        return MM.deserialize(template);
    }

    @SuppressWarnings("unchecked")
    private static List<Map<?, ?>> nestedMapList(Map<?, ?> map, String key) {
        Object val = map.get(key);
        if (val instanceof List<?> list) return (List<Map<?, ?>>) list;
        return List.of();
    }

    /** {@code Map<?,?>.getOrDefault} workaround: returns the value for key, or defaultValue if absent/null. */
    private static Object mapGet(Map<?, ?> map, String key, Object defaultValue) {
        Object val = map.get(key);
        return val != null ? val : defaultValue;
    }

    private static String toString(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    private static float toFloat(Object obj) {
        if (obj instanceof Number n) return n.floatValue();
        return 0f;
    }

    private static int toInt(Object obj) {
        if (obj instanceof Number n) return n.intValue();
        return 0;
    }

    private static boolean toBool(Object obj) {
        if (obj instanceof Boolean b) return b;
        return false;
    }
}
