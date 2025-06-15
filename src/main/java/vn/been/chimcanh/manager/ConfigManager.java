package vn.been.chimcanh.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import vn.been.chimcanh.ChimCanhVIP;
import vn.been.chimcanh.model.ParrotLevel;

public class ConfigManager {

    private final ChimCanhVIP plugin;
    private FileConfiguration config;

    public ConfigManager(ChimCanhVIP plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        ParrotLevel.loadLevelsFromConfig(config); // Tải dữ liệu cấp độ từ config
    }

    public String getMessage(String path, String... replacements) {
        String prefix = config.getString("messages.prefix", "&e&l[ChimCanh] &r");
        String message = config.getString("messages." + path, "&cMessage not found: messages." + path);
        String fullMessage = prefix + message;

        for (int i = 0; i < replacements.length; i += 2) {
            fullMessage = fullMessage.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', fullMessage);
    }

    public int getTamingTimeMinutes(String key) {
        return config.getInt("taming_time." + key, 15);
    }

    public int getBathingCooldownDays() {
        return config.getInt("bathing.cooldown_days", 4);
    }

    public double getChallengeWinReward() {
        return config.getDouble("challenge.win_reward_money", 400000);
    }
}