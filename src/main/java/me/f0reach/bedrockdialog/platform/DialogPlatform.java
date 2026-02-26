package me.f0reach.bedrockdialog.platform;

import me.f0reach.bedrockdialog.dialog.ConfirmDialog;
import me.f0reach.bedrockdialog.dialog.InputDialog;
import me.f0reach.bedrockdialog.dialog.MultiButtonDialog;
import me.f0reach.bedrockdialog.dialog.NoticeDialog;
import org.bukkit.entity.Player;

public interface DialogPlatform {

    void showConfirmDialog(Player player, ConfirmDialog dialog);

    void showNoticeDialog(Player player, NoticeDialog dialog);

    void showMultiButtonDialog(Player player, MultiButtonDialog dialog);

    void showInputDialog(Player player, InputDialog dialog);
}
