package landon.legendlootboxes.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class GiveUtil {
    public static void giveOrDropItem(Player player, ItemStack item) {
        int amountOfItems = 0;
        for(int i = 0; i < 36; i++) {
            if(player.getInventory().getItem(i) != null) {
                amountOfItems++;
            }
        }
        if(amountOfItems >= 36) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(c.c("&c&l(!) &cYour inventory was full so your item was dropped in front of you! (" + (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : org.apache.commons.lang.StringUtils.capitaliseAllWords(item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' '))) : org.apache.commons.lang.StringUtils.capitaliseAllWords(item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' '))) + "&c)"));
        } else {
            player.getInventory().addItem(item);
        }
    }
}
