package mc.nekocraft.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemManager {
    private JavaPlugin plugin;
    private Map<Player, ItemStack[]> playerInventories;

    public ItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerInventories = new HashMap<>();
        // Initialize item management logic here
    }

    public void savePlayerInventory(Player player) {
        playerInventories.put(player, player.getInventory().getContents());
    }

    public ItemStack[] getPlayerInventory(Player player) {
        return playerInventories.get(player);
    }

    public void clearPlayerInventory(Player player) {
        playerInventories.remove(player);
    }

    public Set<Player> getActiveCombatPlayers() {
        return playerInventories.keySet();
    }
}