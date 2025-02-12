package mc.nekocraft.listeners;

import mc.nekocraft.managers.CombatManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class CombatListener implements Listener {
    private CombatManager combatManager;

    public CombatListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (combatManager.isInCombat(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use commands while in combat!");
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (combatManager.isInCombat(player)) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.sendMessage(ChatColor.RED + "Fly mode is disabled while in combat!");
        }
    }
}
