package vn.been.chimcanh.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import vn.been.chimcanh.ChimCanhVIP;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final ChimCanhVIP plugin;
    private final File dataFolder;
    private final Map<UUID, Map<UUID, ParrotData>> parrotCache = new HashMap<>();

    public PlayerDataManager(ChimCanhVIP plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void loadPlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        Map<UUID, ParrotData> playerParrots = new HashMap<>();
        if (playerFile.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
            ConfigurationSection parrotsSection = data.getConfigurationSection("parrots");
            if (parrotsSection != null) {
                for (String parrotUUIDStr : parrotsSection.getKeys(false)) {
                    UUID parrotUUID = UUID.fromString(parrotUUIDStr);
                    ConfigurationSection singleParrotSection = parrotsSection.getConfigurationSection(parrotUUIDStr);
                    if (singleParrotSection != null) {
                        playerParrots.put(parrotUUID, ParrotData.loadFromConfig(parrotUUID, singleParrotSection));
                    }
                }
            }
        }
        parrotCache.put(playerUUID, playerParrots);
    }

    public void savePlayerData(UUID playerUUID) {
        if (!parrotCache.containsKey(playerUUID)) return;
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        FileConfiguration data = new YamlConfiguration();
        Map<UUID, ParrotData> playerParrots = parrotCache.get(playerUUID);
        for (Map.Entry<UUID, ParrotData> entry : playerParrots.entrySet()) {
            ConfigurationSection parrotSection = data.createSection("parrots." + entry.getKey().toString());
            entry.getValue().saveToConfig(parrotSection);
        }
        try {
            data.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Không thể lưu dữ liệu cho người chơi: " + playerUUID);
            e.printStackTrace();
        }
    }

    public void saveAndUnloadPlayerData(UUID playerUUID) {
        savePlayerData(playerUUID);
        parrotCache.remove(playerUUID);
    }

    public void saveAllData() {
        for (UUID uuid : parrotCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    public ParrotData getParrotData(UUID parrotUUID) {
        for (Map<UUID, ParrotData> playerParrots : parrotCache.values()) {
            if (playerParrots.containsKey(parrotUUID)) {
                return playerParrots.get(parrotUUID);
            }
        }
        return null;
    }

    public ParrotData createParrotData(UUID ownerUUID, UUID parrotUUID) {
        parrotCache.computeIfAbsent(ownerUUID, k -> new HashMap<>());
        ParrotData newParrotData = new ParrotData(parrotUUID, ownerUUID);
        parrotCache.get(ownerUUID).put(parrotUUID, newParrotData);
        savePlayerData(ownerUUID);
        return newParrotData;
    }

    public void saveParrotData(ParrotData parrotData) {
        if (parrotData == null) return;
        savePlayerData(parrotData.getOwnerId());
    }
}