package landon.legendlootboxes.struct;

import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.struct.reward.LBReward;
import landon.legendlootboxes.struct.reward.RewardCategory;
import landon.legendlootboxes.struct.reward.RewardLootTable;
import landon.legendlootboxes.struct.reward.RewardType;
import landon.legendlootboxes.util.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Getter
@Setter
public class Lootbox {
    private String internalName;
    private String displayName;
    private ItemStack physicalItem;
    private int rewardCount;
    private Map<RewardCategory, List<LBReward>> rewards;
    private RewardLootTable<LBReward> lootTable;
    private LootboxFile file;
    private boolean broadcastOnOpen;
    private boolean broadcastEachItemOnOpen;
    private String openMessage;
    private boolean allowPreview;

    public Lootbox(String internalName, ItemStack physicalItem, Map<RewardCategory, List<LBReward>> rewards) {
        this.internalName = internalName;
        this.physicalItem = physicalItem;
        this.displayName = (physicalItem.hasItemMeta() ? (physicalItem.getItemMeta().hasDisplayName() ? physicalItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(physicalItem.getType().toString().toLowerCase().replace("_", " "))) : StringUtils.capitaliseAllWords(physicalItem.getType().toString().toLowerCase().replace("_", " ")));
        this.rewards = rewards;
        this.rewardCount = 1;
        this.broadcastOnOpen = true;
        this.broadcastEachItemOnOpen = true;
        this.allowPreview = true;
        this.openMessage = String.valueOf(LootboxManager.get().getDefaultLootboxOpenMessage());
    }

    public RewardCategory getRewardCategory(LBReward reward) {
        for (RewardCategory category : this.rewards.keySet()) {
            for (LBReward lbReward : this.rewards.get(category)) {
                if (lbReward.equals(reward)) {
                    return category;
                }
            }
        }
        return null;
    }

    public int getRewardSize() {
        int size = 0;
        for (List<LBReward> value : this.rewards.values()) {
            size += value.size();
        }
        return size;
    }

    public LootboxFile getFile() {
        if (this.file == null) {
            this.file = new LootboxFile(LegendLootboxes.get(), this.internalName + ".yml");
        }
        return this.file;
    }

    public boolean deleteReward(LBReward reward) {
        for (List<LBReward> value : this.rewards.values()) {
            for (LBReward lbReward : value) {
                if (lbReward.equals(reward)) {
                    value.remove(reward);
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack buildPhysicalItem() {
        ItemStack stack = this.physicalItem.clone();
        List<String> lore;
        if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
            lore = new ArrayList<>(stack.getItemMeta().getLore());
        } else {
            lore = new ArrayList<>();
        }
        lore.add(c.c(""));
        for (RewardCategory category : this.rewards.keySet()) {
            if(!category.isShowInLore()) {
                continue;
            }
            lore.add(c.c(category.getDisplay()
                    .replace("%items%", this.rewards.get(category).size() + "")
            ));
            for (int i = 0; i < this.rewards.get(category).size(); i++) {
                LBReward reward = this.rewards.get(category).get(i);
                if (reward.getType() == RewardType.ITEM) {
                    ItemStack rewardItemStack = reward.getItemStack();
                    String display = rewardItemStack.hasItemMeta() ? (rewardItemStack.getItemMeta().hasDisplayName() ? rewardItemStack.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(rewardItemStack.getType().toString().toLowerCase().replace("_", " "))) : StringUtils.capitaliseAllWords(rewardItemStack.getType().toString().toLowerCase().replace("_", " "));
                    if (reward.getTitle() == null) {
                        lore.add(c.c(category.getItemFormat()
                                .replace("%item-amount%", rewardItemStack.getAmount() + "")
                                .replace("%item-display-name%", display)
                        ));
                    } else {
                        lore.add(c.c(category.getCommandFormat()
                                .replace("%title%", reward.getTitle())
                        ));
                    }
                }
                if (reward.getType() == RewardType.COMMAND) {
                    if (reward.getTitle() == null && category.getCommandFormat().contains("%title%")) {
                        if (LegendLootboxes.get().isDebug()) {
                            LegendLootboxes.get().getLogger().log(Level.SEVERE, "Lootbox: " + this.internalName + " attempted to use title configuration for a command however the title for the reward was null; SKIPPING reward in lootbox lore.");
                        }
                        continue;
                    }
                    lore.add(c.c(category.getCommandFormat()
                            .replace("%title%", reward.getTitle())
                            .replace("%command%", reward.getCommand())
                    ));
                }
            }
            lore.add(c.c(""));
        }
        lore.remove(lore.size() - 1);
        NBTItem nbtItem = new NBTItem(ItemBuilder.modifyItem(stack, this.displayName, lore));
        nbtItem.setBoolean("isLootbox", true);
        nbtItem.setString("lootboxName", this.internalName);
        return nbtItem.getItem();
    }

    public void setPhysicalItem(ItemStack physicalItem) {
        this.physicalItem = physicalItem;
        this.displayName = (physicalItem.hasItemMeta() ? (physicalItem.getItemMeta().hasDisplayName() ? physicalItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(physicalItem.getType().toString().toLowerCase().replace("_", " "))) : StringUtils.capitaliseAllWords(physicalItem.getType().toString().toLowerCase().replace("_", " ")));
    }

    public LBReward getRandomReward() {
        if (this.lootTable == null) {
            this.lootTable = new RewardLootTable<LBReward>();
            for (RewardCategory category : this.rewards.keySet()) {
                if (category.isGuaranteed()) {
                    continue;
                }
                this.rewards.get(category).forEach(reward -> this.lootTable.addItem(reward, reward.getChance()));
            }
        }
        return this.lootTable.getRandomLoot();
    }

    public void inputReward(LBReward reward, RewardCategory category) {
        if (this.rewards.containsKey(category)) {
            List<LBReward> rewards = new ArrayList<>(this.rewards.get(category));
            rewards.add(reward);
            this.rewards.put(category, rewards);
            return;
        }
        this.rewards.put(category, Lists.newArrayList(new LBReward[]{reward}));
        LootboxManager.get().saveLootbox(this);
    }

    public void delete() {
        LootboxManager.get().getLoadedLootboxes().remove(this);
        this.getFile().delete();
    }

    public void open(Player player) {
        if (this.broadcastOnOpen) {
            Bukkit.broadcastMessage(c.c(ConfMessage.get("open-broadcast")
                    .replace("%player%", player.getName())
                    .replace("%lootbox%", this.displayName)
            ));
        }
        for (RewardCategory category : this.rewards.keySet()) {
            if (category.isGuaranteed()) {
                for (LBReward reward : this.rewards.get(category)) {
                    if (reward.getType() == RewardType.ITEM) {
                        ItemStack stack = reward.getItemStack().clone();
                        GiveUtil.giveOrDropItem(player, stack);
                        if (this.broadcastEachItemOnOpen) {
                            if (reward.getTitle() != null) {
                                Bukkit.broadcastMessage(c.c(ConfMessage.get("reward-each-title-broadcast")
                                        .replace("%title%", reward.getTitle())
                                ));
                            } else {
                                Bukkit.broadcastMessage(c.c(ConfMessage.get("reward-each-item-broadcast")
                                        .replace("%item-amount%", reward.getItemStack().getAmount() + "")
                                        .replace("%item%", LoreUtil.getDisplayName(reward.getItemStack()))
                                ));
                            }
                        }
                    }
                    if (reward.getType() == RewardType.COMMAND) {
                        String command = reward.getCommand();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                        if (this.broadcastEachItemOnOpen) {
                            if (reward.getTitle() != null) {
                                Bukkit.broadcastMessage(c.c(ConfMessage.get("reward-each-title-broadcast")
                                        .replace("%title%", reward.getTitle())
                                ));
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.rewardCount; i++) {
            LBReward reward = this.getRandomReward();
            if (reward.getType() == RewardType.ITEM) {
                ItemStack stack = reward.getItemStack().clone();
                GiveUtil.giveOrDropItem(player, stack);
                if (this.broadcastEachItemOnOpen) {
                    if (reward.getTitle() != null) {
                        Bukkit.broadcastMessage(c.c(ConfMessage.get("reward-each-title-broadcast")
                                .replace("%title%", reward.getTitle())
                        ));
                    } else {
                        Bukkit.broadcastMessage(c.c(ConfMessage.get("reward-each-item-broadcast")
                                .replace("%item-amount%", reward.getItemStack().getAmount() + "")
                                .replace("%item%", LoreUtil.getDisplayName(reward.getItemStack()))
                        ));
                    }
                }
            }
            if (reward.getType() == RewardType.COMMAND) {
                String command = reward.getCommand();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                if (this.broadcastEachItemOnOpen) {
                    if (reward.getTitle() != null) {
                        Bukkit.broadcastMessage(c.c(ConfMessage.get("reward-each-title-broadcast")
                                .replace("%title%", reward.getTitle())
                        ));
                    }
                }
            }
        }
        player.sendMessage(c.c(this.openMessage
                .replace("%lootbox%", this.displayName)
                .replace("%player%", player.getName())));
    }
}
