package vn.been.chimcanh.model;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;

public enum ParrotLevel {
    NON, BOI_THUONG, BOI_GIA, CHIEN_BINH;
    private static final Map<ParrotLevel, LevelData> levelDataMap = new HashMap<>();

    public static void loadLevelsFromConfig(FileConfiguration config) {
        levelDataMap.clear();
        ConfigurationSection levelsSection = config.getConfigurationSection("levels");
        if (levelsSection == null) {
            System.err.println("[ChimCanhVIP] Không tìm thấy mục 'levels' trong config.yml!");
            return;
        }
        for (String key : levelsSection.getKeys(false)) {
            try {
                ParrotLevel level = ParrotLevel.valueOf(key.toUpperCase());
                String displayName = levelsSection.getString(key + ".display_name");
                String nextLevelStr = levelsSection.getString(key + ".next_level");
                ParrotLevel nextLevel = (nextLevelStr != null && !nextLevelStr.equalsIgnoreCase("null")) ? ParrotLevel.valueOf(nextLevelStr.toUpperCase()) : null;
                int seedsToUpgrade = levelsSection.getInt(key + ".seeds_to_level_up");
                levelDataMap.put(level, new LevelData(displayName, nextLevel, seedsToUpgrade));
            } catch (IllegalArgumentException e) {
                System.