package mc.nekocraft.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Combat {
    private UUID id;
    private Player damager;
    private Player victim;
    private Player killer;
    private Map<Player, Double> assistants;
    private long lastDamageTime;

    public Combat(Player damager, Player victim) {
        this.id = UUID.randomUUID();
        this.damager = damager;
        this.victim = victim;
        this.assistants = new HashMap<>();
        this.lastDamageTime = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Player getDamager() {
        return damager;
    }

    public Player getVictim() {
        return victim;
    }

    public Player getKiller() {
        return killer;
    }

    public void setKiller(Player killer) {
        this.killer = killer;
    }

    public Map<Player, Double> getAssistants() {
        return assistants;
    }

    public long getLastDamageTime() {
        return lastDamageTime;
    }

    public void updateLastDamageTime() {
        this.lastDamageTime = System.currentTimeMillis();
    }

    public void addAssistant(Player assistant, double damage) {
        assistants.put(assistant, assistants.getOrDefault(assistant, 0.0) + damage);
    }
}
