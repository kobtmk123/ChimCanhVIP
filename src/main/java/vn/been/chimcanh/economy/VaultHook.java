package vn.been.chimcanh.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private Economy economy = null;

    public boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean giveMoney(Player player, double amount) {
        if (economy == null) return false;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }
    
    public boolean takeMoney(Player player, double amount) {
        if (economy == null) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
}