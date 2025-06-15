package vn.been.chimcanh;

import org.bukkit.plugin.java.JavaPlugin;
import vn.been.chimcanh.commands.MainCommand;
import vn.been.chimcanh.data.PlayerDataManager;
import vn.been.chimcanh.economy.VaultHook;
import vn.been.chimcanh.listeners.EntityListener;
import vn.been.chimcanh.listeners.PlayerListener;
import vn.been.chimcanh.manager.ChallengeManager;
import vn.been.chimcanh.manager.ConfigManager;
import vn.been.chimcanh.manager.ItemManager;

public final class ChimCanhVIP extends JavaPlugin {

    private static ChimCanhVIP instance;
    private ConfigManager configManager;
    private ItemManager itemManager;
    private PlayerDataManager playerDataManager;
    private ChallengeManager challengeManager;
    private VaultHook vaultHook;

    @Override
    public void onEnable() {
        instance = this;

        // Load các hệ thống quản lý theo thứ tự quan trọng
        this.configManager = new ConfigManager(this);
        this.itemManager = new ItemManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.challengeManager = new ChallengeManager(this);
        this.vaultHook = new VaultHook();
        if (!vaultHook.setupEconomy()) {
            getLogger().warning("Không tìm thấy Vault hoặc plugin Economy! Các tính năng tiền tệ sẽ bị vô hiệu hóa.");
        }

        // Đăng ký lệnh
        getCommand("chimcanh").setExecutor(new MainCommand(this));

        // Đăng ký sự kiện
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);

        getLogger().info("ChimCanhVIP đã được bật!");
    }

    @Override
    public void onDisable() {
        // Lưu tất cả dữ liệu người chơi khi tắt server
        if (playerDataManager != null) {
            playerDataManager.saveAllData();
        }
        getLogger().info("ChimCanhVIP đã được tắt!");
    }

    public static ChimCanhVIP getInstance() {
        return instance;
    }

    // Getters để các lớp khác có thể truy cập
    public ConfigManager getConfigManager() { return configManager; }
    public ItemManager getItemManager() { return itemManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public ChallengeManager getChallengeManager() { return challengeManager; }
    public VaultHook getVaultHook() { return vaultHook; }
}