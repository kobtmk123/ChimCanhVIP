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

    // Bộ nhớ đệm (Cache) để lưu dữ liệu của người chơi đang online
    // Cấu trúc: Map<UUID của chủ, Map<UUID của vẹt, Dữ liệu của vẹt>>
    private final Map<UUID, Map<UUID, ParrotData>> parrotCache = new HashMap<>();

    public PlayerDataManager(ChimCanhVIP plugin) {
        this.plugin = plugin;
        // Tạo thư mục /plugins/ChimCanhVIP/playerdata/ để lưu file
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    /**
     * Tải dữ liệu của một người chơi từ file vào bộ nhớ cache.
     * Được gọi khi người chơi tham gia server.
     */
    public void loadPlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        Map<UUID, ParrotData> playerParrots = new HashMap<>();

        if (playerFile.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
            ConfigurationSection parrotsSection = data.getConfigurationSection("parrots");
            if (parrotsSection != null) {
                // Duyệt qua tất cả các con vẹt được lưu trong file
                for (String parrotUUIDStr : parrotsSection.getKeys(false)) {
                    UUID parrotUUID = UUID.fromString(parrotUUIDStr);
                    ConfigurationSection singleParrotSection = parrotsSection.getConfigurationSection(parrotUUIDStr);
                    if (singleParrotSection != null) {
                        // Tải dữ liệu và tạo đối tượng ParrotData
                        playerParrots.put(parrotUUID, ParrotData.loadFromConfig(parrotUUID, singleParrotSection));
                    }
                }
            }
        }
        // Đưa dữ liệu của người chơi vào cache
        parrotCache.put(playerUUID, playerParrots);
    }

    /**
     * Lưu dữ liệu của một người chơi từ cache vào file.
     * Được gọi khi người chơi rời server hoặc khi server tắt.
     */
    public void savePlayerData(UUID playerUUID) {
        if (!parrotCache.containsKey(playerUUID)) {
            // Không có dữ liệu trong cache để lưu
            return;
        }

        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");
        FileConfiguration data = new YamlConfiguration();
        Map<UUID, ParrotData> playerParrots = parrotCache.get(playerUUID);

        for (Map.Entry<UUID, ParrotData> entry : playerParrots.entrySet()) {
            // Tạo một mục riêng cho mỗi con vẹt trong file yml
            // Ví dụ: parrots.UUID_CUA_VET.dữ_liệu
            ConfigurationSection parrotSection = data.createSection("parrots." + entry.getKey().toString());
            // Yêu cầu đối tượng ParrotData tự lưu dữ liệu của nó
            entry.getValue().saveToConfig(parrotSection);
        }

        try {
            data.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Không thể lưu dữ liệu cho người chơi: " + playerUUID);
            e.printStackTrace();
        }
    }

    /**
     * Phương thức tiện ích để lưu và dọn dẹp cache khi người chơi thoát.
     */
    public void saveAndUnloadPlayerData(UUID playerUUID) {
        savePlayerData(playerUUID);
        parrotCache.remove(playerUUID);
    }

    /**
     * Lưu dữ liệu của tất cả người chơi đang online.
     * Được gọi khi server tắt để đảm bảo an toàn.
     */
    public void saveAllData() {
        // Duyệt qua tất cả người chơi trong cache và lưu dữ liệu của họ
        for (UUID uuid : parrotCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    /**
     * Lấy dữ liệu của một con vẹt cụ thể dựa vào UUID của nó.
     * Nó sẽ quét qua cache để tìm ra chủ nhân của con vẹt này.
     */
    public ParrotData getParrotData(UUID parrotUUID) {
        for (Map<UUID, ParrotData> playerParrots : parrotCache.values()) {
            if (playerParrots.containsKey(parrotUUID)) {
                return playerParrots.get(parrotUUID);
            }
        }
        return null; // Không tìm thấy vẹt trong cache
    }

    /**
     * Tạo dữ liệu cho một con vẹt mới khi nó được thuần hóa.
     */
    public ParrotData createParrotData(UUID ownerUUID, UUID parrotUUID) {
        // Đảm bảo người chủ đã có trong cache, nếu chưa thì tạo một mục trống
        parrotCache.computeIfAbsent(ownerUUID, k -> new HashMap<>());

        // Tạo đối tượng ParrotData mới
        ParrotData newParrotData = new ParrotData(parrotUUID, ownerUUID);
        // Thêm vẹt mới vào danh sách của người chủ
        parrotCache.get(ownerUUID).put(parrotUUID, newParrotData);
        
        // Lưu ngay lập tức để đảm bảo dữ liệu không bị mất nếu server crash
        savePlayerData(ownerUUID);
        
        return newParrotData;
    }

    /**
     * Một phương thức tiện ích để lưu lại dữ liệu sau khi thông tin của một con vẹt thay đổi.
     */
    public void saveParrotData(ParrotData parrotData) {
        if (parrotData == null) return;
        // Lưu lại toàn bộ file của người chủ
        savePlayerData(parrotData.getOwnerId());
    }
}