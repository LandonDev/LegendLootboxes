package landon.legendlootboxes.menu.submenus;

public class LBRewardModifyMenu {}/*implements InventoryProvider {
    public static SmartInventory get(LBReward reward) {
        return SmartInventory.builder()
                .provider(new LBRewardModifyMenu())
                .size(1, 9)
                .id("lbrewardedit-" + reward.getUniqueId().toString())
                .manager(LegendLootboxes.get().getInventoryManager())
                .title("Edit Lootbox Reward")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        LBReward reward = LootboxManager.get().findLootboxReward(UUID.fromString(contents.inventory().getId().split("lbrewardedit-")[1]));
        Lootbox lootbox = reward.getLootbox();
        contents.fill(ClickableItem.empty(ItemBuilder.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "&0")));
        contents.set(0, 0, ClickableItem.empty(ItemBuilder.modifyItem(lootbox.getPhysicalItem(), lootbox.getDisplayName(), LoreUtil.getAndModifyLore(lootbox, true, "", "&eThe rewards you add will appear", "&ehere under their specified category."))));
        contents.set(0, 2, ClickableItem.of(ItemBuilder.createItem(XMaterial.NETHER_STAR.parseItem(), "&e&lChance", "&7Current Chance: &e" + reward.getChance() + "%"), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("What is the new chance you'd like for this reward?", ResponseType.PERCENTAGE)}, prompt -> {
                if(!prompt.isCancelled()) {
                    double chance = (double) prompt.getTextInputs().get(0).getResponse();
                    reward.setChance(chance);
                    LBRewardModifyMenu.get(reward).open(prompt.getPlayer());
                }
            });
        }));
        contents.set(0, 3, ClickableItem.of(ItemBuilder.createItem(reward.getType() == RewardType.ITEM ? XMaterial.GRASS_BLOCK.parseItem() : XMaterial.COMMAND_BLOCK.parseItem(), "&e&lType:&f " + reward.getType().toString(), "&7Click to change this reward."), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            if (reward.getType() == RewardType.COMMAND) {
                CompletePrompt.create(player, new TextInput[]{new TextInput("Hold the item you wish the reward to be in your hand, once you are holding it, type 'yes'.", ResponseType.STRING)}, prompt -> {
                    if(!prompt.isCancelled()) {
                        ItemStack stack = prompt.getPlayer().getItemInHand();
                        if(stack == null || stack.getType() != XMaterial.AIR.parseItem()) {
                            prompt.getPlayer().sendMessage(c.c("&cThere was no item in your hand!"));
                            LBRewardModifyMenu.get(reward).open(player);
                            return;
                        }
                        reward.setCommand(null);
                        reward.setItemStack(stack.clone());
                        reward.setType(RewardType.ITEM);
                        LBRewardModifyMenu.get(reward).open(player);
                    }
                });
            } else {
                CompletePrompt.create(player, new TextInput[]{new TextInput("Type the command you wish the reward to be (do not include a slash and use %player% as a player variable).", ResponseType.STRING)}, prompt -> {
                    if(!prompt.isCancelled()) {
                        String command = (String) prompt.getTextInputs().get(0).getResponse();
                        reward.setCommand(command);
                        reward.setItemStack(null);
                        reward.setType(RewardType.COMMAND);
                        LBRewardModifyMenu.get(reward).open(player);
                    }
                });
            }
        }));
        contents.set(0, 4, ClickableItem.of(ItemBuilder.createItem(XMaterial.NAME_TAG.parseItem(), "&e&lViewable Title", reward.getTitle() == null ? "&7Current Title: &eNone" : "&7Current Title: &e" + reward.getTitle(), "'&7Click to change or add a title."), e -> {
            e.setCancelled(true);
            contents.inventory().close(player);
            CompletePrompt.create(player, new TextInput[]{new TextInput("Type the new title you wish to be publicly displayed for this item. Type 'none' to remove the current title.", ResponseType.STRING)}, prompt -> {
                if(!prompt.isCancelled()) {
                    String title = (String) prompt.getTextInputs().get(0).getResponse();
                    if(title.equalsIgnoreCase("none")) {
                        reward.setTitle(null);
                    } else {
                        reward.setTitle(title);
                    }
                    LBRewardModifyMenu.get(reward).open(player);
                }
            });
        }));
        contents.set(0, 7, ClickableItem.of(ItemBuilder.createItem(XMaterial.BARRIER.parseItem(), "&c&lDelete Reward", "&7Click to remove this reward."), e -> {
            e.setCancelled(true);
            player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 1.0F, 1.0F);
            contents.set(0, 7, ClickableItem.of(ItemBuilder.createItem(XMaterial.RED_STAINED_GLASS_PANE.parseItem(), "&c&lConfirm Deletion", "&7Click again to confirm the deletion", "&7of this lootbox reward."), e2 -> {
                e2.setCancelled(true);
                player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 1.0F, 1.0F);
                if(!lootbox.deleteReward(reward)) {
                    player.sendMessage(c.c("&cThere was an error deleting the reward (could not be found in rewards map, contact the developer for more details)."));
                }
                LootboxRewardMenu.get(lootbox).open(player);
            }));
        }));
        contents.set(0, 8, ClickableItem.of(ItemBuilder.createItem(XMaterial.RED_STAINED_GLASS.parseItem(), "&cBack to previous menu"), e -> {
            e.setCancelled(true);
            LootboxRewardMenu.get(lootbox).open(player);
            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}*/
