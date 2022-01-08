package landon.legendlootboxes.util.customcommand;

import com.google.common.collect.Lists;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.util.ConfMessage;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class StructuredCommand {
    private String mainCommand;
    private String description;
    private SubCommand[] subCommands;
    private boolean needsOp;
    private boolean needsPerm;
    private String permNode;
    private String[] aliases;
    private Command bukkitCommand;
    private LegendLootboxes plugin;

    public StructuredCommand(String mainCommand, String description, boolean needsOp, boolean needsPerm, String permNode, String[] aliases, SubCommand... subCommands) {
        this.mainCommand = mainCommand;
        this.description = description;
        this.subCommands = subCommands;
        this.needsOp = needsOp;
        this.needsPerm = needsPerm;
        this.permNode = permNode;
        this.aliases = aliases;
        this.plugin = LegendLootboxes.get();
        this.bukkitCommand = new Command(this.mainCommand, "", "", Lists.newArrayList(this.aliases)) {
            @Override
            public boolean execute(CommandSender commandSender, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (StructuredCommand.this.isNeedsOp()) {
                        if (player.isOp()) {
                            if (strings.length > 0) {
                                if (CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]) != null) {
                                    if (CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]).isNeedsPerm()) {
                                        if (player.hasPermission(CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]).getPermissionNode())) {
                                            List<String> leftArgs = new ArrayList<>();
                                            for (String arg : strings) {
                                                if (arg != strings[0]) {
                                                    leftArgs.add(arg);
                                                }
                                            }
                                            StructuredCommand.this.execute(player, CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]), leftArgs, plugin);
                                        } else {
                                            StructuredCommand.this.executeNoArgs(player, plugin);
                                        }
                                    } else {
                                        List<String> leftArgs = new ArrayList<>();
                                        for (String arg : strings) {
                                            if (arg != strings[0]) {
                                                leftArgs.add(arg);
                                            }
                                        }
                                        StructuredCommand.this.execute(player, CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]), leftArgs, plugin);
                                    }
                                } else {
                                    StructuredCommand.this.executeNoArgs(player, plugin);
                                }
                            } else {
                                StructuredCommand.this.executeNoArgs(player, plugin);
                            }
                        } else {
                            StructuredCommand.this.fail(player, Lists.newArrayList(strings), plugin);
                        }
                    } else if (StructuredCommand.this.isNeedsPerm()) {
                        if (player.hasPermission(StructuredCommand.this.getPermNode())) {
                            if (strings.length > 0) {
                                if (CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]) != null) {
                                    if (CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]).isNeedsPerm()) {
                                        if (player.hasPermission(CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]).getPermissionNode())) {
                                            List<String> leftArgs = new ArrayList<>();
                                            for (String arg : strings) {
                                                if (arg != strings[0]) {
                                                    leftArgs.add(arg);
                                                }
                                            }
                                            StructuredCommand.this.execute(player, CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]), leftArgs, plugin);
                                        } else {
                                            StructuredCommand.this.executeNoArgs(player, plugin);
                                        }
                                    } else {
                                        List<String> leftArgs = new ArrayList<>();
                                        for (String arg : strings) {
                                            if (arg != strings[0]) {
                                                leftArgs.add(arg);
                                            }
                                        }
                                        StructuredCommand.this.execute(player, CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]), leftArgs, plugin);
                                    }
                                } else {
                                    StructuredCommand.this.executeNoArgs(player, plugin);
                                }
                            } else {
                                StructuredCommand.this.executeNoArgs(player, plugin);
                            }
                        } else {
                            StructuredCommand.this.fail(player, Lists.newArrayList(strings), plugin);
                        }
                    } else {
                        if (strings.length > 0) {
                            if (CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]) != null) {
                                if (CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]).isNeedsPerm()) {
                                    if (player.hasPermission(CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]).getPermissionNode())) {
                                        List<String> leftArgs = new ArrayList<>();
                                        for (String arg : strings) {
                                            if (arg != strings[0]) {
                                                leftArgs.add(arg);
                                            }
                                        }
                                        StructuredCommand.this.execute(player, CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]), leftArgs, plugin);
                                    } else {
                                        StructuredCommand.this.executeNoArgs(player, plugin);
                                    }
                                } else {
                                    List<String> leftArgs = new ArrayList<>();
                                    for (String arg : strings) {
                                        if (arg != strings[0]) {
                                            leftArgs.add(arg);
                                        }
                                    }
                                    StructuredCommand.this.execute(player, CommandManager.get().getSubCommand(StructuredCommand.this, strings[0]), leftArgs, plugin);
                                }
                            } else {
                                StructuredCommand.this.executeNoArgs(player, plugin);
                            }
                        } else {
                            StructuredCommand.this.executeNoArgs(player, plugin);
                        }
                    }
                }
                return false;
            }
        };
    }

    public abstract void executeNoArgs(Player player, Plugin plugin);
    public abstract void execute(Player player, SubCommand subCommand, List<String> args, Plugin plugin);
    public abstract void fail(Player player, List<String> args, Plugin plugin);

    public void sendNoArgMessage(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l&n/" + this.mainCommand));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + this.description));
        for(SubCommand subCommand : this.subCommands) {
            if(subCommand.isNeedsPerm() && player.hasPermission(subCommand.getPermissionNode())) {
                if(subCommand.getDescription() != null) {
                    if(subCommand.getExcessArgs() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand() + " " + subCommand.getExcessArgs() + " &7- " + subCommand.getDescription()));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand() + " &7- " + subCommand.getDescription()));
                    }
                } else {
                    if(subCommand.getExcessArgs() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand() + " " + subCommand.getExcessArgs()));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand()));
                    }
                }
            } else if(!subCommand.isNeedsPerm()) {
                if(subCommand.getDescription() != null) {
                    if(subCommand.getExcessArgs() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand() + " " + subCommand.getExcessArgs() + " &7- " + subCommand.getDescription()));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand() + " &7- " + subCommand.getDescription()));
                    }
                } else {
                    if(subCommand.getExcessArgs() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand() + " " + subCommand.getExcessArgs()));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e/" + this.mainCommand + " " + subCommand.getSubCommand()));
                    }
                }
            }
        }
        player.sendMessage("");
    }

    public String getFailMessage() {
        return ConfMessage.get("commands.no-permission")
                .replace("%command%", this.mainCommand);
    }

    public void sendFailedSubCommand(Player player, SubCommand subCommand, Exception exception) {
        player.sendMessage(ConfMessage.get("commands.incorrect-usage")
            .replace("%command%", this.mainCommand)
                .replace("%subcommand%", subCommand.getSubCommand())
                .replace("%excess-arguments%", subCommand.getExcessArgs())
        );
        if(subCommand.getDescription() != null) {
            player.sendMessage(ConfMessage.get("commands.incorrect-usage-description")
                .replace("%description%", subCommand.getDescription())
            );
        }
        if(exception != null && !(exception instanceof IndexOutOfBoundsException) && LegendLootboxes.get().isDebug()) {
            exception.printStackTrace();
        }
    }
}
