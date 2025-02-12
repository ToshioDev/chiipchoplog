package mc.nekocraft;

import mc.nekocraft.placeholders.CombatLogPlaceholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import mc.nekocraft.managers.ItemManager;
import mc.nekocraft.managers.CombatManager;
import mc.nekocraft.managers.InventoryCache;
import mc.nekocraft.commands.CombatLogCommand;
import mc.nekocraft.gui.CombatInventoryGUI;
import mc.nekocraft.listeners.DeathListener;
import mc.nekocraft.listeners.CombatListener;

import java.io.File;

public class Main extends JavaPlugin {
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("CombatLog plugin enabled!");
        saveDefaultConfig();
        loadMessages();

        ItemManager itemManager = new ItemManager(this);
        InventoryCache inventoryCache = new InventoryCache();
        long combatCooldown = getConfig().getLong("combat-cooldown", 20000);
        CombatManager combatManager = new CombatManager(this, itemManager, inventoryCache, combatCooldown, messages);
        CombatInventoryGUI combatInventoryGUI = new CombatInventoryGUI();
        getServer().getPluginManager().registerEvents(combatInventoryGUI, this);
        getServer().getPluginManager().registerEvents(new DeathListener(messages, combatManager), this);
        getServer().getPluginManager().registerEvents(new CombatListener(combatManager), this);
        getCommand("combatlog").setExecutor(new CombatLogCommand(combatManager, itemManager, inventoryCache, messages));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CombatLogPlaceholder(combatManager).register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("CombatLog plugin disabled!");
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
}