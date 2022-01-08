package landon.legendlootboxes.util;

import landon.legendlootboxes.LegendLootboxes;

public class ConfMessage {
    public static String get(String path) {
        return c.c(LegendLootboxes.get().getConfig().getString("messages." + path));
    }

    public static String getNotMessage(String path) {
        return c.c(LegendLootboxes.get().getConfig().getString(path));
    }
}
