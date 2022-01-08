package landon.legendlootboxes.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.menu.submenus.LootboxEditorMenu;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.util.ItemBuilder;
import landon.legendlootboxes.util.c;
import landon.legendlootboxes.util.textinpututil.CompletePrompt;
import landon.legendlootboxes.util.textinpututil.ResponseType;
import landon.legendlootboxes.util.textinpututil.TextInput;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootboxViewMenu implements InventoryProvider {
    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .title("Legend Lootboxes")
            .id("lootboxViewMenu")
            .manager(LegendLootboxes.get().getInventoryManager())
            .size(6, 9)
            .provider(new LootboxViewMenu())
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        contents.fillRow(5, ClickableItem.empty(ItemBuilder.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "&aPage: " + (pagination.getPage() + 1) + " / " + (pagination.last().getPage() + 1))));
        ClickableItem[] items = new ClickableItem[LootboxManager.get().getLoadedLootboxes().size()];
        for(int i = 0; i < LootboxManager.get().getLoadedLootboxes().size(); i++) {
            Lootbox lootbox = LootboxManager.get().getLoadedLootboxes().get(i);
            ItemStack stack = lootbox.getPhysicalItem().clone();
            List<String> lore;
            if(stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                lore = new ArrayList<>(stack.getItemMeta().getLore());
            } else {
                lore = new ArrayList<>();
            }
            lore.add(c.c(""));
            lore.add(c.c("&aClick to modify this lootbox!"));
            items[i] = ClickableItem.of(ItemBuilder.modifyItem(stack, "&e&lLootbox: &f" + lootbox.getInternalName() + " &8[" + lootbox.getDisplayName() + "&8]", lore), e -> {
                e.setCancelled(true);
                LootboxEditorMenu.get(lootbox).open(player);
                player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
            });
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(45);
        contents.set(5, 4, ClickableItem.of(ItemBuilder.createItem(Material.EMERALD, "&a&lCreate Lootbox", "&7Click to create a new lootbox."), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("What is the name of the lootbox you'd like to create?", ResponseType.STRING)}, prompt -> {
                if(!prompt.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(LegendLootboxes.get(), () -> {
                        player.performCommand("lootbox create " + ((String)prompt.getTextInputs().get(0).getResponse()));
                    }, 1L);
                }
            });
        }));
        contents.set(5, 6, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lBack", "&7Click to go back."), e -> {
            INVENTORY.open(player, pagination.previous().getPage());
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
        contents.set(5, 8, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lNext Page", "&7Click to go forward."), e -> {
            INVENTORY.open(player, pagination.next().getPage());
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
