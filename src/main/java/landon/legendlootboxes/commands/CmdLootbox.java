package landon.legendlootboxes.commands;

import com.cryptomorin.xseries.XSound;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.menu.LootboxViewMenu;
import landon.legendlootboxes.menu.submenus.LBRewardBuildMenu;
import landon.legendlootboxes.menu.submenus.LootboxEditorMenu;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.struct.reward.LBReward;
import landon.legendlootboxes.struct.reward.RewardCategory;
import landon.legendlootboxes.struct.reward.RewardType;
import landon.legendlootboxes.util.ConfMessage;
import landon.legendlootboxes.util.GiveUtil;
import landon.legendlootboxes.util.c;
import landon.legendlootboxes.util.customcommand.StructuredCommand;
import landon.legendlootboxes.util.customcommand.SubCommand;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CmdLootbox extends StructuredCommand {
    public CmdLootbox() {
        super("lootbox", ConfMessage.getNotMessage("commands.lootbox.description"), false, false, null, new String[]{"elootbox", "lb", "elb"},
                new SubCommand("create", "Create a new lootbox.", true, "legendlootboxes.commands.create", "<name>"),
                new SubCommand("edit", "Edit a lootbox / view all lootboxes,", true, "legendlootboxes.commands.edit", "[lootbox]"),
                new SubCommand("give", "Give a lootbox", true, "legendlootboxes.commands.give", "<player> <lootbox> [amount]"),
                new SubCommand("delete", "Delete a lootbox", true, "legendlootboxes.commands.delete", "<lootbox>"),
                new SubCommand("additemreward", "Adds the item you are holding as a reward.", true, "legendlootboxes.commands.additemreward", "<lootbox> <category> <chance>"),
                new SubCommand("open", "Open a specified lootbox for a specified player.", true, "legendlootbox.commands.open", "<player> <lootbox>")
        );
    }

    @Override
    public void executeNoArgs(Player player, Plugin plugin) {
        sendNoArgMessage(player);
    }

    @Override
    public void execute(Player player, SubCommand subCommand, List<String> args, Plugin pluginWrapper) {
        LegendLootboxes plugin = (LegendLootboxes) pluginWrapper;
        String sub = subCommand.getSubCommand();
        if (sub.equalsIgnoreCase("create")) {
            if (args.size() >= 1) {
                String name = args.get(0);
                if (LootboxManager.get().findLootbox(name, false, true) != null) {
                    player.sendMessage(c.c("&cThere is already a lootbox with the name '&7" + name + "&c'"));
                    return;
                }
                Lootbox lootbox = LootboxManager.get().createLootbox(name);
                LootboxEditorMenu.get(lootbox).open(player);
                return;
            }
            sendFailedSubCommand(player, subCommand, null);
        }
        if (sub.equalsIgnoreCase("edit")) {
            if (args.size() >= 1) {
                String name = args.get(0);
                Lootbox lootbox = LootboxManager.get().findLootbox(name, false, true);
                if (lootbox != null) {
                    LootboxEditorMenu.get(lootbox).open(player);
                    return;
                }
                LootboxViewMenu.INVENTORY.open(player);
                player.sendMessage(c.c("&cThere was no lootbox found for '&7" + name + "&c', thus the lootbox view GUI has been opened."));
                return;
            }
            LootboxViewMenu.INVENTORY.open(player);
        }
        if (sub.equalsIgnoreCase("give")) {
            if (args.size() >= 2) {
                if (Bukkit.getPlayer(args.get(0)) == null) {
                    player.sendMessage(c.c("&cThere was no player found for '&7" + args.get(0) + "&c'"));
                    return;
                }
                Player toGive = Bukkit.getPlayer(args.get(0));
                Lootbox lootbox = LootboxManager.get().findLootbox(args.get(1), false, true);
                if (lootbox == null) {
                    player.sendMessage(c.c("&cThere was no lootbox found for '&7" + args.get(1) + "&c'"));
                    return;
                }
                if(lootbox.getRewardCount() < 1) {
                    player.sendMessage(c.c("&cThat lootbox has no rewards and cannot be opened!"));
                    return;
                }
                int amount = 1;
                if (args.size() >= 3 && NumberUtils.isNumber(args.get(2))) {
                    amount = Integer.parseInt(args.get(2));
                }
                for (int i = 0; i < amount; i++) {
                    GiveUtil.giveOrDropItem(toGive, lootbox.buildPhysicalItem());
                }
                return;
            }
            sendFailedSubCommand(player, subCommand, null);
        }
        if (sub.equalsIgnoreCase("delete")) {
            if (args.size() >= 1) {
                String name = args.get(0);
                Lootbox lootbox = LootboxManager.get().findLootbox(name, false, true);
                if (lootbox == null) {
                    player.sendMessage(c.c("&cThere was no lootbox found for '&7" + args.get(1) + "&c'"));
                    return;
                }
                player.sendMessage(c.c("&aYou successfully deleted the '&7" + lootbox.getInternalName() + "&a' lootbox."));
                try {
                    lootbox.delete();
                } catch (Exception e) {
                    if(LegendLootboxes.get().isDebug()) {
                        e.printStackTrace();
                    }
                    player.sendMessage(c.c("&cThere was an error trying to delete the lootbox! Turn on debugging for more info."));
                }
                return;
            }
            sendFailedSubCommand(player, subCommand, null);
        }
        if (sub.equalsIgnoreCase("additemreward")) {
            if (args.size() >= 3) {
                Lootbox lootbox = LootboxManager.get().findLootbox(args.get(0), false, true);
                if (lootbox == null) {
                    player.sendMessage(c.c("&cThere was no lootbox found for '&7" + args.get(0) + "&c'"));
                    return;
                }
                RewardCategory category = LootboxManager.get().findRewardCategory(args.get(1));
                if (category == null) {
                    player.sendMessage(c.c("&cThere was no reward category found for '&7" + args.get(1) + "&c'"));
                    return;
                }
                if (!NumberUtils.isNumber(args.get(2))) {
                    player.sendMessage(c.c("&c'&7" + args.get(2) + "&c' is not a valid number for a chance."));
                    return;
                }
                double chance = Double.parseDouble(args.get(2));
                ItemStack item = player.getItemInHand();
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage(c.c("&cYou are not holding any item in your hand!"));
                    return;
                }
                if(!player.hasPermission("legendlootboxes.commands.edit")) {
                    player.sendMessage(c.c("&cYou do not have permission to open the lootbox editor and thus you cannot create a reward."));
                    return;
                }
                LBReward.RewardBuilder builder = LBReward.builder(lootbox);
                builder.setType(RewardType.ITEM)
                        .setItem(item)
                        .setRewardCategory(category)
                        .setChance(chance);
                LootboxManager.get().getActiveRewardBuilders().put(player, builder);
                LBRewardBuildMenu.get(lootbox).open(player);
                player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                return;
            }
            sendFailedSubCommand(player, subCommand, null);
        }
        if (sub.equalsIgnoreCase("open")) {
            if (args.size() >= 2) {
                if (Bukkit.getPlayer(args.get(0)) == null) {
                    player.sendMessage(c.c("&cThere was no player found for '&7" + args.get(0) + "&c'"));
                    return;
                }
                Player toGive = Bukkit.getPlayer(args.get(0));
                Lootbox lootbox = LootboxManager.get().findLootbox(args.get(1), false, true);
                if (lootbox == null) {
                    player.sendMessage(c.c("&cThere was no lootbox found for '&7" + args.get(1) + "&c'"));
                    return;
                }
                if(lootbox.getRewardCount() < 1) {
                    player.sendMessage(c.c("&cThat lootbox has no rewards and cannot be opened!"));
                    return;
                }
                lootbox.open(toGive);
                return;
            }
            sendFailedSubCommand(player, subCommand, null);
        }
    }

    @Override
    public void fail(Player player, List<String> args, Plugin plugin) {
        player.sendMessage(getFailMessage());
    }
}
