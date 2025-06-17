package vn.been.chimcanh.manager;

import org.bukkit.entity.Player;
import vn.been.chimcanh.ChimCanhVIP;

public class ChallengeManager {
    private final ChimCanhVIP plugin;

    public ChallengeManager(ChimCanhVIP plugin) { this.plugin = plugin; }
    public void createChallenge(Player challenger, Player target) {}
    public void acceptChallenge(Player target) {}
    public void denyChallenge(Player target) {}
}