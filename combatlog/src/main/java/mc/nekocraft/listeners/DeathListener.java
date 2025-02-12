package mc.nekocraft.listeners;

import mc.nekocraft.managers.Combat;
import mc.nekocraft.managers.CombatManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private FileConfiguration messages;
    private CombatManager combatManager;

    public DeathListener(FileConfiguration messages, CombatManager combatManager) {
        this.messages = messages;
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        EntityDamageEvent lastDamage = victim.getLastDamageCause();

        if (lastDamage != null) {
            String deathMessage = messages.getString("death-message", "&c{victim} was killed by {killer} using {cause}");
            deathMessage = deathMessage.replace("{victim}", victim.getName());

            if (lastDamage instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) lastDamage;
                if (entityEvent.getDamager() instanceof Player) {
                    Player killer = (Player) entityEvent.getDamager();
                    deathMessage = deathMessage.replace("{killer}", killer.getName());
                    deathMessage = deathMessage.replace("{cause}", translateCause(lastDamage.getCause().toString()));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));

                    Combat combat = combatManager.getCombatByPlayer(victim);
                    if (combat != null) {
                        combat.setKiller(killer);
                    }
                }
            } else {
                String cause = translateCause(lastDamage.getCause().toString());
                deathMessage = deathMessage.replace("{killer}", "an unknown entity");
                deathMessage = deathMessage.replace("{cause}", cause);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));
            }
        }
    }

    private String translateCause(String cause) {
        return messages.getString("causes." + cause.toLowerCase().replace("_", "-"), cause.toLowerCase().replace("_", " "));
    }
}
