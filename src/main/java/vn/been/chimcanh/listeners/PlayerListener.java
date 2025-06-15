package vn.been.chimcanh.listeners;

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
import vn.been.chimcanh.manager.PlayerDataManager;

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
        // Tải dữ liệu của người chơi vào cache khi họ tham gia
        dataManager.getPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Lưu và xóa dữ liệu khỏi cache khi họ rời đi
        dataManager.saveAndUnloadPlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onFeedParrot(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Parrot)) return;

        Player player = event.getPlayer();
        Parrot parrot = (Parrot) event.getRightClicked();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Kiểm tra xem có phải là hạt giống của plugin không
        if (!itemManager.isCustomSeed(itemInHand)) return;
        
        // Ngăn hành động mặc định (ví dụ: thuần hóa bằng hạt thường)
        event.setCancelled(true); 

        ParrotData parrotData = dataManager.getParrotData(parrot.getUniqueId());
        // Nếu vẹt không thuộc plugin hoặc người chơi không phải chủ, không cho ăn
        if (parrotData == null || !player.getUniqueId().equals(parrotData.getOwnerId())) {
            return;
        }

        // Cho vẹt ăn và kiểm tra lên cấp
        boolean leveledUp = parrotData.feed(1);
        parrotData.updateNameTag(parrot);
        dataManager.saveParrotData(parrotData);

        // Trừ 1 item khỏi tay người chơi
        itemInHand.setAmount(itemInHand.getAmount() - 1);
        
        if (leveledUp) {
            // Gửi thông báo lên cấp, v.v.
            player.sendMessage(ChatColor.GREEN + "Vẹt của bạn đã lên cấp thành " + parrotData.getLevel().getDisplayName());
            if (parrotData.getLevel() == ParrotLevel.CHIEN_BINH) {
                 plugin.getServer().broadcastMessage(plugin.getConfigManager().getMessage("warrior_achieved", "{player}", player.getName()));
            }
        }
    }
}