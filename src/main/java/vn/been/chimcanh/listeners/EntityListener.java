package vn.been.chimcanh.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import vn.been.chimcanh.ChimCanhVIP;
import vn.been.chimcanh.data.ParrotData;
import vn.been.chimcanh.data.PlayerDataManager;

public class EntityListener implements Listener {
    private final ChimCanhVIP plugin;
    private final PlayerDataManager dataManager;

    public EntityListener(ChimCanhVIP plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getPlayerDataManager();
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (!(event.getEntity() instanceof Parrot parrot)) return;
        if (!(event.getOwner() instanceof Player owner)) return;
        ParrotData parrotData = dataManager.createParrotData(owner.getUniqueId(), parrot.getUniqueId());
        parrotData.updateNameTag(parrot);
        owner.sendMessage(plugin.getConfigManager().getMessage("parrot_tamed"));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Parrot parrot)) return;
        ParrotData parrotData = dataManager.getParrotData(parrot.getUniqueId());
        if (parrotData == null) return;
        if (event.getDamager() instanceof Player damager) {
            if (!damager.getUniqueId().equals(parrotData.getOwnerId())) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.RED + "Bạn không thể tấn công vẹt của người khác!");
            }
        }
    }
}