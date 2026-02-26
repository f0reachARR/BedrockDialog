package me.f0reach.bedrockdialog.dialog;

public sealed interface UnifiedDialog
        permits ConfirmDialog, NoticeDialog, MultiButtonDialog, InputDialog {}
