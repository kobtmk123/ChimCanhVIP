package vn.been.chimcanh.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import vn.been.chimcanh.ChimCanhVIP;
import vn.been.chimcanh.data.ParrotData;
import vn.been.chimcanh.manager.PlayerDataManager;

public class EntityListener implements Listener {

    private final ChimCanhVIP plugin;
    private final PlayerDataManager dataManager;

    public EntityListener(ChimCanhVIP plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getPlayerDataManager();
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (!(event.getEntity() instanceof Parrot)) return;
        if (!(event.getOwner() instanceof Player)) return;

        Parrot parrot = (Parrot) event.getEntity();
        Player owner = (Player) event.getOwner();

        // Tạo dữ liệu mới cho vẹt
        ParrotData parrotData = dataManager.createParrotData(owner.getUniqueId(), parrot.getUniqueId());
        
        // Cập nhật tên hiển thị
        parrotData.updateNameTag(parrot);
        
        owner.sendMessage(plugin.getConfigManager().getMessage("parrot_tamed"));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Parrot)) return;

        Parrot parrot = (Parrot) event.getEntity();
        ParrotData parrotData = dataManager.getParrotData(parrot.getUniqueId());

        // Nếu vẹt không có trong dữ liệu của plugin, bỏ qua
        if (parrotData == null) return;
        
        // Nếu người gây sát thương là một người chơi
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            // Nếu người gây sát thương không phải chủ
            if (!damager.getUniqueId().equals(parrotData.getOwnerId())) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.RED + "Bạn không thể tấn công vẹt của người khác!");
            }
        } else {
            // Nếu không phải người chơi (ví dụ: mob) tấn công vẹt của người khác
             if (parrot.getOwner() != null && !parrot.getOwner().getUniqueId().equals(event.getDamager().getUniqueId())) {
                // Có thể thêm logic bảo vệ ở đây nếu muốn
             }
        }
    }
}