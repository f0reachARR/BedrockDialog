# BedrockDialog

A Paper plugin library that lets you show interactive dialogs to players on both Java Edition and Bedrock Edition (via Geyser/Floodgate) through a single unified API.

Intended to be used as a **dependency library** by other plugins. The bundled `/bdialog` command and `config.yml` dialog definitions are provided as a demo and for simple use cases only.

## Requirements

- Paper 1.21.8+
- Java 21+
- [Geyser](https://geysermc.org/) and/or [Floodgate](https://wiki.geysermc.org/floodgate/) (optional, for Bedrock Edition support)

## Using as a Library

### 1. Declare the dependency

In your `paper-plugin.yml`:

```yaml
dependencies:
  server:
    BedrockDialog:
      load: BEFORE
      required: true
```

### 2. Initialize

```java
@Override
public void onEnable() {
  // You can omit calling this if you use BedrockDialog as a plugin dependency. In case of shadow(shaded) usage, you must call this method to initialize the API.
  BedrockDialog.init(this); 
}

@Override
public void onDisable() {
  BedrockDialog.reset();
}
```

### 3. Show a dialog

BedrockDialog automatically detects whether the player is on Java or Bedrock Edition and uses the appropriate backend.

```java
UnifiedDialog dialog = ConfirmDialog.builder()
    .title(Component.text("Are you sure?"))
    .body(Component.text("This action cannot be undone."))
    .yesLabel(Component.text("Confirm"))
    .noLabel(Component.text("Cancel"))
    .onYes(player -> {
        // Callbacks may be called off the main thread — schedule Bukkit API calls if needed
        Bukkit.getScheduler().runTask(plugin, () -> {
            // your logic here
        });
    })
    .onNo(player -> {})
    .build();

BedrockDialog.get().show(player, dialog);
```

### 4. Close a dialog

```java
BedrockDialog.get().closeDialog(player);
```

> **Note**: Programmatic close requires Floodgate not Geyser, due to API limitations.

## Dialog Types

| Type                | Description                                               |
|---------------------|-----------------------------------------------------------|
| `ConfirmDialog`     | Yes/No choice                                             |
| `NoticeDialog`      | Informational with a single dismiss button                |
| `MultiButtonDialog` | Multiple clickable buttons                                |
| `InputDialog`       | Form with text, slider, boolean, and dropdown fields      |

### InputDialog and InputResponse

```java
UnifiedDialog dialog = InputDialog.builder()
    .title(Component.text("Item Form"))
    .body(Component.text("Select what you want"))
    .submitLabel(Component.text("Submit"))
    .addInput(TextInput.builder().key("player_name").label("Target Player").build())
    .addInput(SliderInput.builder().key("amount").label("Amount").min(1).max(64).step(1).defaultValue(1).build())
    .addInput(DropdownInput.builder().key("item").label("Item")
        .addOption("diamond", "Diamond")
        .addOption("gold_ingot", "Gold Ingot")
        .build())
    .onSubmit((player, response) -> {
        String target = response.getText("player_name");
        float  amount = response.getFloat("amount");
        String itemId = response.getDropdownOptionId("item");
    })
    .onClose(player -> {})
    .build();
```

#### InputResponse methods

| Method                          | Description                             |
|---------------------------------|-----------------------------------------|
| `getText(key)`                  | Get text field value                    |
| `getFloat(key)`                 | Get slider value                        |
| `getBoolean(key)`               | Get boolean/toggle value                |
| `getDropdownOptionId(key)`      | Get selected dropdown option ID         |
| `getDropdownIndex(key)`         | Get selected dropdown index             |

## Platform Differences

| Feature                     | Java Edition (Paper) | Bedrock Edition (Geyser) |
|-----------------------------|----------------------|--------------------------|
| MiniMessage formatting      | Supported            | Stripped to plain text   |
| On-close callback           | Best-effort          | Not supported            |
| Programmatic close          | Supported            | Requires Floodgate       |
| Slider step (float)         | Supported            | Rounded to integer       |

> Callbacks may be invoked from a network thread on Bedrock Edition. Always schedule Bukkit API calls with `Bukkit.getScheduler().runTask(...)`.

## Demo / Standalone Usage

The plugin ships with a built-in command and `config.yml` for testing dialogs without writing code. This is intended for demonstration and simple use cases.

### Commands

| Command               | Description                       | Permission                      |
|-----------------------|-----------------------------------|---------------------------------|
| `/bdialog open <id>`  | Open a dialog defined in config   | `bedrockdialog.dialog.open`     |
| `/bdialog reload`     | Reload `config.yml`               | `bedrockdialog.dialog.reload`   |

Both permissions default to **OP only**.

### config.yml structure

Dialogs are defined under the `dialogs:` key. Each entry has a `type` field and type-specific options.

#### Notice

```yaml
notice_rules:
  type: notice
  title: "Server Rules"
  body: "1. No griefing\n2. No cheating\n3. Have fun!"
  dismiss_label: "Got it"
  on_dismiss: []
```

#### Confirm

```yaml
confirm_pvp:
  type: confirm
  title: "Join PVP?"
  body: "<player>, do you want to join PVP?"
  yes_label: "Join"
  no_label: "Cancel"
  on_yes:
    - type: run_as_console
      command: "pvp join {player}"
  on_no: []
```

#### Multi-Button

```yaml
welcome:
  type: multi_button
  title: "Welcome!"
  body: "Welcome, <player>!"
  buttons:
    - label: "Go to Spawn"
      actions:
        - type: run_as_player
          command: "spawn"
    - label: "Check Rules"
      actions:
        - type: open_dialog
          dialog_id: "notice_rules"
```

#### Input

```yaml
item_form:
  type: input
  title: "Item Distribution Form"
  body: "Select what you want to receive"
  submit_label: "Submit"
  inputs:
    - type: text
      key: player_name
      label: "Target Player Name"
      placeholder: "e.g. Steve"
      default: ""
    - type: slider
      key: amount
      label: "Amount"
      min: 1
      max: 64
      step: 1
      default: 1
    - type: boolean
      key: checkbox
      label: "Checkbox Test"
      default: true
    - type: dropdown
      key: item
      label: "Item"
      default: 0
      options:
        - id: diamond
          label: "Diamond"
        - id: gold_ingot
          label: "Gold Ingot"
  on_submit:
    - type: run_as_console
      command: "give {input.player_name} {input.item} {input.amount}"
  on_close: []
```

### Config Actions

| Type            | Description                          | Fields      |
|-----------------|--------------------------------------|-------------|
| `run_as_player` | Execute a command as the player      | `command`   |
| `run_as_console`| Execute a command as the console     | `command`   |
| `open_dialog`   | Open another dialog by config ID     | `dialog_id` |

**Placeholders**: `{player}` (player name), `{input.<key>}` (input field value, available in `on_submit`)

## License

MIT License. See [LICENSE](LICENSE) for details.
