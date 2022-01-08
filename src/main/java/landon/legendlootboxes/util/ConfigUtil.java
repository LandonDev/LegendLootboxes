package landon.legendlootboxes.util;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class ConfigUtil {
    public static FileConfiguration storeItem(FileConfiguration config, String path, ItemStack stack) {
        config.set(path + ".material", stack.getType().toString());
        config.set(path + ".amount", stack.getAmount());
        config.set(path + ".nbt", NBTItem.convertItemtoNBT(stack).toString());
        return config;
    }

    public static ItemStack getStoredItem(FileConfiguration config, String path) {
        Material material = XMaterial.matchXMaterial(config.getString(path + ".material")).get().parseMaterial();
        int amount = config.getInt(path + ".amount");
        String nbt = config.getString(path + ".nbt");
        ItemStack stack = NBTItem.convertNBTtoItem(new NBTContainer(nbt));
        stack.setType(material);
        stack.setAmount(amount);
        return stack;
    }
}
