package landon.legendlootboxes.util.customcommand;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {
    private static volatile CommandManager inst;

    public static CommandManager get() {
        if(inst == null) {
            synchronized (CommandManager.class) {
                inst = new CommandManager();
            }
        }
        return inst;
    }

    private List<CustomCommand> registeredCustomCommands;
    private List<StructuredCommand> registeredStructuredCommands;
    private CommandManager() {
        this.registeredCustomCommands = new ArrayList<>();
        this.registeredStructuredCommands = new ArrayList<>();
    }

    public void registerCommand(Plugin plugin, CustomCommand customCommand) throws NoSuchFieldException, IllegalAccessException {
        this.registeredCustomCommands.add(customCommand);
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        commandMap.register(plugin.getName(), customCommand.getBukkitCommand());
    }

    public void registerCommand(Plugin plugin, StructuredCommand command) throws NoSuchFieldException, IllegalAccessException {
        this.registeredStructuredCommands.add(command);
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        commandMap.register(plugin.getName(), command.getBukkitCommand());
    }

    public void registerCommands(Plugin plugin, CustomCommand... customCommands) throws NoSuchFieldException, IllegalAccessException {
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        Lists.newArrayList(customCommands).forEach(customCommand -> {
            this.registeredCustomCommands.add(customCommand);
            commandMap.register(plugin.getName(), customCommand.getBukkitCommand());
        });
    }

    public void registerCommands(Plugin plugin, StructuredCommand... commands) throws NoSuchFieldException, IllegalAccessException {
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        Lists.newArrayList(commands).forEach(command -> {
            this.registeredStructuredCommands.add(command);
            commandMap.register(plugin.getName(), command.getBukkitCommand());
        });
    }

    public SubCommand getSubCommand(StructuredCommand command, String arg) {
        for(SubCommand subCommand : command.getSubCommands()) {
            if(subCommand.getSubCommand().equalsIgnoreCase(arg)) {
                return subCommand;
            }
        }
        return null;
    }
}
