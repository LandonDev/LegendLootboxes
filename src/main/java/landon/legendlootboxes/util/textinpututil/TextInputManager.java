package landon.legendlootboxes.util.textinpututil;

import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.struct.LootboxManager;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TextInputManager {
    private List<CompletePrompt> activePrompts = new ArrayList<>();
    private static volatile TextInputManager inst;

    public static TextInputManager get() {
        if(inst == null) {
            synchronized (TextInputManager.class) {
                inst = new TextInputManager();
            }
        }
        return inst;
    }

    private TextInputManager() {

    }

    public boolean isAcceptableResponse(String response, ResponseType type) {
        if(type == ResponseType.NUMBER) {
            return NumberUtils.isNumber(response);
        }
        if(type == ResponseType.STRING) {
            return true;
        }
        if(type == ResponseType.PERCENTAGE) {
            return NumberUtils.isNumber(response) && NumberUtils.toDouble(response) <= 100.0D && NumberUtils.toDouble(response) >= 0.0D;
        }
        if(type == ResponseType.LOOTBOX) {
            return (LootboxManager.get().findLootbox(response, false, true) != null ? LootboxManager.get().findLootbox(response, false, true) != null : LootboxManager.get().findLootbox(response, true, true) != null);
        }
        return false;
    }

    public Object parseResponse(String response, ResponseType type) {
        if(type == ResponseType.NUMBER) {
            return NumberUtils.toInt(response);
        }
        if(type == ResponseType.STRING) {
            return response;
        }
        if(type == ResponseType.PERCENTAGE) {
            return NumberUtils.toDouble(response);
        }
        if(type == ResponseType.LOOTBOX) {
            return (LootboxManager.get().findLootbox(response, false, true) != null ? LootboxManager.get().findLootbox(response, false, true) : LootboxManager.get().findLootbox(response, true, true));
        }
        return null;
    }

    public boolean hasActivePrompt(Player player) {
        for (CompletePrompt prompt : this.activePrompts) {
            if(prompt.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public CompletePrompt getActivePrompt(Player player) {
        for (CompletePrompt prompt : this.activePrompts) {
            if(prompt.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) {
                return prompt;
            }
        }
        return null;
    }
}
