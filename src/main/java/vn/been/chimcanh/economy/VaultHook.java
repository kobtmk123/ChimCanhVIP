package vn.been.chimcanh.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private Economy economy = null;

    public boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean isEnabled() { return economy != null; }
    public boolean giveMoney(OfflinePlayer player, double amount) {
        if (!isEnabled()) return false;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }
}