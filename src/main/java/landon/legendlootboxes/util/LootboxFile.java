package landon.legendlootboxes.util;

import landon.legendlootboxes.LegendLootboxes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public class LootboxFile {
    private FileConfiguration config;

    private File file;

    private Plugin plugin;

    private String fileName;

    public LootboxFile(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        create();
    }

    public FileConfiguration get() {
        return this.config;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        this.file.delete();
    }

    public void create() {
        this.file = new File(this.plugin.getDataFolder() + "/lootboxes/", getFileName());
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
        }
        this.config = (FileConfiguration) new YamlConfiguration();
        try {
            this.config.load(this.file);
        } catch(FileNotFoundException e) {
            if(LegendLootboxes.get().isDebug()) {
                e.printStackTrace();
            }
            LegendLootboxes.get().getLogger().log(Level.SEVERE, "Could not find required lootbox file, could be due to creation of a new lootbox. If it was caused by a creation of a lootbox, ignore.");
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.config = (FileConfiguration) new YamlConfiguration();
        try {
            this.config.load(this.file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
