package landon.legendlootboxes.util.textinpututil;

import com.google.common.collect.Lists;
import landon.legendlootboxes.LegendLootboxes;
import landon.legendlootboxes.menu.LootboxViewMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
public class CompletePrompt {
    private List<TextInput> textInputs;
    private Player player;
    private TextInput currentInput;
    private boolean imposed;
    private boolean cancelled;
    private boolean runDefaultCancelMethod;
    private int current;
    private List<Object> associatedObjects;
    private Consumer<CompletePrompt> consumer;

    private CompletePrompt(Player player, List<TextInput> textInputs, List<Object> associatedObjects, Consumer<CompletePrompt> consumer) {
        this.player = player;
        this.textInputs = textInputs;
        this.imposed = false;
        this.associatedObjects = associatedObjects;
        this.consumer = consumer;
        this.cancelled = false;
        this.runDefaultCancelMethod = true;
    }

    private CompletePrompt impose() {
        this.imposed = true;
        this.currentInput = this.textInputs.get(0);
        this.current = 1;
        this.currentInput.run();
        return this;
    }

    protected void next() {
        try {
            this.currentInput = this.textInputs.get(this.current);
            this.currentInput.run();
            this.current++;
        } catch (IndexOutOfBoundsException e) {
            Bukkit.getScheduler().runTask(LegendLootboxes.get(), this::done);
        }
    }

    protected void cancel() {
        this.cancelled = true;
        this.consumer.accept(this);
        Bukkit.getScheduler().runTaskLater(LegendLootboxes.get(), () -> {
            if(CompletePrompt.this.runDefaultCancelMethod) {
                LootboxViewMenu.INVENTORY.open(this.player);
            }
        }, 1L);
        TextInputManager.get().getActivePrompts().remove(this);
    }

    private void done() {
        this.consumer.accept(this);
        TextInputManager.get().getActivePrompts().remove(this);
    }

    public static CompletePrompt create(Player player, TextInput[] inputs, Consumer<CompletePrompt> consumer) {
        CompletePrompt prompt = new CompletePrompt(player, Lists.newArrayList(inputs), Lists.newArrayList(), consumer);
        for (TextInput textInput : prompt.getTextInputs()) {
            textInput.setPartOf(prompt);
        }
        TextInputManager.get().getActivePrompts().add(prompt);
        return prompt.impose();
    }
}
