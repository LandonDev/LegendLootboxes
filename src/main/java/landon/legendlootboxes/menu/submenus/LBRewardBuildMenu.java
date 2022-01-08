package landon.legendlootboxes.menu.submenus;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.struct.reward.LBReward;
import landon.legendlootboxes.struct.reward.RewardCategory;
import landon.legendlootboxes.struct.reward.RewardType;
import landon.legendlootboxes.util.GiveUtil;
import landon.legendlootboxes.util.ItemBuilder;
import landon.legendlootboxes.util.LoreUtil;
import landon.legendlootboxes.util.c;
import landon.legendlootboxes.util.textinpututil.CompletePrompt;
import landon.legendlootboxes.util.textinpututil.ResponseType;
import landon.legendlootboxes.util.textinpututil.TextInput;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LBRewardBuildMenu implements InventoryProvider {
    public static SmartInventory get(Lootbox lootbox) {
        return SmartInventory.builder()
                .provider(new LBRewardBuildMenu())
                .size(1, 9)
                .id("rewardbuilder-" + lootbox.getInternalName())
                .manager(LegendLootboxes.get().getInventoryManager())
                .title("Create Lootbox Reward")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Lootbox lootbox = LootboxManager.get().findLootbox(contents.inventory().getId().split("rewardbuilder-")[1], false, false);
        LBReward.RewardBuilder notFinalBuilder;
        if (LootboxManager.get().getActiveRewardBuilders().containsKey(player)) {
            notFinalBuilder = LootboxManager.get().getActiveRewardBuilders().get(player);
            if (!notFinalBuilder.getLootbox().getInternalName().equals(lootbox.getInternalName())) {
                notFinalBuilder = LBReward.builder(lootbox);
                LootboxManager.get().getActiveRewardBuilders().put(player, notFinalBuilder);
                player.sendMessage(c.c("&cThere was an error opening the builder. Detected previous builder instance yet builder lootbox != inv lootbox."));
            }
        } else {
            notFinalBuilder = LBReward.builder(lootbox);
            LootboxManager.get().getActiveRewardBuilders().put(player, notFinalBuilder);
        }
        LBReward.RewardBuilder builder = notFinalBuilder;
        contents.fill(ClickableItem.empty(ItemBuilder.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "&0")));
        contents.set(0, 0, ClickableItem.of(ItemBuilder.createItem(XMaterial.RED_STAINED_GLASS.parseItem(), "&cBack to previous menu"), e -> {
            e.setCancelled(true);
            LootboxRewardMenu.get(lootbox).open(player);
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
        contents.set(0, 1, ClickableItem.of(ItemBuilder.createItem(XMaterial.REDSTONE.parseItem(), "&c&lDelete Reward Builder", "&7Click to delete this reward builder."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 1.0F, 1.0F);
            LootboxManager.get().getActiveRewardBuilders().remove(player);
            LootboxRewardMenu.get(lootbox).open(player);
        }));
        if (builder.getType() == RewardType.ITEM) {
            ItemStack stack;
            if (builder.getStack() != null) {
                stack = ItemBuilder.modifyItem(builder.getStack().clone(), LoreUtil.getDisplayName(builder.getStack()), LoreUtil.getAndModifyLore(builder.getStack(), "&8&m------------------------------", "&fClick &7to &cUnset Item"));
                contents.set(0, 4, ClickableItem.of(stack, e -> {
                    e.setCancelled(true);
                    builder.setItem(null);
                    player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 1.0F, 1.0F);
                    init(player, contents);
                }));
            } else {
                stack = ItemBuilder.createItem(XMaterial.BARRIER.parseItem(), "&cNo Item", "&7Drag n' drop an item here", "&7to set it as the reward.");
                contents.set(0, 4, ClickableItem.of(stack, e -> {
                    e.setCancelled(true);
                    ItemStack cursor = e.getCursor().clone();
                    if (e.getCursor() == null || cursor.getType() == Material.AIR) {
                        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_BASS.parseSound(), 1.0F, 1.0F);
                        player.sendMessage(c.c("&cYou need an item in your cursor!"));
                        return;
                    }
                    builder.setItem(cursor.clone());
                    GiveUtil.giveOrDropItem((Player)e.getWhoClicked(), cursor.clone());
                    e.getWhoClicked().setItemOnCursor(null);
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                    init(player, contents);
                }));
            }
        }
        if (builder.getType() == RewardType.COMMAND) {
            ItemStack stack;
            if (builder.getCommand() != null) {
                stack = ItemBuilder.createItem(XMaterial.COMMAND_BLOCK.parseItem(), "&e&lCommand", "&7Current Command: " + "&f/" + builder.getCommand() + "", "&7Click to change the command.");
            } else {
                stack = ItemBuilder.createItem(XMaterial.COMMAND_BLOCK.parseItem(), "&e&lCommand", "&7Current Command: &fNone", "&7Click to set the command.");
            }
            contents.set(0, 4, ClickableItem.of(stack, e -> {
                e.setCancelled(true);
                contents.inventory().close(player);
                CompletePrompt.create(player, new TextInput[]{new TextInput("Type the command you wish to set.", ResponseType.STRING)}, prompt -> {
                    if (!prompt.isCancelled()) {
                        String command = (String) prompt.getTextInputs().get(0).getResponse();
                        builder.setCommand(command);
                        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                        LBRewardBuildMenu.get(lootbox).open(player);
                    }
                });
            }));
        }
        ItemStack typeDisplay = null;
        if (builder.getType() == RewardType.ITEM) {
            typeDisplay = ItemBuilder.createItem(XMaterial.NETHER_STAR.parseItem(), "&e&lReward Type", "", "&f&l* &b&lItem", "&f&l* &7Command", "", "&7Click to swap item types.");
        }
        if (builder.getType() == RewardType.COMMAND) {
            typeDisplay = ItemBuilder.createItem(XMaterial.NETHER_STAR.parseItem(), "&e&lReward Type", "", "&f&l* &7Item", "&f&l* &b&lCommand", "", "&7Click to swap item types.");
        }
        contents.set(0, 3, ClickableItem.of(typeDisplay, e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1.0F, 1.0F);
            builder.setType(builder.getType() == RewardType.ITEM ? RewardType.COMMAND : RewardType.ITEM);
            init(player, contents);
        }));
        contents.set(0, 5, ClickableItem.of(ItemBuilder.createItem(XMaterial.NAME_TAG.parseItem(), "&e&lViewable Title", builder.getTitle() == null ? "&7Current Title: &eNone" : "&7Current Title: &e" + builder.getTitle(), "&7Click to change or add a title."), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("Type the new title you wish to be publicly displayed for this item. Type 'none' to remove the current title.", ResponseType.STRING)}, prompt -> {
                if (!prompt.isCancelled()) {
                    String title = (String) prompt.getTextInputs().get(0).getResponse();
                    if (title.equalsIgnoreCase("none")) {
                        builder.setTitle(null);
                    } else {
                        builder.setTitle(title);
                    }
                    LBRewardBuildMenu.get(lootbox).open(player);
                }
            });
        }));
        contents.set(0, 6, ClickableItem.of(ItemBuilder.createItem(XMaterial.PAPER.parseItem(), "&e&lChance", "&7Current Chance: &e" + builder.getChance() + "%", "&7Click to change the chance."), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("What is the new chance you'd like for this reward?", ResponseType.PERCENTAGE)}, prompt -> {
                if(!prompt.isCancelled()) {
                    double chance = (double) (prompt.getTextInputs().get(0).getResponse());
                    builder.setChance(chance);
                    LBRewardBuildMenu.get(lootbox).open(prompt.getPlayer());
                }
            });
        }));
        List<String> categoryLore = new ArrayList<>();
        categoryLore.add(c.c(""));
        for (RewardCategory rewardCategory : LegendLootboxes.get().getLoadedRewardCategories()) {
            String name = rewardCategory.isGuaranteed() ? rewardCategory.getInternal().toLowerCase() + " &7(guaranteed loot)" : rewardCategory.getInternal().toLowerCase();
            if(builder.getRewardCategory() == rewardCategory) {
                categoryLore.add(c.c("&f&l* &b&l" + StringUtils.capitalize(name)));
            } else {
                categoryLore.add(c.c("&f&l* &7" + StringUtils.capitalize(name)));
            }
        }
        categoryLore.add(c.c(""));
        categoryLore.add(c.c("&7Click to toggle between categories."));
        contents.set(0, 7, ClickableItem.of(ItemBuilder.createItem(XMaterial.MAP.parseItem(), "&e&lReward Category", categoryLore), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1.0F, 1.0F);
            if(builder.getRewardCategory() != null) {
                builder.setRewardCategory(LootboxManager.get().getCategoryBelowCategory(builder.getRewardCategory()));
            } else {
                builder.setRewardCategory(LegendLootboxes.get().getLoadedRewardCategories().get(0));
            }
            init(player, contents);
        }));
        if(builder.isComplete()) {
            contents.set(0, 8, ClickableItem.of(ItemBuilder.createItem(XMaterial.EMERALD.parseItem(), "&a&lCreate Reward", "&7Click to create the reward", "&7with the options specified."), e -> {
                e.setCancelled(true);
                player.playSound(player.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1.0F, 1.0F);
                builder.build();
                LootboxManager.get().getActiveRewardBuilders().remove(player);
                LootboxRewardMenu.get(lootbox).open(player);
            }));
        } else {
            contents.set(0, 8, ClickableItem.empty(ItemBuilder.createItem(XMaterial.RED_DYE.parseItem(), "&c&lCannot Create Reward", "&7You have not filled out all", "&7required options to create this reward.", "", "&7The following options need to be filled out for a reward to be created...", "&f&l* &7Reward Type", "&f&l* &7Item OR Command", "&f&l* &7Chance", "&f&l* &7Reward Category")));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
