package landon.legendlootboxes.util.customcommand;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public abstract class CustomCommand {
    private String command;
    private boolean needsOp;
    private boolean needsPerm;
    private String permNode;
    private Command bukkitCommand;

    public CustomCommand(String command, boolean needsOp, boolean needsPerm, String permNode) {
        this.command = command;
        this.needsOp = needsOp;
        this.needsPerm = needsPerm;
        this.permNode = permNode;
        this.bukkitCommand = new Command(this.command) {
            @Override
            public boolean execute(CommandSender commandSender, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (CustomCommand.this.isNeedsOp()) {
                        if (player.isOp()) {
                            CustomCommand.this.execute(player, Lists.newArrayList(strings));
                        } else {
                            CustomCommand.this.onFail(player, Lists.newArrayList(strings));
                        }
                    } else if (CustomCommand.this.isNeedsPerm()) {
                        if (player.hasPermission(CustomCommand.this.getPermNode())) {
                            CustomCommand.this.execute(player, Lists.newArrayList(strings));
                        } else {
                            CustomCommand.this.onFail(player, Lists.newArrayList(strings));
                        }
                    } else {
                        CustomCommand.this.execute(player, Lists.newArrayList(strings));
                    }
                }
                return false;
            }
        };
    }

    public abstract void execute(Player player, List<String> args);
    public abstract void onFail(Player player, List<String> args);

    public String getFailMessage() {
        return ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou do not have permission to execute &c&n/" + this.command + "&c!");
    }
}
