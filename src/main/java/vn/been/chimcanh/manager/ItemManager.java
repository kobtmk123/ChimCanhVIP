package vn.been.chimcanh.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import vn.been.chimcanh.ChimCanhVIP;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemManager {
    private final ChimCanhVIP plugin;
    private final Map<String, ItemStack> customItems = new HashMap<>();
    public static final NamespacedKey SEED_ID_KEY = new NamespacedKey(ChimCanhVIP.getInstance(), "chimcanh_seed_id");

    public ItemManager(ChimCanhVIP plugin) {
        this.plugin = plugin;
        loadItems();
    }

    public void loadItems() {
        customItems.clear();
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
        FileConfiguration itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
        ConfigurationSection itemsSection = itemsConfig.getConfigurationSection("");
        if (itemsSection == null) return;
        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection != null) {
                customItems.put(key.toLowerCase(), createItem(key, itemSection));
            }
        }
    }

    private ItemStack createItem(String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", "WHEAT_SEEDS"));
        if (material == null) {
            plugin.getLogger().warning("Vật liệu không hợp lệ '" + section.getString("material") + "' cho item '" + id + "'.");
            return null;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = section.getString("name");
            if (name != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) meta.setLore(lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
            ConfigurationSection enchantSection = section.getConfigurationSection("enchantments");
            if (enchantSection != null) {
                for (String enchantKey : enchantSection.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantKey.toLowerCase()));
                    if (enchantment != null) meta.addEnchant(enchantment, enchantSection.getInt(enchantKey), true);
                }
            }
            List<String> flags = section.getStringList("flags");
            for (String flagStr : flags) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flagStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("ItemFlag không hợp lệ: " + flagStr);
                }
            }
            meta.getPersistentDataContainer().set(SEED_ID_KEY, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getItem(String id) { return customItems.get(id.toLowerCase()); }
    public boolean isCustomSeed(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(SEED_ID_KEY, PersistentDataType.STRING);
    }
}