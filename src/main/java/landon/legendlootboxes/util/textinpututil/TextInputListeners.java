package landon.legendlootboxes.util.textinpututil;

import com.cryptomorin.xseries.XSound;
import landon.legendlootboxes.util.c;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TextInputListeners implements Listener {
    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        TextInputManager manager = TextInputManager.get();
        Player player = e.getPlayer();
        if(manager.hasActivePrompt(player)) {
            e.setCancelled(true);
            CompletePrompt prompt = manager.getActivePrompt(player);
            String response = e.getMessage();
            if(response.equalsIgnoreCase("cancel")) {
                prompt.cancel();
                return;
            }
            if(manager.isAcceptableResponse(response, prompt.getCurrentInput().getAccepts())) {
                prompt.getCurrentInput().setResponse(manager.parseResponse(response, prompt.getCurrentInput().getAccepts()));
                prompt.getCurrentInput().setRawResponse(response);
                player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1.0F, 1.0F);
                prompt.next();
            } else {
                player.playSound(player.getLocation(), XSound.BLOCK_GLASS_BREAK.parseSound(), 1.0F, 1.0F);
                player.sendMessage(c.c("&cThat is not an acceptable response to the prompt. The prompt:"));
                prompt.getCurrentInput().run();
                player.sendMessage(c.c("&caccepts: &7" + StringUtils.capitalize(prompt.getCurrentInput().getAccepts().toString().replace("_", " "))));
                player.sendMessage(c.c("&cPlease try again! Type '&7cancel&c' to cancel the text input."));
            }
        }
    }
}
