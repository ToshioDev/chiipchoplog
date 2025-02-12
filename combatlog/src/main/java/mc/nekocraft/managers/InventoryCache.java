package mc.nekocraft.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryCache {
    private Map<Player, ItemStack[]> inventoryCache;

    public InventoryCache() {
        this.inventoryCache = new HashMap<>();
    }

    public void cacheInventory(Player player, ItemStack[] inventory) {
        inventoryCache.put(player, inventory);
    }

    public ItemStack[] getInventory(Player player) {
        return inventoryCache.get(player);
    }

    public void clearInventory(Player player) {
        inventoryCache.remove(player);
    }
}
