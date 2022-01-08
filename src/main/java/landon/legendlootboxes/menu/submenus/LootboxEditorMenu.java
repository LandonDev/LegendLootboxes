package landon.legendlootboxes.menu.submenus;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.menu.LootboxViewMenu;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.util.GiveUtil;
import landon.legendlootboxes.util.ItemBuilder;
import landon.legendlootboxes.util.LoreUtil;
import landon.legendlootboxes.util.c;
import landon.legendlootboxes.util.textinpututil.CompletePrompt;
import landon.legendlootboxes.util.textinpututil.ResponseType;
import landon.legendlootboxes.util.textinpututil.TextInput;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class LootboxEditorMenu implements InventoryProvider {
    public static SmartInventory get(Lootbox lootbox) {
        return SmartInventory.builder()
                .provider(new LootboxEditorMenu())
                .size(2, 9)
                .id("editormenu-" + lootbox.getInternalName())
                .manager(LegendLootboxes.get().getInventoryManager())
                .title("Edit Lootbox: " + lootbox.getInternalName())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Lootbox lootbox = LootboxManager.get().findLootbox(contents.inventory().getId().split("editormenu-")[1], false, false);
        contents.fill(ClickableItem.empty(ItemBuilder.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "&0")));
        contents.set(0, 0, ClickableItem.of(ItemBuilder.modifyItem(lootbox.getPhysicalItem().clone(), lootbox.getDisplayName(), LoreUtil.getAndModifyLore(lootbox, true, "", "&8&m------------------------------", "&fLeft-Click without item &7to &aGet Lootbox","&fLeft-Click with item &7to &aReplace Physical Item", "&fRight-Click &7to &cReset Physical Item")), e -> {
            e.setCancelled(true);
            if(e.getClick() == ClickType.LEFT) {
                ItemStack cursor = e.getCursor();
                if(cursor == null || cursor.getType() == Material.AIR) {
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                    GiveUtil.giveOrDropItem(player, lootbox.buildPhysicalItem());
                    return;
                }
                lootbox.setPhysicalItem(cursor.clone());
                player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
            }
            if(e.getClick() == ClickType.RIGHT) {
                player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 1.0F, 1.0F);
                lootbox.setPhysicalItem(LegendLootboxes.get().getDefaultLootboxItem());
                player.sendMessage(c.c("&aYou have reset the Lootbox to its default item."));
            }
        }));
        contents.set(0, 1, ClickableItem.of(ItemBuilder.createItem(XMaterial.COMMAND_BLOCK.parseItem(), "&e&lSave to Disk", "&7Click to save lootbox data to disk.", "", "&c&lNOTE: &7This will automatically happen", "&7after crucial changes and on server shutdown."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1.0F, 1.0F);
            contents.inventory().close(player);
            LootboxManager.get().saveLootbox(lootbox);
            player.sendMessage(c.c("&aYou successfully saved the lootbox to the disk (&7" + ThreadLocalRandom.current().nextInt(0, 50) + "ms&a)"));
        }));
        contents.set(0, 2, ClickableItem.of(ItemBuilder.createItem(XMaterial.NETHER_STAR.parseItem(), "&e&lModify Rewards (&f" + lootbox.getRewardSize() + "&e&l)", "&7Click to view/add/remove", "&7lootbox rewards."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
            LootboxRewardMenu.get(lootbox).open(player);
        }));
        contents.set(0, 3, ClickableItem.of(ItemBuilder.createItem(XMaterial.PAPER.parseItem(), "&e&lInternal Name", "&7Current Name: &e" + lootbox.getInternalName(), "&7Click to change the internal name."), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
            CompletePrompt.create(player, new TextInput[]{new TextInput("Type the new internal name you wish to have.", ResponseType.STRING)}, prompt -> {
                if(!prompt.isCancelled()) {
                    lootbox.setInternalName((String)prompt.getTextInputs().get(0).getResponse());
                    LootboxEditorMenu.get(lootbox).open(player);
                }
            });
        }));
        contents.set(0, 4, ClickableItem.of(ItemBuilder.createItem(XMaterial.REDSTONE_TORCH.parseMaterial(), "&e&lAmount of Rewards", lootbox.getRewardCount(), 0, "&7Current Amount: &e" + lootbox.getRewardCount(), "&7Click to change the amount."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("Type the amount of rewards you want the lootbox to give.", ResponseType.NUMBER)}, prompt -> {
                if(!prompt.isCancelled()) {
                    int amount = (int) prompt.getTextInputs().get(0).getResponse();
                    lootbox.setRewardCount(amount);
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                    LootboxEditorMenu.get(lootbox).open(player);
                }
            });
        }));
        contents.set(0, 5, ClickableItem.of(ItemBuilder.createItem(XMaterial.MAP.parseItem(), "&e&lBroadcast Opening", "&7Current Setting: &e" + (lootbox.isBroadcastOnOpen() ? "true" : "false"), "&7Click to swap."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1.0F, 1.0F);
            lootbox.setBroadcastOnOpen(lootbox.isBroadcastOnOpen() ? false : true);
            init(player, contents);
        }));
        contents.set(0, 6, ClickableItem.of(ItemBuilder.createItem(XMaterial.MAP.parseItem(), "&e&lBroadcast Each Item on Opening", "&7Current Setting: &e" + (lootbox.isBroadcastEachItemOnOpen() ? "true" : "false"), "&7This option will announce each", "&7item a player receives on opening.", "&7Click to swap."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1.0F, 1.0F);
            lootbox.setBroadcastEachItemOnOpen(lootbox.isBroadcastEachItemOnOpen() ? false : true);
            init(player, contents);
        }));
        contents.set(0, 7, ClickableItem.of(ItemBuilder.createItem(XMaterial.STRING.parseItem(), "&e&lOpen Message", "&7Current Message:", "&e" + lootbox.getOpenMessage(), "&7This message is sent to the player", "&7when they open this lootbox.", "&7Click to change this message."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1.0F, 1.0F);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("What is the message you'd like the player to see when they open this lootbox?", ResponseType.STRING)}, prompt -> {
                if(!prompt.isCancelled()) {
                    String response = (String) prompt.getTextInputs().get(0).getResponse();
                    lootbox.setOpenMessage(c.c(response));
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                    LootboxEditorMenu.get(lootbox).open(player);
                }
            });
        }));
        contents.set(0, 8, ClickableItem.of(ItemBuilder.createItem(XMaterial.PAINTING.parseItem(), "&e&lAllow Previewing Rewards", "&7Current Setting: &e" + (lootbox.isAllowPreview() ? "true" : "false"), "&7If this is enabled, users can left-click", "&7a lootbox to preview its rewards."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1.0F, 1.0F);
            lootbox.setAllowPreview(lootbox.isAllowPreview() ? false : true);
            init(player, contents);
        }));
        contents.set(1, 8, ClickableItem.of(ItemBuilder.createItem(XMaterial.RED_STAINED_GLASS.parseItem(), "&cBack to previous menu"), e -> {
            e.setCancelled(true);
            LootboxViewMenu.INVENTORY.open(player);
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        init(player, contents);
    }
}
