package mc.nekocraft.placeholders;

import mc.nekocraft.managers.Combat;
import mc.nekocraft.managers.CombatManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class CombatLogPlaceholder extends PlaceholderExpansion {
    private CombatManager combatManager;

    public CombatLogPlaceholder(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @Override
    public String getIdentifier() {
        return "combatlog";
    }

    @Override
    public String getAuthor() {
        return "YourName";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        Combat combat = combatManager.getCombatByPlayer(player);
        if (combat == null) {
            return "";
        }

        switch (identifier) {
            case "damager":
                return combat.getDamager().getName();
            case "victim":
                return combat.getVictim().getName();
            case "killer":
                return combat.getKiller() != null ? combat.getKiller().getName() : "None";
            case "assistants":
                return combat.getAssistants().keySet().toString();
            case "last_damage_time":
                return String.valueOf(combat.getLastDamageTime());
            default:
                return "";
        }
    }
}
