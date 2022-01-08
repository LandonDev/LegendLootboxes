package landon.legendlootboxes.struct.reward;

import landon.legendlootboxes.struct.Lootbox;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class LBReward {
    private RewardType type;
    private String command;
    private String title;
    private ItemStack itemStack;
    private double chance;
    private UUID uniqueId;
    private Lootbox lootbox;

    public LBReward(RewardType type, String command, String title, double chance, Lootbox lootbox) {
        this.type = type;
        this.command = command;
        this.chance = chance;
        this.title = title;
        this.uniqueId = UUID.randomUUID();
        this.lootbox = lootbox;
    }

    public LBReward(RewardType type, ItemStack stack, String title, double chance, Lootbox lootbox) {
        this.type = type;
        this.itemStack = stack;
        this.chance = chance;
        this.title = title;
        this.uniqueId = UUID.randomUUID();
        this.lootbox = lootbox;
    }

    public LBReward(RewardType type, String command, String title, double chance, Lootbox lootbox, UUID uuid) {
        this.type = type;
        this.command = command;
        this.chance = chance;
        this.title = title;
        this.uniqueId = uuid;
        this.lootbox = lootbox;
    }

    public LBReward(RewardType type, ItemStack stack, String title, double chance, Lootbox lootbox, UUID uuid) {
        this.type = type;
        this.itemStack = stack;
        this.chance = chance;
        this.title = title;
        this.uniqueId = uuid;
        this.lootbox = lootbox;
    }

    public boolean equals(LBReward other) {
        return this.uniqueId.toString().equals(other.getUniqueId().toString());
    }

    public static RewardBuilder builder(Lootbox lootbox) {
        return new RewardBuilder(RewardType.ITEM, 5.0D, lootbox);
    }

    @Getter
    public static final class RewardBuilder {
        private RewardType type;
        private String title;
        private ItemStack stack;
        private String command;
        private double chance;
        private Lootbox lootbox;
        private RewardCategory rewardCategory;

        private RewardBuilder(RewardType type, double chance, Lootbox lootbox) {
            this.type = type;
            this.chance = chance;
            this.lootbox = lootbox;
        }

        public RewardBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public RewardBuilder setItem(ItemStack stack) {
            this.stack = stack;
            return this;
        }

        public RewardBuilder setCommand(String command) {
            this.command = command;
            return this;
        }

        public RewardBuilder setType(RewardType type) {
            this.type = type;
            return this;
        }

        public RewardBuilder setChance(double chance) {
            this.chance = chance;
            return this;
        }

        public RewardBuilder setRewardCategory(RewardCategory category) {
            this.rewardCategory = category;
            return this;
        }

        public boolean isComplete() {
            if(this.rewardCategory == null) {
                return false;
            }
            if(this.type == RewardType.COMMAND && this.command != null) {
                return true;
            }
            if(this.type == RewardType.ITEM && this.stack != null) {
                return true;
            }
            return false;
        }

        public LBReward build() {
            LBReward reward = null;
            if(this.type == RewardType.COMMAND) {
                reward = new LBReward(this.type, this.command, this.title, this.chance, this.lootbox);
            }
            if(this.type == RewardType.ITEM) {
                reward = new LBReward(this.type, this.stack, this.title, this.chance, this.lootbox);
            }
            this.lootbox.inputReward(reward, this.rewardCategory);
            return reward;
        }
    }
}
