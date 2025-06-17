package vn.been.chimcanh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.been.chimcanh.ChimCanhVIP;
import vn.been.chimcanh.manager.ConfigManager;

public class MainCommand implements CommandExecutor {
    private final ChimCanhVIP plugin;
    private final ConfigManager configManager;

    public MainCommand(ChimCanhVIP plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("chimcanh.admin.reload")) {
                sender.sendMessage(configManager.getMessage("no_permission"));
                return true;
            }
            plugin.getConfigManager().loadConfig();
            plugin.getItemManager().loadItems();
            sender.sendMessage(configManager.getMessage("reload"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Lệnh này chỉ có thể dùng bởi người chơi.");
            return true;
        }

        Player player = (Player) sender;

        switch (subCommand) {
            case "list":
                sendHelp(player);
                break;
            case "thidau":
                player.sendMessage(ChatColor.YELLOW + "Tính năng thách đấu đang được phát triển!");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Lệnh không hợp lệ. Dùng /chimcanh help để xem các lệnh.");
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e§l--- Hướng dẫn ChimCanhVIP ---");
        sender.sendMessage("§a/chim list (hoặc help): §fXem bảng trợ giúp này.");
        sender.sendMessage("§a/chim thidau <tên>: §fThách đấu chim với người khác.");
        sender.sendMessage("§a/chim okchim: §fChấp nhận lời thách đấu.");
        sender.sendMessage("§a/chim tuchoi: §fTừ chối lời thách đấu.");
        sender.sendMessage("§a/chim editchim <mô tả>: §fChỉnh sửa mô tả cho chim.");
        sender.sendMessage("§a/chim soichim [tên]: §fXem thông tin chim của bạn hoặc người khác.");
        sender.sendMessage("§a/chim tangchim <tên>: §fTặng chim của bạn cho người khác.");
        if (sender.hasPermission("chimcanh.admin")) {
            sender.sendMessage("§c/chim reload: §7Tải lại cấu hình plugin.");
            sender.sendMessage("§c/chim item get <id>: §7Lấy vật phẩm hạt giống.");
        }
    }
}