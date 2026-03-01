package me.f0reach.bedrockdialog.platform.geyser;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.Form;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.floodgate.api.FloodgateApi;

public class GeyserApiWrapper {
    private static boolean isGeyserAvailable;
    private static boolean isFloodgateAvailable;

    public static void init() {
        try {
            Class.forName("org.geysermc.geyser.api.GeyserApi");
            isGeyserAvailable = GeyserApi.api() != null;
        } catch (Exception e) {
            isGeyserAvailable = false;
        }

        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            isFloodgateAvailable = FloodgateApi.getInstance() != null;
        } catch (Exception e) {
            isFloodgateAvailable = false;
        }
    }

    public static boolean isBedrockFormApiAvailable() {
        return isGeyserAvailable || isFloodgateAvailable;
    }

    public static void sendFormToPlayer(Player player, Form form) {
        if (isGeyserAvailable) {
            GeyserApi.api().sendForm(player.getUniqueId(), form);
        } else if (isFloodgateAvailable) {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        } else {
            throw new IllegalStateException("Neither Geyser nor Floodgate API is available");
        }
    }

    public static boolean isBedrockPlayer(Player player) {
        if (isGeyserAvailable) {
            return GeyserApi.api().isBedrockPlayer(player.getUniqueId());
        } else if (isFloodgateAvailable) {
            return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        } else {
            return false;
        }
    }
}
