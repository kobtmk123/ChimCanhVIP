package vn.been.chimcanh.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import vn.been.chimcanh.ChimCanhVIP;
import vn.been.chimcanh.data.ParrotData;
import vn.been.chimcanh.manager.ItemManager;
// --- DÒNG NÀY ĐÃ ĐƯỢC SỬA ---
import vn.been.chimcanh.data.PlayerDataManager;
// ----------------------------
import vn.been.chimcanh.model.ParrotLevel;

public class PlayerListener implements Listener {
    private final ChimCanhVIP plugin;
    private final PlayerDataManager dataManager;
    private final ItemManager itemManager;

    public PlayerListener(ChimCanhVIP plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getPlayerDataManager();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dataManager.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        dataManager.saveAndUnloadPlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onFeedParrot(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Parrot)) return;

        Player player = event.getPlayer();
        Parrot parrot = (Parrot) event.getRightClicked();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemManager.isCustomSeed(itemInHand)) return;
        event.setCancelled(true);

        ParrotData parrotData = dataManager.getParrotData(parrot.getUniqueId());
        if (parrotData == null || !player.getUniqueId().equals(parrotData.getOwnerId())) {
            return;
        }

        boolean leveledUp = parrotData.feed(1);
        parrotData.updateNameTag(parrot);
        dataManager.saveParrotData(parrotData);

        itemInHand.setAmount(itemInHand.getAmount() - 1);

        if (leveledUp) {
            player.sendMessage(ChatColor.GREEN + "Vẹt của bạn đã lên cấp thành " + parrotData.getLevel().getDisplayName());
            if (parrotData.getLevel() == ParrotLevel.CHIEN_BINH) {
                 plugin.getServer().broadcastMessage(plugin.getConfigManager().getMessage("warrior_achieved", "{player}", player.getName()));
            }
        }
    }
}