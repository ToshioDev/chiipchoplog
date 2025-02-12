package mc.nekocraft.commands;

import mc.nekocraft.gui.CombatInventoryGUI;
import mc.nekocraft.managers.CombatManager;
import mc.nekocraft.managers.Combat;
import mc.nekocraft.managers.ItemManager;
import mc.nekocraft.managers.InventoryCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CombatLogCommand implements CommandExecutor {
    private CombatManager combatManager;
    private ItemManager itemManager;
    private CombatInventoryGUI combatInventoryGUI;
    private InventoryCache inventoryCache;
    private FileConfiguration messages;

    public CombatLogCommand(CombatManager combatManager, ItemManager itemManager, InventoryCache inventoryCache, FileConfiguration messages) {
        this.combatManager = combatManager;
        this.itemManager = itemManager;
        this.combatInventoryGUI = new CombatInventoryGUI();
        this.inventoryCache = inventoryCache;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("combatlog.list")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "list":
                        listActiveCombats(sender);
                        break;
                    case "gui":
                        if (args.length == 2) {
                            openCombatGUI(sender, args[1]);
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("usage")));
                        }
                        break;
                    case "recover":
                        if (args.length == 2) {
                            recoverInventory(sender, args[1]);
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("usage")));
                        }
                        break;
                    default:
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("usage")));
                        break;
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("usage")));
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("no-permission")));
            return false;
        }
    }

    private void listActiveCombats(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Active Combats:");
        for (Combat combat : combatManager.getActiveCombats()) {
            sender.sendMessage(ChatColor.YELLOW + "Combat ID: " + combat.getId());
            sender.sendMessage(ChatColor.YELLOW + "Damager: " + combat.getDamager().getName());
            sender.sendMessage(ChatColor.YELLOW + "Victim: " + combat.getVictim().getName());
            sender.sendMessage(ChatColor.YELLOW + "Assistants: " + combat.getAssistants().keySet());
        }
    }

    private void openCombatGUI(CommandSender sender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            Combat combat = combatManager.getCombatByPlayer(player);
            if (combat != null && sender instanceof Player) {
                combatInventoryGUI.open((Player) sender, combat);
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("combat-not-found").replace("{player}", playerName)));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("player-not-found").replace("{player}", playerName)));
        }
    }

    private void recoverInventory(CommandSender sender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            if (!combatManager.isInCombat(player)) {
                ItemStack[] inventory = inventoryCache.getInventory(player);
                if (inventory != null) {
                    player.getInventory().setContents(inventory);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("combat-recover").replace("{player}", player.getName())));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("combat-no-inventory").replace("{player}", player.getName())));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("combat-in-combat").replace("{player}", player.getName())));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("player-not-found").replace("{player}", playerName)));
        }
    }
}
