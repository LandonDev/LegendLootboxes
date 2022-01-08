package landon.legendlootboxes;

import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.InventoryManager;
import landon.legendlootboxes.commands.CmdLootbox;
import landon.legendlootboxes.listeners.LootboxListeners;
import landon.legendlootboxes.struct.Lootbox;
import landon.legendlootboxes.struct.LootboxManager;
import landon.legendlootboxes.struct.reward.RewardCategory;
import landon.legendlootboxes.util.ItemBuilder;
import landon.legendlootboxes.util.customcommand.CommandManager;
import landon.legendlootboxes.util.textinpututil.TextInputListeners;
import landon.legendlootboxes.util.textinpututil.TextInputManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@Getter
public final class LegendLootboxes extends JavaPlugin {
    private static LegendLootboxes inst;

    private boolean debug;
    private InventoryManager inventoryManager;
    private ItemStack defaultLootboxItem;

    private List<RewardCategory> loadedRewardCategories = new ArrayList<>();

    @SneakyThrows
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.initializeConfigDefaults();
        if(!new File(this.getDataFolder() + "/lootboxes/").exists()) {
            new File(this.getDataFolder() + "/lootboxes/").mkdirs();
        }
        inst = this;
        this.debug = getConfig().getBoolean("debug");
        this.loadCategories();
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();
        this.fillDefaultLootboxItem();
        TextInputManager.get();
        commands();
        listeners();
        LootboxManager.get().loadLootboxes();
    }

    public void commands() throws NoSuchFieldException, IllegalAccessException {
        CommandManager.get().registerCommand(this, new CmdLootbox());
    }

    public void initializeConfigDefaults() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("config.yml")));
        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            if(entry.getValue().equals("reward-categories")) {
                continue;
            }
            getConfig().addDefault(entry.getKey(), entry.getValue());
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void listeners() {
        getServer().getPluginManager().registerEvents(new LootboxListeners(), this);
        getServer().getPluginManager().registerEvents(new TextInputListeners(), this);
    }

    public void loadCategories() {
        if(this.getConfig().isSet("reward-categories")) {
            ConfigurationSection section = this.getConfig().getConfigurationSection("reward-categories");
            for (String key : this.getConfig().getConfigurationSection("reward-categories").getKeys(false)) {
                this.loadedRewardCategories.add(new RewardCategory(key, section.getString(key + ".display"), section.getBoolean(key + ".guaranteed-loot"), section.getString(key + ".item-lore-without-title"), section.getString(key + ".command-lore"), section, section.getBoolean(key + ".show-in-lore")));
                this.getLogger().log(Level.INFO, "Loaded reward category: " + key);
            }
        }
        if(this.loadedRewardCategories.isEmpty()) {
            this.getLogger().log(Level.SEVERE, "No Lootbox reward categories were found in the config, shutting down plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            this.onDisable();
        }
    }

    public void fillDefaultLootboxItem() {
        Optional<XMaterial> material = XMaterial.matchXMaterial(this.getConfig().getString("items.default-lootbox.material"));
        if(!material.isPresent()) {
            this.getLogger().log(Level.SEVERE, "Default Lootbox Item has an invalid material in the config!");
            return;
        }
        String displayName = this.getConfig().getString("items.default-lootbox.display-name");
        List<String> lore = new ArrayList<>(this.getConfig().getStringList("items.default-lootbox.lore"));
        this.defaultLootboxItem = ItemBuilder.createItem(material.get().parseItem(), displayName, lore);
    }

    @Override
    public void onDisable() {
        for (Lootbox lootbox : LootboxManager.get().getLoadedLootboxes()) {
            LootboxManager.get().saveLootbox(lootbox);
        }
    }

    public static LegendLootboxes get() {
        return inst;
    }
}
