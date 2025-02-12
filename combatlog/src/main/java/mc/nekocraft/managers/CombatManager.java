package mc.nekocraft.managers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CombatManager implements Listener {
    private JavaPlugin plugin;
    private ItemManager itemManager;
    private InventoryCache inventoryCache;
    private Map<UUID, Combat> activeCombats;
    private long combatCooldown;
    private FileConfiguration messages;

    public CombatManager(JavaPlugin plugin, ItemManager itemManager, InventoryCache inventoryCache, long combatCooldown, FileConfiguration messages) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.inventoryCache = inventoryCache;
        this.activeCombats = new HashMap<>();
        this.combatCooldown = combatCooldown;
        this.messages = messages;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (!isInCombat(victim)) {
                startCombat(damager, victim);
            } else {
                addAssistant(victim, damager, event.getDamage());
            }
        }
    }

    public void startCombat(Player damager, Player victim) {
        Combat combat = new Combat(damager, victim);
        activeCombats.put(victim.getUniqueId(), combat);
        itemManager.savePlayerInventory(damager);
        itemManager.savePlayerInventory(victim);
        updateCombat(victim);
        sendActionBarMessage(damager, messages.getString("combat-start").replace("{opponent}", victim.getName()));
        sendActionBarMessage(victim, messages.getString("combat-start").replace("{opponent}", damager.getName()));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - combat.getLastDamageTime() >= combatCooldown) {
                    endCombat(victim);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void updateCombat(Player player) {
        Combat combat = activeCombats.get(player.getUniqueId());
        if (combat != null) {
            combat.updateLastDamageTime();
        }
    }

    public void endCombat(Player player) {
        Combat combat = activeCombats.remove(player.getUniqueId());
        if (combat != null) {
            cacheAndClearInventory(combat.getDamager());
            cacheAndClearInventory(combat.getVictim());
            sendActionBarMessage(combat.getDamager(), messages.getString("combat-end"));
            sendActionBarMessage(combat.getVictim(), messages.getString("combat-end"));
        }
    }

    private void cacheAndClearInventory(Player player) {
        ItemStack[] inventory = itemManager.getPlayerInventory(player);
        if (inventory != null) {
            inventoryCache.cacheInventory(player, inventory);
            itemManager.clearPlayerInventory(player);
        }
    }

    public boolean isInCombat(Player player) {
        return activeCombats.containsKey(player.getUniqueId());
    }

    public void setCombatCooldown(long combatCooldown) {
        this.combatCooldown = combatCooldown;
    }

    public Set<Player> getActiveCombatPlayers() {
        Set<Player> players = new HashSet<>();
        for (Combat combat : activeCombats.values()) {
            players.add(combat.getDamager());
            players.add(combat.getVictim());
            players.addAll(combat.getAssistants().keySet());
        }
        return players;
    }

    public Set<Combat> getActiveCombats() {
        return new HashSet<>(activeCombats.values());
    }

    public Combat getCombatByPlayer(Player player) {
        for (Combat combat : activeCombats.values()) {
            if (combat.getDamager().equals(player) || combat.getVictim().equals(player)) {
                return combat;
            }
        }
        return null;
    }

    private void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    private void addAssistant(Player victim, Player assistant, double damage) {
        Combat combat = activeCombats.get(victim.getUniqueId());
        if (combat != null) {
            combat.addAssistant(assistant, damage);
            sendActionBarMessage(assistant, messages.getString("combat-start").replace("{opponent}", victim.getName()));
        }
    }
}