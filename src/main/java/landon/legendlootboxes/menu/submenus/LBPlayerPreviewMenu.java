package landon.legendlootboxes.menu.submenus;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.struct.reward.LBReward;
import landon.legendlootboxes.struct.reward.RewardCategory;
import landon.legendlootboxes.struct.reward.RewardType;
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

import java.util.ArrayList;
import java.util.List;

public class LBPlayerPreviewMenu implements InventoryProvider {
    public static SmartInventory get(Lootbox lootbox) {
        return SmartInventory.builder()
                .provider(new LBPlayerPreviewMenu())
                .size(6, 9)
                .id("rewardpreview-" + lootbox.getInternalName())
                .manager(LegendLootboxes.get().getInventoryManager())
                .title("Preview Lootbox Rewards")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillRow(5, ClickableItem.empty(ItemBuilder.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "&0")));
        Lootbox lootbox = LootboxManager.get().findLootbox(contents.inventory().getId().split("rewardpreview-")[1], false, false);
        List<LBReward> allRewards = new ArrayList<>();
        for (List<LBReward> value : lootbox.getRewards().values()) {
            for (LBReward allReward : value) {
                allRewards.add(allReward);
            }
        }
        ClickableItem[] items = new ClickableItem[allRewards.size()];
        for (int i = 0; i < allRewards.size(); i++) {
            LBReward reward = allRewards.get(i);
            RewardCategory category = lootbox.getRewardCategory(reward);
            if (reward.getType() == RewardType.ITEM) {
                ItemStack stack = reward.getItemStack().clone();
                items[i] = ClickableItem.empty(ItemBuilder.modifyItem(stack, LoreUtil.getDisplayName(stack), LoreUtil.getAndModifyLore(stack, "", "&8&m------------------------------", "&7Chance: &e" + reward.getChance() + "%", "&7Reward Category: &e" + category.getDisplay()
                        .replace("%items%", lootbox.getRewards().get(category).size() + "")
                )));
            }
            if (reward.getType() == RewardType.COMMAND) {
                ItemStack stack;
                if (reward.getTitle() != null) {
                    stack = ItemBuilder.createItem(XMaterial.COMMAND_BLOCK.parseItem(), reward.getTitle(), "&7Chance: &e" + reward.getChance() + "%", "&7Reward Category: &e" + category.getDisplay().replace("%items%", lootbox.getRewards().get(category).size() + ""));
                } else {
                    continue;
                }
                items[i] = ClickableItem.empty(ItemBuilder.modifyItem(stack, LoreUtil.getDisplayName(stack), LoreUtil.getAndModifyLore(stack, "&8&m------------------------------", "&fLeft-Click &7to &aModify Chance", "&fRight-Click &7to &cDelete Reward")));
            }
        }
        Pagination pagination = contents.pagination();
        pagination.setItems(items);
        pagination.setItemsPerPage(45);
        contents.set(5, 6, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lBack", "&7Click to go back."), e -> {
            LootboxRewardMenu.get(lootbox).open(player, pagination.previous().getPage());
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
        contents.set(5, 8, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lNext Page", "&7Click to go forward."), e -> {
            LootboxRewardMenu.get(lootbox).open(player, pagination.next().getPage());
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        init(player, contents);
    }
}
