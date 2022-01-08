package landon.legendlootboxes.util.customcommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCommand {
    private String subCommand;
    private String description;
    private String excessArgs;
    private boolean needsPerm;
    private String permissionNode;

    public SubCommand(String subCommand, String description, boolean needsPerm, String permissionNode, String excessArgs) {
        this.subCommand = subCommand;
        this.needsPerm = needsPerm;
        this.description = description;
        this.permissionNode = permissionNode;
        this.excessArgs = excessArgs;
    }
}
