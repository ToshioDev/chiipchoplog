package mc.nekocraft.gui;

import mc.nekocraft.managers.Combat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Map;

public class CombatInventoryGUI implements Listener {
    private Inventory gui;

    public CombatInventoryGUI() {
        this.gui = Bukkit.createInventory(new CombatInventoryHolder(), 54, "Active Combats");
    }

    public void open(Player player, Combat combat) {
        gui.clear();
        ItemStack damagerHead = createPlayerHead(combat.getDamager());
        ItemStack victimHead = createPlayerHead(combat.getVictim());
        gui.setItem(22, damagerHead); // Centered with a slot of distance
        gui.setItem(24, victimHead); // Centered with a slot of distance

        int assistantSlot = 29; // Start from the next row
        for (Map.Entry<Player, Double> entry : combat.getAssistants().entrySet()) {
            ItemStack assistantHead = createPlayerHead(entry.getKey());
            gui.setItem(assistantSlot, assistantHead);
            assistantSlot += 2; // Slot of distance
        }

        player.openInventory(gui);
    }

    private ItemStack createPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.YELLOW + player.getName());
        meta.setLore(Arrays.asList(
                ChatColor.GREEN + "Health: " + player.getHealth(),
                ChatColor.GREEN + "Food: " + player.getFoodLevel()
        ));
        head.setItemMeta(meta);
        return head;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CombatInventoryHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof CombatInventoryHolder) {
            event.setCancelled(true);
        }
    }

    private static class CombatInventoryHolder implements org.bukkit.inventory.InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
