package landon.legendlootboxes.struct.reward;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@Setter
public class RewardCategory {
    private String internal;
    private String display;
    private boolean guaranteed;
    private String itemFormat;
    private String commandFormat;
    private ConfigurationSection confSection;
    private boolean showInLore;

    public RewardCategory(String internal, String display, boolean guaranteed, String itemFormat, String commandFormat, ConfigurationSection section, boolean showInLore) {
        this.internal = internal;
        this.display = display;
        this.guaranteed = guaranteed;
        this.itemFormat = itemFormat;
        this.commandFormat = commandFormat;
        this.confSection = section;
        this.showInLore = showInLore;
    }
}
