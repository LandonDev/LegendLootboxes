package landon.legendlootboxes.listeners;

import com.cryptomorin.xseries.XSound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.legendlootboxes.menu.submenus.LBPlayerPreviewMenu;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.util.ConfMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LootboxListeners implements Listener {
    @EventHandler
    public void lootboxOpenListener(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();
        if(item != null && item.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(item);
            if(nbtItem.hasKey("isLootbox")) {
                e.setCancelled(true);
                Lootbox lootbox = LootboxManager.get().findLootbox(nbtItem.getString("lootboxName"), false, false);
                if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if(!lootbox.isAllowPreview() || !player.hasPermission("legendlootboxes.preview")) {
                        player.sendMessage(ConfMessage.get("cannot-preview").replace("%lootbox%", lootbox.getDisplayName()));
                        return;
                    }
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                    LBPlayerPreviewMenu.get(lootbox).open(player);
                } else {
                    if(item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.setItemInHand(null);
                    }
                    lootbox.open(player);
                }
            }
        }
    }
}
