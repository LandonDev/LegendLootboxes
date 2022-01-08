package landon.legendlootboxes.util;

import landon.legendlootboxes.struct.Lootbox;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoreUtil {
    public static String getDisplayName(ItemStack physicalItem) {
        return (physicalItem.hasItemMeta() ? (physicalItem.getItemMeta().hasDisplayName() ? physicalItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(physicalItem.getType().toString().toLowerCase().replace("_", " "))) : StringUtils.capitaliseAllWords(physicalItem.getType().toString().toLowerCase().replace("_", " ")));
    }

    public static List<String> getAndModifyLore(Lootbox lootbox, boolean truncated, String... toAdd) {
        List<String> lore;
        if(truncated) {
            ItemStack stack = lootbox.getPhysicalItem().clone();
            if(stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                lore = new ArrayList<>(stack.getItemMeta().getLore());
            } else {
                lore = new ArrayList<>();
            }
            for (String s : toAdd) {
                lore.add(c.c(s));
            }
        } else {
            ItemStack stack = lootbox.buildPhysicalItem().clone();
            if(stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                lore = new ArrayList<>(stack.getItemMeta().getLore());
            } else {
                lore = new ArrayList<>();
            }
            for (String s : toAdd) {
                lore.add(c.c(s));
            }
        }
        return lore;
    }

    public static List<String> getAndModifyList(List<String> currentList, String... toAdd) {
        List<String> list = new ArrayList<>(currentList);
        for (String s : toAdd) {
            list.add(c.c(s));
        }
        return list;
    }

    public static List<String> getAndModifyLore(ItemStack item, String... toAdd) {
        List<String> lore;
        if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
            lore = new ArrayList<>(item.getItemMeta().getLore());
        } else {
            lore = new ArrayList<>();
        }
        for (String s : toAdd) {
            lore.add(c.c(s));
        }
        return lore;
    }
}
