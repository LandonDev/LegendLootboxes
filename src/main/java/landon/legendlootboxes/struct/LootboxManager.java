package landon.legendlootboxes.struct;

import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.struct.reward.LBReward;
import landon.legendlootboxes.struct.reward.RewardCategory;
import landon.legendlootboxes.struct.reward.RewardType;
import landon.legendlootboxes.util.ConfMessage;
import landon.legendlootboxes.util.ConfigUtil;
import landon.legendlootboxes.util.LootboxFile;
import landon.legendlootboxes.util.c;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@Getter
public class LootboxManager {
    private List<Lootbox> loadedLootboxes = new ArrayList<>();
    private HashMap<Player, LBReward.RewardBuilder> activeRewardBuilders = new HashMap<>();
    private static volatile LootboxManager instance;

    private final String defaultLootboxOpenMessage;

    private LootboxManager() {
        this.defaultLootboxOpenMessage = ConfMessage.get("default-lootbox-open-message");
    }

    public static LootboxManager get() {
        if(instance == null) {
            synchronized (LootboxManager.class) {
                instance = new LootboxManager();
            }
        }
        return instance;
    }

    public void saveLootbox(Lootbox lootbox) {
        LootboxFile file = lootbox.getFile();
        FileConfiguration config = file.get();
        config.set("reward-count", lootbox.getRewardCount());
        config.set("broadcast.on-open", lootbox.isBroadcastOnOpen());
        config.set("broadcast.each-item-on-open", lootbox.isBroadcastEachItemOnOpen());
        config.set("open-message", lootbox.getOpenMessage());
        config.set("allow-preview", lootbox.isAllowPreview());
        ConfigUtil.storeItem(config, "physical-item", lootbox.getPhysicalItem());
        for (RewardCategory category : lootbox.getRewards().keySet()) {
            for(int i = 0; i < lootbox.getRewards().get(category).size(); i++) {
                LBReward reward = lootbox.getRewards().get(category).get(i);
                String path = "rewards." + category.getInternal() + "." + i;
                config.set(path + ".uuid", reward.getUniqueId().toString());
                config.set(path + ".title", reward.getTitle());
                config.set(path + ".chance", reward.getChance());
                config.set(path + ".type", reward.getType().toString());
                if(reward.getType() == RewardType.COMMAND) {
                    config.set(path + ".command", reward.getCommand());
                }
                if(reward.getType() == RewardType.ITEM) {
                    ConfigUtil.storeItem(config, path + ".item", reward.getItemStack());
                }
            }
        }
        LegendLootboxes.get().getLogger().log(Level.INFO, "Saved lootbox: " + lootbox.getInternalName());
        file.save();
    }

    public void loadLootboxes() {
        for (File file : new File(LegendLootboxes.get().getDataFolder() + "/lootboxes/").listFiles()) {
            LegendLootboxes.get().getServer().getLogger().log(Level.INFO, "Loading file: " + file.getName());
            LootboxFile lbFile = new LootboxFile(LegendLootboxes.get(), file.getName());
            FileConfiguration config = lbFile.get();
            ItemStack physicalItem = ConfigUtil.getStoredItem(config, "physical-item");
            HashMap<RewardCategory, List<LBReward>> rewards = new HashMap<>();
            Lootbox lootbox = new Lootbox(file.getName().split(".yml")[0], physicalItem, rewards);
            LegendLootboxes.get().getServer().getLogger().log(Level.INFO, "Loaded lootbox: " + file.getName());
            int amountOfRewards = config.getInt("reward-count");
            lootbox.setRewardCount(amountOfRewards);
            lootbox.setBroadcastOnOpen(config.getBoolean("broadcast.on-open"));
            lootbox.setBroadcastEachItemOnOpen(config.getBoolean("broadcast.each-item-on-open"));
            lootbox.setOpenMessage(config.getString("open-message"));
            lootbox.setAllowPreview(config.getBoolean("allow-preview"));
            if(config.isSet("rewards")) {
                for (String key : config.getConfigurationSection("rewards").getKeys(false)) {
                    RewardCategory category = this.findRewardCategory(key);
                    for (String s : config.getConfigurationSection("rewards." + key).getKeys(false)) {
                        String path = "rewards." + key + "." + s;
                        LBReward.RewardBuilder builder = LBReward.builder(lootbox);
                        builder.setChance(config.getDouble(path + ".chance"));
                        builder.setRewardCategory(category);
                        builder.setType(RewardType.valueOf(config.getString(path + ".type")));
                        if(config.isSet(path + ".title")) {
                            builder.setTitle(c.c(config.getString(path + ".title")));
                        }
                        if(builder.getType() == RewardType.ITEM) {
                            builder.setItem(ConfigUtil.getStoredItem(config, path + ".item"));
                        }
                        if(builder.getType() == RewardType.COMMAND) {
                            builder.setCommand(config.getString(path + ".command"));
                        }
                        LBReward reward = builder.build();
                        LegendLootboxes.get().getServer().getLogger().log(Level.INFO, "(" + lootbox.getInternalName() + "): Loaded reward: " + reward.getUniqueId().toString());
                    }
                }
            }
            this.loadedLootboxes.add(lootbox);
        }
    }

    public RewardCategory findRewardCategory(String internalName) {
        for (RewardCategory category : LegendLootboxes.get().getLoadedRewardCategories()) {
            if(category.getInternal().equalsIgnoreCase(internalName)) {
                return category;
            }
        }
        return null;
    }

    public Lootbox createLootbox(String internalName) {
        Lootbox lootbox = new Lootbox(internalName, LegendLootboxes.get().getDefaultLootboxItem(), new HashMap<>());
        this.loadedLootboxes.add(lootbox);
        this.saveLootbox(lootbox);
        return lootbox;
    }

    public LBReward findLootboxReward(UUID uuid) {
        for (Lootbox lootbox : this.loadedLootboxes) {
            for (List<LBReward> value : lootbox.getRewards().values()) {
                for (LBReward reward : value) {
                    if(reward.toString().equals(uuid.toString())) {
                        return reward;
                    }
                }
            }
        }
        return null;
    }

    public Lootbox findLootbox(String name, boolean displayName, boolean ignoreCase) {
        if(displayName) {
            for (Lootbox lootbox : this.loadedLootboxes) {
                if(ignoreCase) {
                    if(lootbox.getDisplayName().equalsIgnoreCase(name)) {
                        return lootbox;
                    }
                } else {
                    if(lootbox.getDisplayName().equals(name)) {
                        return lootbox;
                    }
                }
            }
        } else {
            for (Lootbox lootbox : this.loadedLootboxes) {
                if(ignoreCase) {
                    if(lootbox.getInternalName().equalsIgnoreCase(name)) {
                        return lootbox;
                    }
                } else {
                    if(lootbox.getInternalName().equals(name)) {
                        return lootbox;
                    }
                }
            }
        }
        return null;
    }

    public RewardCategory getCategoryBelowCategory(RewardCategory category) {
        for(int i = 0; i < LegendLootboxes.get().getLoadedRewardCategories().size(); i++) {
            RewardCategory found = LegendLootboxes.get().getLoadedRewardCategories().get(i);
            if(found == category) {
                try {
                    return LegendLootboxes.get().getLoadedRewardCategories().get(i + 1);
                } catch (IndexOutOfBoundsException e) {
                    return LegendLootboxes.get().getLoadedRewardCategories().get(0);
                }
            }
        }
        return null;
    }
}
