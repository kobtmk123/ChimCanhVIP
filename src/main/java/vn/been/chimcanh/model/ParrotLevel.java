package vn.been.chimcanh.model;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public enum ParrotLevel {
    // Các cấp độ mặc định
    NON,
    BOI_THUONG,
    BOI_GIA,
    CHIEN_BINH;

    private static final Map<ParrotLevel, LevelData> levelDataMap = new HashMap<>();

    public static void loadLevelsFromConfig(FileConfiguration config) {
        levelDataMap.clear();
        ConfigurationSection levelsSection = config.getConfigurationSection("levels");
        if (levelsSection == null) return;

        for (String key : levelsSection.getKeys(false)) {
            try {
                ParrotLevel level = ParrotLevel.valueOf(key.toUpperCase());
                String displayName = levelsSection.getString(key + ".display_name");
                String nextLevelStr = levelsSection.getString(key + ".next_level");
                ParrotLevel nextLevel = (nextLevelStr != null) ? ParrotLevel.valueOf(nextLevelStr.toUpperCase()) : null;
                int seedsToUpgrade = levelsSection.getInt(key + ".seeds_to_level_up");
                levelDataMap.put(level, new LevelData(displayName, nextLevel, seedsToUpgrade));
            } catch (IllegalArgumentException e) {
                System.err.println("Cấp độ không hợp lệ trong config.yml: " + key);
            }
        }
    }

    public LevelData getData() {
        return levelDataMap.get(this);
    }
    
    public ParrotLevel getNextLevel() {
        return getData() != null ? getData().nextLevel : null;
    }
    
    public String getDisplayName() {
        return getData() != null ? org.bukkit.ChatColor.translateAlternateColorCodes('&', getData().displayName) : this.name();
    }

    public int getSeedsToUpgrade() {
        return getData() != null ? getData().seedsToUpgrade : Integer.MAX_VALUE;
    }

    private static class LevelData {
        final String displayName;
        final ParrotLevel nextLevel;
        final int seedsToUpgrade;

        LevelData(String displayName, ParrotLevel nextLevel, int seedsToUpgrade) {
            this.displayName = displayName;
            this.nextLevel = nextLevel;
            this.seedsToUpgrade = seedsToUpgrade;
        }
    }
}